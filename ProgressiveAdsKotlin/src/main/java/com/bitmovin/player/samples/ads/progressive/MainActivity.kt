package com.bitmovin.player.samples.ads.progressive

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.LinearLayout
import com.bitmovin.player.BitmovinPlayerView
import com.bitmovin.player.config.PlayerConfiguration
import com.bitmovin.player.config.advertising.AdItem
import com.bitmovin.player.config.advertising.AdSource
import com.bitmovin.player.config.advertising.AdSourceType
import com.bitmovin.player.config.advertising.AdvertisingConfiguration
import com.bitmovin.player.config.media.SourceConfiguration
import kotlinx.android.synthetic.main.activity_main.*

private const val AD_SOURCE_1 = "https://bitmovin-a.akamaihd.net/content/testing/ads/testad2s.mp4"
private const val AD_SOURCE_2 = "file:///android_asset/testad2s.mp4"

class MainActivity : AppCompatActivity() {
    private lateinit var bitmovinPlayerView: BitmovinPlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Create a new source configuration
        val sourceConfig = SourceConfiguration().apply {
            // Add a new source item
            addSourceItem("https://bitdash-a.akamaihd.net/content/sintel/sintel.mpd")
        }

        // Create AdSources
        val firstAdSource = AdSource(AdSourceType.PROGRESSIVE, AD_SOURCE_1)
        val secondAdSource = AdSource(AdSourceType.PROGRESSIVE, AD_SOURCE_2)

        // Setup a pre-roll ad
        val preRoll = AdItem("pre", firstAdSource)
        // Setup a mid-roll ad at 10% of the content duration
        val midRoll = AdItem("10%", secondAdSource)

        // Add the AdItems to the AdvertisingConfiguration
        val advertisingConfig = AdvertisingConfiguration(preRoll, midRoll)

        // Creating a new PlayerConfiguration
        val playerConfiguration = PlayerConfiguration().apply {
            sourceConfiguration = sourceConfig
            // All ads in the AdvertisingConfiguration will be scheduled automatically
            advertisingConfiguration = advertisingConfig
        }
        // Create new BitmovinPlayerView with our PlayerConfiguration
        bitmovinPlayerView = BitmovinPlayerView(this, playerConfiguration).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        }

        // Add BitmovinPlayerView to the layout
        root.addView(this.bitmovinPlayerView, 0)
    }

    override fun onStart() {
        super.onStart()
        this.bitmovinPlayerView.onStart()
    }

    override fun onResume() {
        super.onResume()
        this.bitmovinPlayerView.onResume()
    }

    override fun onPause() {
        this.bitmovinPlayerView.onPause()
        super.onPause()
    }

    override fun onStop() {
        this.bitmovinPlayerView.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        this.bitmovinPlayerView.onDestroy()
        super.onDestroy()
    }

}
