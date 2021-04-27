package com.bitmovin.player.samples.pip.basic

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bitmovin.player.api.Player
import com.bitmovin.player.api.event.PlayerEvent
import com.bitmovin.player.api.event.on
import com.bitmovin.player.api.source.SourceConfig
import com.bitmovin.player.api.source.SourceType
import com.bitmovin.player.ui.DefaultPictureInPictureHandler
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var bitmovinPlayer: Player
    private var playerShouldPause = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bitmovinPlayer = bitmovinPlayerView.player!!

        // Create a PictureInPictureHandler and set it on the BitmovinPlayerView
        val pictureInPictureHandler = DefaultPictureInPictureHandler(this, bitmovinPlayer)
        bitmovinPlayerView.setPictureInPictureHandler(pictureInPictureHandler)

        initializePlayer()
    }

    override fun onStart() {
        bitmovinPlayerView.onStart()
        super.onStart()
    }

    override fun onResume() {
        super.onResume()

        // Add the PictureInPictureEnterListener to the PlayerView
        bitmovinPlayerView.on(::onPipEnter)

        bitmovinPlayerView.onResume()
    }

    override fun onPause() {
        if (playerShouldPause) {
            bitmovinPlayerView.onPause()
        }
        playerShouldPause = true

        bitmovinPlayerView.off(::onPipEnter)

        super.onPause()
    }

    override fun onDestroy() {
        bitmovinPlayerView.onDestroy()
        super.onDestroy()
    }

    private fun initializePlayer() {
        // load source using a source item
        bitmovinPlayer.load(
                SourceConfig(
                        url = "https://bitdash-a.akamaihd.net/content/sintel/sintel.mpd",
                        type = SourceType.Dash
                )
        )
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)

        // Hiding the ActionBar
        if (isInPictureInPictureMode) {
            supportActionBar?.hide()
        } else {
            supportActionBar?.show()
        }
        bitmovinPlayerView.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
    }

    private fun onPipEnter(event: PlayerEvent.PictureInPictureEnter) {
        // Android fires an onPause on the Activity when entering PiP mode.
        // However, we do not want the BitmovinPlayerView to act on 
        this@MainActivity.playerShouldPause = false
    }
}
