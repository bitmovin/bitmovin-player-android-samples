package com.bitmovin.player.samples.custom.ui

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.bitmovin.player.api.PlayerConfig
import com.bitmovin.player.api.source.Source
import com.bitmovin.player.api.source.SourceConfig
import com.bitmovin.player.api.ui.StyleConfig
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var playerUi: PlayerUI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Create a new PlayerConfig containing a StyleConfig with disabled UI
        val playerConfig = PlayerConfig(styleConfig = StyleConfig(isUiEnabled = false))

        playerUi = PlayerUI(this, playerConfig)
        val fullscreenHandler = CustomFullscreenHandler(this, playerUi)

        // Set the FullscreenHandler of the PlayerUI
        playerUi.setFullscreenHandler(fullscreenHandler)

        playerUi.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        // Load the Source into the player
        playerUi.load(Source.create(SourceConfig.fromUrl("https://bitdash-a.akamaihd.net/content/sintel/sintel.mpd")))

        rootView.addView(playerUi)
    }

    override fun onStart() {
        super.onStart()
        playerUi.onStart()
    }

    override fun onResume() {
        super.onResume()
        playerUi.onResume()
    }

    override fun onPause() {
        playerUi.onPause()
        super.onPause()
    }

    override fun onStop() {
        playerUi.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        playerUi.onDestroy()
        super.onDestroy()
    }
}
