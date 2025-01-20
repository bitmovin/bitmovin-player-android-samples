package com.bitmovin.player.samples.pip.basic

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bitmovin.player.api.Player
import com.bitmovin.player.api.source.SourceConfig
import com.bitmovin.player.api.source.SourceType
import com.bitmovin.player.samples.pip.basic.databinding.ActivityMainBinding
import com.bitmovin.player.ui.DefaultPictureInPictureHandler

class MainActivity : AppCompatActivity() {

    private lateinit var bitmovinPlayer: Player
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bitmovinPlayer = binding.bitmovinPlayerView.player!!

        // Create a PictureInPictureHandler and set it on the BitmovinPlayerView
        val pictureInPictureHandler = DefaultPictureInPictureHandler(this, bitmovinPlayer)
        binding.bitmovinPlayerView.setPictureInPictureHandler(pictureInPictureHandler)

        initializePlayer()
    }

    override fun onStart() {
        binding.bitmovinPlayerView.onStart()
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.bitmovinPlayerView.onResume()
    }

    override fun onPause() {
        binding.bitmovinPlayerView.onPause()
        super.onPause()
    }

    override fun onStop() {
        binding.bitmovinPlayerView.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        binding.bitmovinPlayerView.onDestroy()
        super.onDestroy()
    }

    private fun initializePlayer() {
        // load source using a source item
        bitmovinPlayer.load(
            SourceConfig(
                url = "https://cdn.bitmovin.com/content/assets/sintel/sintel.mpd",
                type = SourceType.Dash
            )
        )
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration,
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)

        // Hiding the ActionBar
        if (isInPictureInPictureMode) {
            supportActionBar?.hide()
        } else {
            supportActionBar?.show()
        }
        binding.bitmovinPlayerView.onPictureInPictureModeChanged(
            isInPictureInPictureMode,
            newConfig
        )
    }
}
