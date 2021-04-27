package com.bitmovin.player.samples.ads.companion

import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.bitmovin.player.PlayerView
import com.bitmovin.player.api.Player
import com.bitmovin.player.api.PlayerConfig
import com.bitmovin.player.api.advertising.*
import com.bitmovin.player.api.source.SourceConfig
import com.bitmovin.player.api.source.SourceType

class MainActivity : AppCompatActivity() {
    private lateinit var playerView: PlayerView
    private lateinit var player: Player

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        val companionAdContainerView = findViewById<FrameLayout>(R.id.companionAdContainer)
        playerView = findViewById(R.id.bitmovinPlayerView)


        // Setup companion ad container
        val playerConfig = PlayerConfig(
                advertisingConfig = AdvertisingConfig(
                        listOf(CompanionAdContainer(companionAdContainerView, 300, 250)),
                        AdItem("pre", AdSource(AdSourceType.Ima, AD_TAG))
                )
        )

        player = Player.create(this, playerConfig).also {
            playerView.player = it
        }

        player.load(SourceConfig("https://bitdash-a.akamaihd.net/content/sintel/sintel.mpd", SourceType.Dash))
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

private const val AD_TAG = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dlinear&correlator=";
