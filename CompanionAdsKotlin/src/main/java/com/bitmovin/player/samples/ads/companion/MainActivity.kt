package com.bitmovin.player.samples.ads.companion

import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.bitmovin.player.BitmovinPlayer
import com.bitmovin.player.BitmovinPlayerView
import com.bitmovin.player.config.PlayerConfiguration
import com.bitmovin.player.config.advertising.*
import com.bitmovin.player.config.media.SourceConfiguration

class MainActivity : AppCompatActivity() {

    private lateinit var bitmovinPlayerView: BitmovinPlayerView
    private lateinit var bitmovinPlayer: BitmovinPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        val companionAdContainerView = findViewById<FrameLayout>(R.id.companionAdContainer)
        bitmovinPlayerView = findViewById(R.id.bitmovinPlayerView)
        bitmovinPlayer = bitmovinPlayerView.player!!


        // Setup companion ad container
        val companionAdContainer = CompanionAdContainer(companionAdContainerView, 300, 250)
        val adItem = AdItem("pre", AdSource(AdSourceType.IMA, AD_TAG))

        val advertisingConfiguration = AdvertisingConfiguration(listOf(companionAdContainer), adItem)

        // Finish setup of the player
        val playerConfiguration = PlayerConfiguration().apply {
            this.advertisingConfiguration = advertisingConfiguration
            sourceConfiguration = SourceConfiguration().apply {
                addSourceItem("https://bitdash-a.akamaihd.net/content/sintel/sintel.mpd")
            }
        }
        this.bitmovinPlayer.setup(playerConfiguration);
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

private const val AD_TAG = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dlinear&correlator=";
