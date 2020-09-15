package com.bitmovin.player.samples.pip.basic

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bitmovin.player.BitmovinPlayer
import com.bitmovin.player.api.event.listener.OnPictureInPictureEnterListener
import com.bitmovin.player.config.media.SourceItem
import com.bitmovin.player.ui.DefaultPictureInPictureHandler
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var bitmovinPlayer: BitmovinPlayer? = null
    private var playerShouldPause = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.bitmovinPlayer = bitmovinPlayerView.player

        // Create a PictureInPictureHandler and set it on the BitmovinPlayerView
        val pictureInPictureHandler = DefaultPictureInPictureHandler(this, this.bitmovinPlayer)
        bitmovinPlayerView.setPictureInPictureHandler(pictureInPictureHandler)

        this.initializePlayer()
    }

    override fun onStart() {
        bitmovinPlayerView.onStart()
        super.onStart()
    }

    override fun onResume() {
        super.onResume()

        // Add the PictureInPictureEnterListener to the BitmovinPlayerView
        bitmovinPlayerView.addEventListener(this.pipEnterListener)

        bitmovinPlayerView.onResume()
    }

    override fun onPause() {
        if (this.playerShouldPause) {
            bitmovinPlayerView.onPause()
        }
        this.playerShouldPause = true

        bitmovinPlayerView.removeEventListener(this.pipEnterListener)

        super.onPause()
    }

    override fun onDestroy() {
        bitmovinPlayerView.onDestroy()
        super.onDestroy()
    }

    private fun initializePlayer() {
        // load source using a source item
        this.bitmovinPlayer?.load(SourceItem("https://bitdash-a.akamaihd.net/content/sintel/sintel.mpd"))
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)

        // Hiding the ActionBar
        if (isInPictureInPictureMode) {
            this.supportActionBar?.hide()
        } else {
            this.supportActionBar?.show()
        }
        this.bitmovinPlayerView.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
    }

    private val pipEnterListener = OnPictureInPictureEnterListener {
        // Android fires an onPause on the Activity when entering PiP mode.
        // However, we do not want the BitmovinPlayerView to act on this.
        this@MainActivity.playerShouldPause = false
    }
}
