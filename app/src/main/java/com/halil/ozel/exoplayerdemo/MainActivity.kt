package com.halil.ozel.exoplayerdemo

import android.app.Activity
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.ui.PlayerView
import com.halil.ozel.exoplayerdemo.databinding.ActivityMainBinding

class MainActivity : Activity() {

    private lateinit var binding: ActivityMainBinding
    private var exoPlayer: ExoPlayer? = null
    private var playbackPosition = 0L
    private var playWhenReady = true

    private val CUSTOM_FAST_FORWARD_INCREMENT = 15000L
    private val CUSTOM_REWIND_INCREMENT = 15000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preparePlayer()
        setupCustomControls()
    }

    private fun preparePlayer() {
        exoPlayer = SimpleExoPlayer.Builder(this).build()
        exoPlayer?.playWhenReady = true
        binding.playerView.player = exoPlayer

        val defaultHttpDataSourceFactory = DefaultHttpDataSource.Factory()
        val mediaItem = MediaItem.fromUri(Uri.parse(URL))
        val mediaSource = if (URL.endsWith(".m3u8")) {
            HlsMediaSource.Factory(defaultHttpDataSourceFactory).createMediaSource(mediaItem)
        } else {
            ProgressiveMediaSource.Factory(defaultHttpDataSourceFactory).createMediaSource(mediaItem)
        }

        exoPlayer?.apply {
            setMediaSource(mediaSource)
            seekTo(playbackPosition)
            playWhenReady = playWhenReady
            prepare()
        }
    }

    private fun setupCustomControls() {
        val playPauseButton = binding.playerView.findViewById<ImageButton>(R.id.play_pause_button)

        playPauseButton.setOnClickListener {
            exoPlayer?.let { player ->
                if (player.isPlaying) {
                    player.pause()
                    playPauseButton.setImageResource(R.drawable.baseline_forward_10_24) // Set the play icon
                } else {
                    player.play()
                    playPauseButton.setImageResource(R.drawable.baseline_forward_10_24 ) // Set the pause icon
                }
            }
        }

        // Fast forward button
        binding.playerView.findViewById<ImageButton>(R.id.custom_fast_forward_button)?.setOnClickListener {
            exoPlayer?.let { player ->
                player.seekTo(player.currentPosition + CUSTOM_FAST_FORWARD_INCREMENT)
            }
        }

        // Rewind button
        binding.playerView.findViewById<ImageButton>(R.id.custom_rewind_button)?.setOnClickListener {
            exoPlayer?.let { player ->
                player.seekTo(player.currentPosition - CUSTOM_REWIND_INCREMENT)
            }
        }



        // Rotate screen button
        val rotateScreenButton = binding.playerView.findViewById<ImageButton>(R.id.rotate_screen_button)
        rotateScreenButton.setOnClickListener {
            requestedOrientation = if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            } else {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        }
    }


    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    override fun onPause() {
        super.onPause()
        releasePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    private fun releasePlayer() {
        exoPlayer?.let { player ->
            playbackPosition = player.currentPosition
            playWhenReady = player.playWhenReady
            player.release()
            exoPlayer = null
        }
    }

    companion object {
        private const val URL = "https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8"
        //private const val URL = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
    }
}
