package com.bitmovin.player.samples.playback.basic

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bitmovin.analytics.api.AnalyticsConfig
import com.bitmovin.player.api.Player
import com.bitmovin.player.api.analytics.AnalyticsPlayerConfig
import com.bitmovin.player.api.source.SourceConfig
import com.bitmovin.player.samples.playback.basic.databinding.ActivityMainBinding

private const val Sintel = "https://cdn.bitmovin.com/content/assets/sintel/sintel.mpd"

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

        player.load(SourceConfig.fromUrl(Sintel))
    }
}
