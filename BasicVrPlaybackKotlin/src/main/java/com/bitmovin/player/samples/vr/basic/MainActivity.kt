package com.bitmovin.player.samples.vr.basic

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bitmovin.player.BitmovinPlayer
import com.bitmovin.player.config.media.SourceConfiguration
import com.bitmovin.player.config.media.SourceItem
import com.bitmovin.player.config.vr.VRContentType
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var bitmovinPlayer: BitmovinPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.bitmovinPlayer = bitmovinPlayerView.player

        // Enabling the gyroscopic controlling for the 360° video
        this.bitmovinPlayer?.enableGyroscope()

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
        // Create a new source configuration
        val sourceConfiguration = SourceConfiguration()

        // Create a new SourceItem
        val vrSourceItem = SourceItem("https://bitmovin-a.akamaihd.net/content/playhouse-vr/mpds/105560.mpd")

        // Get the current VRConfiguration of the SourceItem
        val vrConfiguration = vrSourceItem.vrConfiguration
        // Set the VrContentType on the VRConfiguration
        vrConfiguration.vrContentType = VRContentType.SINGLE
        // Set the start position to 180 degrees
        vrConfiguration.startPosition = 180.0

        // Add a the SourceItem to the SourceConfiguration
        sourceConfiguration.addSourceItem(vrSourceItem)

        // load source using the created source configuration
        this.bitmovinPlayer?.load(sourceConfiguration)
    }
}
