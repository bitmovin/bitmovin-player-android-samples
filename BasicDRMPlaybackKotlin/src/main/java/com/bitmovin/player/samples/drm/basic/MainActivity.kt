package com.bitmovin.player.samples.drm.basic

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bitmovin.player.BitmovinPlayer
import com.bitmovin.player.config.drm.DRMSystems
import com.bitmovin.player.config.media.SourceItem
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var bitmovinPlayer: BitmovinPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bitmovinPlayer = bitmovinPlayerView.player
        initializePlayer()
    }

    override fun onStart() {
        bitmovinPlayerView.onStart()
        super.onStart()
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
        // Create a new source item
        val sourceItem = SourceItem("https://bitmovin-a.akamaihd.net/content/art-of-motion_drm/mpds/11331.mpd")

        // setup DRM handling
        val drmLicenseUrl = "https://widevine-proxy.appspot.com/proxy"
        val drmSchemeUuid = DRMSystems.WIDEVINE_UUID
        sourceItem.addDRMConfiguration(drmSchemeUuid, drmLicenseUrl)

        // load source using the created source item
        bitmovinPlayer?.load(sourceItem)
    }
}
