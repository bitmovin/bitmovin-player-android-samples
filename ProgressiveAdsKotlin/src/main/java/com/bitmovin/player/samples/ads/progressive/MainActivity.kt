package com.bitmovin.player.samples.ads.progressive

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.bitmovin.analytics.api.AnalyticsConfig
import com.bitmovin.player.PlayerView
import com.bitmovin.player.api.Player
import com.bitmovin.player.api.PlayerConfig
import com.bitmovin.player.api.advertising.AdItem
import com.bitmovin.player.api.advertising.AdSource
import com.bitmovin.player.api.advertising.AdSourceType
import com.bitmovin.player.api.advertising.AdvertisingConfig
import com.bitmovin.player.api.analytics.create
import com.bitmovin.player.api.source.SourceConfig
import com.bitmovin.player.api.source.SourceType
import com.bitmovin.player.samples.ads.progressive.databinding.ActivityMainBinding

private const val AD_SOURCE_1 = "https://bitmovin-a.akamaihd.net/content/testing/ads/testad2s.mp4"
private const val AD_SOURCE_2 = "file:///android_asset/testad2s.mp4"

class MainActivity : AppCompatActivity() {
    private lateinit var playerView: PlayerView
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Create AdSources
        val firstAdSource = AdSource(AdSourceType.Progressive, AD_SOURCE_1)
        val secondAdSource = AdSource(AdSourceType.Progressive, AD_SOURCE_2)

        // Setup a pre-roll ad
        val preRoll = AdItem("pre", firstAdSource)
        // Setup a mid-roll ad at 10% of the content duration
        val midRoll = AdItem("10%", secondAdSource)

        // Add the AdItems to the AdvertisingConfig
        val advertisingConfig = AdvertisingConfig(preRoll, midRoll)

        // Creating a new PlayerConfig
        // All ads in the AdvertisingConfig will be scheduled automatically
        val playerConfig = PlayerConfig(advertisingConfig = advertisingConfig)

        // Create a new Player instance with a PlayerConfig used to instantiate the PlayerView
        val analyticsKey = "{ANALYTICS_LICENSE_KEY}"
        val player = Player.create(this, playerConfig, AnalyticsConfig(analyticsKey))
        playerView = PlayerView(this, player).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
            keepScreenOn = true
        }
        // Load the SourceItem
        playerView.player?.load(SourceConfig("https://bitmovin-a.akamaihd.net/content/sintel/sintel.mpd", SourceType.Dash))

        // Add PlayerView to the layout
        binding.root.addView(playerView, 0)
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
