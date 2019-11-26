package com.bitmovin.player.samples.custom.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import com.bitmovin.player.config.PlayerConfiguration
import com.bitmovin.player.config.StyleConfiguration
import com.bitmovin.player.config.media.SourceConfiguration
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var playerUi: PlayerUI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Create new StyleConfiguration
        val styleConfiguration = StyleConfiguration()
        // Disable UI
        styleConfiguration.isUiEnabled = false

        // Create a new source configuration
        val sourceConfiguration = SourceConfiguration()
        // Add a new source item
        sourceConfiguration.addSourceItem("https://bitdash-a.akamaihd.net/content/sintel/sintel.mpd")

        // Creating a new PlayerConfiguration
        val playerConfiguration = PlayerConfiguration()
        // Assign created StyleConfiguration to the PlayerConfiguration
        playerConfiguration.styleConfiguration = styleConfiguration
        // Assign created SourceConfiguration to the PlayerConfiguration
        playerConfiguration.sourceConfiguration = sourceConfiguration

        this.playerUi = PlayerUI(this, playerConfiguration)
        val fullscreenHandler = CustomFullscreenHandler(this, this.playerUi)

        // Set the FullscreenHandler of the PlayerUI
        this.playerUi.setFullscreenHandler(fullscreenHandler)

        this.playerUi.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        rootView.addView(this.playerUi)
    }

    override fun onStart() {
        super.onStart()
        this.playerUi.onStart()
    }

    override fun onResume() {
        super.onResume()
        this.playerUi.onResume()
    }

    override fun onPause() {
        this.playerUi.onPause()
        super.onPause()
    }

    override fun onStop() {
        this.playerUi.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        this.playerUi.onDestroy()
        super.onDestroy()
    }
}
