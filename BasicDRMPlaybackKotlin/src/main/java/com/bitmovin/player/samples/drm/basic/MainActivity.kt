package com.bitmovin.player.samples.drm.basic

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bitmovin.player.api.Player
import com.bitmovin.player.api.source.SourceConfig
import com.bitmovin.player.api.drm.WidevineConfig
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var player: Player? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        player = playerView.player
        initializePlayer()
    }

    override fun onStart() {
        playerView.onStart()
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        playerView.onResume()
    }

    override fun onPause() {
        playerView.onPause()
        super.onPause()
    }

    override fun onStop() {
        playerView.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        playerView.onDestroy()
        super.onDestroy()
    }

    private fun initializePlayer() {
        // Create a new source config
        val sourceConfig = SourceConfig.fromUrl("https://bitmovin-a.akamaihd.net/content/art-of-motion_drm/mpds/11331.mpd")

        // Attach DRM handling to the source config
        sourceConfig.drmConfig = WidevineConfig("https://widevine-proxy.appspot.com/proxy")

        // Load the source
        player?.load(sourceConfig)
    }
}
