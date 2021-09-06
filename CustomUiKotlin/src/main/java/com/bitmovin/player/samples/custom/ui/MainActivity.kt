package com.bitmovin.player.samples.custom.ui

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.bitmovin.player.api.PlayerConfig
import com.bitmovin.player.api.source.Source
import com.bitmovin.player.api.source.SourceConfig
import com.bitmovin.player.api.ui.StyleConfig
import com.bitmovin.player.samples.custom.ui.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var playerUi: PlayerUI
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Create a new PlayerConfig containing a StyleConfig with disabled UI
        val playerConfig = PlayerConfig(styleConfig = StyleConfig(isUiEnabled = false))

        playerUi = PlayerUI(this, playerConfig)
        val fullscreenHandler = CustomFullscreenHandler(this, playerUi)

        // Set the FullscreenHandler of the PlayerUI
        playerUi.setFullscreenHandler(fullscreenHandler)

        playerUi.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        // Load the Source into the player
        playerUi.load(Source.create(SourceConfig.fromUrl("https://bitdash-a.akamaihd.net/content/sintel/sintel.mpd")))

        binding.rootView.addView(playerUi)
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
