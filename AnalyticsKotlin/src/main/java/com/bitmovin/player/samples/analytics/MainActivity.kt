package com.bitmovin.player.samples.analytics

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bitmovin.analytics.api.AnalyticsConfig
import com.bitmovin.analytics.api.CustomData
import com.bitmovin.analytics.api.SourceMetadata
import com.bitmovin.player.api.Player
import com.bitmovin.player.api.PlayerConfig
import com.bitmovin.player.api.analytics.AnalyticsApi.Companion.analytics
import com.bitmovin.player.api.analytics.create
import com.bitmovin.player.api.source.Source
import com.bitmovin.player.api.source.SourceConfig
import com.bitmovin.player.samples.analytics.databinding.ActivityMainBinding

private const val Sintel = "https://bitdash-a.akamaihd.net/content/sintel/sintel.mpd"

class MainActivity : AppCompatActivity() {

    private lateinit var player: Player
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializePlayer()
    }

    override fun onStart() {
        super.onStart()
        binding.playerView.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.playerView.onResume()
    }

    override fun onPause() {
        binding.playerView.onPause()
        super.onPause()
    }

    override fun onStop() {
        binding.playerView.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        binding.playerView.onDestroy()
        super.onDestroy()
    }

    private fun initializePlayer() {
        val key = "{ANALYTICS_LICENSE_KEY}"
        val analyticsConfig = AnalyticsConfig(
            licenseKey = key,
            adTrackingDisabled = false,
            randomizeUserId = true,
        )
        // create a player with analytics config
        player = Player.create(this, PlayerConfig(), analyticsConfig).also {
            binding.playerView.player = it
        }

        // create a source with a sourceMetadata for custom analytics tracking
        val sourceConfig = SourceConfig.fromUrl(Sintel)
        val sourceMetadata = SourceMetadata(
            customData = CustomData(
                customData1 = "CustomData1",
                experimentName = "Experiment1",
            )
        )
        // load the source with custom metadata
        val source = Source.create(sourceConfig, sourceMetadata)

        player.load(source)
    }
}
