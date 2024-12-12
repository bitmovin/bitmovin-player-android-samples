package com.bitmovin.player.samples.playback.basic

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bitmovin.analytics.api.AnalyticsConfig
import com.bitmovin.player.api.Player
import com.bitmovin.player.api.analytics.AnalyticsPlayerConfig
import com.bitmovin.player.api.source.SourceConfig
import com.bitmovin.player.samples.playback.basic.databinding.ActivityMainBinding

// Art of Motion with X-ASSET-LIST. (scheduled at 3 seconds - 2 ads inside the asset list)
private const val ArtOfMotion_Interstitial_MidRollAssetList = "https://bitmovin-player-eu-west1-ci-input.s3.amazonaws.com/general/hls/interstitials/aom_asset_list_mid/main.m3u8"
// Art of Motion with a pre and mid interstitial (mid at 5 seconds)
private const val ArtOfMotion_Interstitial_PreAndMidRoll = "https://bitmovin-player-eu-west1-ci-input.s3.amazonaws.com/general/hls/interstitials/aom_pre_mid/main.m3u8"

private const val source = ArtOfMotion_Interstitial_PreAndMidRoll

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
        val analyticsKey = "{ANALYTICS_LICENSE_KEY}"
        player = Player(
            context = this,
            analyticsConfig = AnalyticsPlayerConfig.Enabled(AnalyticsConfig(analyticsKey)),
        ).also {
            binding.playerView.player = it
        }

        player.load(SourceConfig.fromUrl(source))
    }
}
