package com.bitmovin.player.samples.fullscreen.basic

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bitmovin.player.BitmovinPlayer
import com.bitmovin.player.config.media.SourceItem
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var bitmovinPlayer: BitmovinPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        this.bitmovinPlayer = bitmovinPlayerView.player

        // Instantiate a custom FullscreenHandler
        val customFullscreenHandler = CustomFullscreenHandler(this, bitmovinPlayerView, toolbar)
        // Set the FullscreenHandler to the BitmovinPlayerView
        this.bitmovinPlayerView.setFullscreenHandler(customFullscreenHandler)

        this.initializePlayer()
    }

    override fun onStart() {
        super.onStart()
        bitmovinPlayerView.onStart()
    }

    override fun onResume() {
        super.onResume()
        bitmovinPlayerView.onResume()
    }

    override fun onPause() {
        bitmovinPlayerView.onPause()
        super.onPause()
    }

    override fun onStop() {
        bitmovinPlayerView.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        bitmovinPlayerView.onDestroy()
        super.onDestroy()
    }

    private fun initializePlayer() {
        // load source using a source item
        this.bitmovinPlayer?.load(SourceItem("https://bitdash-a.akamaihd.net/content/sintel/sintel.mpd"))
    }

}
