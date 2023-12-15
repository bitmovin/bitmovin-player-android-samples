package com.bitmovin.player.samples.custom.ui.subtitleview

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.bitmovin.analytics.api.AnalyticsConfig
import com.bitmovin.player.PlayerView
import com.bitmovin.player.SubtitleView
import com.bitmovin.player.api.Player
import com.bitmovin.player.api.PlayerConfig
import com.bitmovin.player.api.analytics.create
import com.bitmovin.player.api.source.SourceConfig
import com.bitmovin.player.api.source.SourceType
import com.bitmovin.player.api.ui.PlayerViewConfig
import com.bitmovin.player.api.ui.StyleConfig
import com.bitmovin.player.api.ui.UiConfig
import com.bitmovin.player.samples.custom.ui.subtitleview.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var playerView: PlayerView
    private lateinit var subtitleView: SubtitleView
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Creating a PlayerView and get it's Player instance.
        val analyticsKey = "{ANALYTICS_LICENSE_KEY}"
        val player = Player.create(this, PlayerConfig(), AnalyticsConfig(analyticsKey))

        // Disable default Bitmovin UI
        val viewConfig = PlayerViewConfig(uiConfig = UiConfig.Disabled)
        playerView = PlayerView(this, player, viewConfig)
            .apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                keepScreenOn = true
            }
        player.load(SourceConfig("https://bitmovin-a.akamaihd.net/content/sintel/sintel.mpd", SourceType.Dash))

        // Creating a SubtitleView and assign the current player instance.
        subtitleView = SubtitleView(this)
        subtitleView.setPlayer(player)

        // Setup minimalistic controls for the player
        binding.playerControls.setPlayer(player)

        // Add the SubtitleView to the layout
        binding.playerContainer.addView(subtitleView)

        // Add the PlayerView to the layout as first position (so it is the behind the SubtitleView)
        binding.playerContainer.addView(playerView, 0)
    }

    override fun onStart() {
        super.onStart()
        playerView.onStart()
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
}
