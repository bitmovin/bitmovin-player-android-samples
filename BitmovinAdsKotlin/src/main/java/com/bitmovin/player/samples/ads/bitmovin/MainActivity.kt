package com.bitmovin.player.samples.ads.bitmovin

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
import com.bitmovin.player.api.analytics.AnalyticsPlayerConfig
import com.bitmovin.player.api.source.SourceConfig
import com.bitmovin.player.samples.ads.bitmovin.databinding.ActivityMainBinding

private const val BITMOVIN_AD = "https://cdn.bitmovin.com/content/player/advertising/bitmovin-ad.xml"
private const val NO_RESPONSE_AD = "https://this-url-doesnt-exist/ad.xml"
// These are Sample Tags from https://developers.google.com/interactive-media-ads/docs/sdks/android/tags
private const val SINGLE_REDIRECT_LINEAR_AD = "https://pubads.g.doubleclick.net/gampad/ads?iu=/21775744923/external/single_ad_samples&sz=640x480&cust_params=sample_ct%3Dredirectlinear&ciu_szs=300x250%2C728x90&gdfp_req=1&output=vast&unviewed_position_start=1&env=vp&impl=s&correlator="
private const val SINGLE_SKIPPABLE_INLINE_AD = "https://pubads.g.doubleclick.net/gampad/ads?iu=/21775744923/external/single_preroll_skippable&sz=640x480&ciu_szs=300x250%2C728x90&gdfp_req=1&output=vast&unviewed_position_start=1&env=vp&impl=s&correlator="

class MainActivity : AppCompatActivity() {
    private lateinit var playerView: PlayerView
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Create AdSources
        val bitmovinAd = AdSource(AdSourceType.Bitmovin, BITMOVIN_AD)
        val noResponseAd = AdSource(AdSourceType.Bitmovin, NO_RESPONSE_AD)
        val redirectLinearAd = AdSource(AdSourceType.Bitmovin, SINGLE_REDIRECT_LINEAR_AD)
        val skippableInlineAd = AdSource(AdSourceType.Bitmovin, SINGLE_SKIPPABLE_INLINE_AD)

        // Set up a pre-roll ad
        val preRoll = AdItem("pre", skippableInlineAd)

        // Set up a mid-roll waterfalling ad at 10% of the content duration
        // NOTE: AdItems containing more than one AdSource, will be executed as waterfalling ad
        val midRoll = AdItem("10%", noResponseAd, bitmovinAd)

        // Set up a post-roll ad
        val postRoll = AdItem("post", redirectLinearAd)

        // Add the AdItems to the AdvertisingConfig
        val advertisingConfig = AdvertisingConfig(preRoll, midRoll, postRoll)

        // Create a new PlayerConfig containing the advertising config. Ads in the AdvertisingConfig will be scheduled automatically.
        val playerConfig = PlayerConfig(advertisingConfig = advertisingConfig)

        // Create new Player with our PlayerConfig
        val analyticsKey = "{ANALYTICS_LICENSE_KEY}"
        val player = Player(
            this,
            playerConfig,
            AnalyticsPlayerConfig.Enabled(AnalyticsConfig(analyticsKey)),
        )
        playerView = PlayerView(this, player).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }
        playerView.keepScreenOn = true
        player.load(SourceConfig.fromUrl("https://cdn.bitmovin.com/content/assets/sintel/sintel.mpd"))

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
