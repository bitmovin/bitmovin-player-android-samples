package com.bitmovin.player.samples.integration.adobeanalytics

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.bitmovin.player.BitmovinPlayer
import com.bitmovin.player.BitmovinPlayerView
import com.bitmovin.player.api.event.data.AdBreakStartedEvent
import com.bitmovin.player.api.event.data.AdStartedEvent
import com.bitmovin.player.config.PlayerConfiguration
import com.bitmovin.player.config.StyleConfiguration
import com.bitmovin.player.config.advertising.AdItem
import com.bitmovin.player.config.advertising.AdSource
import com.bitmovin.player.config.advertising.AdSourceType
import com.bitmovin.player.config.advertising.AdvertisingConfiguration
import com.bitmovin.player.config.media.SourceConfiguration
import com.bitmovin.player.config.media.SourceItem
import com.bitmovin.player.integration.adobeanalytics.AdobeMediaAnalyticsDataOverride
import com.bitmovin.player.integration.adobeanalytics.AdobeMediaAnalyticsTracker
import java.util.*

class MainActivity : AppCompatActivity() {

    // These are IMA Sample Tags from https://developers.google.com/interactive-media-ads/docs/sdks/android/tags
    private val VMAP_SOURCE = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=vmap&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar%3Dpremidpostlongpod&cmsid=496&vid=short_tencue&correlator="
    private lateinit var bitmovinPlayerView: BitmovinPlayerView
    private lateinit var adobeAnalyticsTracker: AdobeMediaAnalyticsTracker
    private lateinit var customDataOverride: AdobeMediaAnalyticsDataOverride

    inner class CustomAdobeMediaDataOverride : AdobeMediaAnalyticsDataOverride() {
        private var activeAdBreakPosition = 0L
        private var activeAdPosition = 0L
        override fun getMediaContextData(player: BitmovinPlayer): HashMap<String, String> {
            return hashMapOf(
                    "os" to "Android",
                    "version" to "9.0"
            )
        }

         // Provide a media name
        override fun getMediaName(player: BitmovinPlayer, activeSourceItem: SourceItem) = activeSourceItem.title

        // return appropriate value for media id
        override fun getMediaUid(player: BitmovinPlayer, activeSourceItem: SourceItem) = activeSourceItem.dashSource.url

        override fun getAdBreakId(player: BitmovinPlayer, event: AdBreakStartedEvent): String {
            // reset ad position in adBreak when receiving new adBreak event
            activeAdPosition = 0L

            // return appropriate value for adBreak id
            return event.adBreak?.id ?: "unknown"
        }

        override fun getAdBreakPosition(player: BitmovinPlayer, event: AdBreakStartedEvent): Long {
            // reset ad position in adBreak when receiving new adBreak event
            activeAdPosition = 0L

            // return position of AdBreak in the content playback
            val scheduledTime = event.adBreak?.scheduleTime
            when (scheduledTime) {
                0.0 -> activeAdBreakPosition = 1 // preroll adBreak
                player.duration -> activeAdBreakPosition++ // postroll adBreak
                else -> {
                    activeAdBreakPosition++ // midroll adBreak
                }
            }
            return activeAdBreakPosition
        }

        override fun getAdName(player: BitmovinPlayer, event: AdStartedEvent) = event.ad?.mediaFileUrl ?: "unknown"

        override fun getAdId(player: BitmovinPlayer, event: AdStartedEvent) =  event.ad?.id ?: "unknown"

        override fun getAdPosition(player: BitmovinPlayer, event: AdStartedEvent): Long {
            // return position of Ad in Ad break
            return ++activeAdPosition
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Create a new source configuration
        val sourceConfiguration = SourceConfiguration()
        // Add a new source item
        val sourceItem = SourceItem("https://bitmovin-a.akamaihd.net/content/MI201109210084_1/mpds/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.mpd")
        sourceItem.title = "Art of Motion"
        sourceConfiguration.addSourceItem(sourceItem)

        // Create AdSources
        val vmapAdSource = AdSource(AdSourceType.IMA, VMAP_SOURCE)

        // Setup a post-roll ad
        val vmap = AdItem("", vmapAdSource)

        // Add the AdItems to the AdvertisingConfiguration
        val advertisingConfiguration = AdvertisingConfiguration(vmap)
        val styleConfiguration = StyleConfiguration().apply {
            isHideFirstFrame = true
        }

        // Creating a new PlayerConfiguration
        val playerConfiguration = PlayerConfiguration()
        // Assign created SourceConfiguration to the PlayerConfiguration
        playerConfiguration.sourceConfiguration = sourceConfiguration
        // Assigning the AdvertisingConfiguration to the PlayerConfiguration
        // All ads in the AdvertisingConfiguration will be scheduled automatically
        playerConfiguration.advertisingConfiguration = advertisingConfiguration
        playerConfiguration.styleConfiguration = styleConfiguration

        // Create new BitmovinPlayerView with our PlayerConfiguration
        bitmovinPlayerView = BitmovinPlayerView(this, playerConfiguration)
        bitmovinPlayerView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        val rootView = findViewById<View>(R.id.activity_main) as LinearLayout

        // Add BitmovinPlayerView to the layout
        rootView.addView(bitmovinPlayerView, 0)

        // setup and start Adobe Media analytics tracker
        adobeAnalyticsTracker = AdobeMediaAnalyticsTracker()
        customDataOverride = CustomAdobeMediaDataOverride()
        adobeAnalyticsTracker.createTracker(bitmovinPlayerView.player, customDataOverride)
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
        // destroy Adobe Media analytics tracker
        adobeAnalyticsTracker.destroyTracker()
        bitmovinPlayerView.onDestroy()
        super.onDestroy()
    }

}
