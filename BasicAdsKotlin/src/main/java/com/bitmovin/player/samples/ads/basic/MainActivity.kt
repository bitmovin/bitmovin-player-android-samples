package com.bitmovin.player.samples.ads.basic

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.LinearLayout
import com.bitmovin.player.BitmovinPlayerView
import com.bitmovin.player.config.PlayerConfiguration
import com.bitmovin.player.config.advertising.AdItem
import com.bitmovin.player.config.advertising.AdSource
import com.bitmovin.player.config.advertising.AdSourceType
import com.bitmovin.player.config.advertising.AdvertisingConfiguration
import com.bitmovin.player.config.media.SourceItem
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    // These are IMA Sample Tags from https://developers.google.com/interactive-media-ads/docs/sdks/android/tags
    private val AD_SOURCE_1 = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dredirecterror&nofb=1&correlator="
    private val AD_SOURCE_2 = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dlinear&correlator="
    private val AD_SOURCE_3 = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dskippablelinear&correlator="
    private val AD_SOURCE_4 = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dredirectlinear&correlator="

    private var bitmovinPlayerView: BitmovinPlayerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Create AdSources
        val firstAdSource = AdSource(AdSourceType.IMA, AD_SOURCE_1)
        val secondAdSource = AdSource(AdSourceType.IMA, AD_SOURCE_2)
        val thirdAdSource = AdSource(AdSourceType.IMA, AD_SOURCE_3)
        val fourthAdSource = AdSource(AdSourceType.IMA, AD_SOURCE_4)

        // Setup a pre-roll ad
        val preRoll = AdItem("pre", thirdAdSource)
        // Setup a mid-roll waterfalling ad at 10% of the content duration
        // NOTE: AdItems containing more than one AdSource, will be executed as waterfalling ad
        val midRoll = AdItem("10%", firstAdSource, secondAdSource)
        // Setup a post-roll ad
        val postRoll = AdItem("post", fourthAdSource)

        // Add the AdItems to the AdvertisingConfiguration
        val advertisingConfiguration = AdvertisingConfiguration(preRoll, midRoll, postRoll)

        // Creating a new PlayerConfiguration
        val playerConfiguration = PlayerConfiguration()
        // Assign the AdvertisingConfiguration to the PlayerConfiguration
        // All ads in the AdvertisingConfiguration will be scheduled automatically
        playerConfiguration.advertisingConfiguration = advertisingConfiguration

        // Create new BitmovinPlayerView with our PlayerConfiguration
        this.bitmovinPlayerView = BitmovinPlayerView(this, playerConfiguration)
        this.bitmovinPlayerView?.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        this.bitmovinPlayerView?.player?.load(SourceItem("https://bitdash-a.akamaihd.net/content/sintel/sintel.mpd"))

        // Add BitmovinPlayerView to the layout
        root.addView(this.bitmovinPlayerView, 0)
    }

    override fun onStart() {
        super.onStart()
        this.bitmovinPlayerView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        this.bitmovinPlayerView?.onResume()
    }

    override fun onPause() {
        this.bitmovinPlayerView?.onPause()
        super.onPause()
    }

    override fun onStop() {
        this.bitmovinPlayerView?.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        this.bitmovinPlayerView?.onDestroy()
        super.onDestroy()
    }

}
