package com.bitmovin.player.samples.ads.pause

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.bitmovin.analytics.api.AnalyticsConfig
import com.bitmovin.player.api.ExperimentalBitmovinApi
import com.bitmovin.player.api.Player
import com.bitmovin.player.api.PlayerConfig
import com.bitmovin.player.api.advertising.AdItem
import com.bitmovin.player.api.advertising.AdSource
import com.bitmovin.player.api.advertising.AdSourceType
import com.bitmovin.player.api.advertising.AdvertisingConfig
import com.bitmovin.player.api.analytics.AnalyticsPlayerConfig
import com.bitmovin.player.api.event.PlayerEvent
import com.bitmovin.player.api.event.on
import com.bitmovin.player.api.source.SourceConfig
import com.bitmovin.player.samples.ads.pause.databinding.ActivityMainBinding

private const val TAG = "PauseAd"

private const val Sintel = "https://cdn.bitmovin.com/content/assets/sintel/sintel.mpd"
// Sample tag from https://developers.google.com/interactive-media-ads/docs/sdks/android/tags
private const val NON_LINEAR_AD = "https://pubads.g.doubleclick.net/gampad/ads?sz=480x70&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dnonlinear&correlator="

class MainActivity : AppCompatActivity() {
    private lateinit var player: Player
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(insets.left, insets.top, insets.right, insets.bottom)
            WindowInsetsCompat.CONSUMED
        }
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }

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

    // Pause ads are experimental and their API may change in future releases.
    @OptIn(ExperimentalBitmovinApi::class)
    private fun initializePlayer() {
        // Pause ads require a VAST tag with a static PNG or JPEG NonLinear creative
        // and an AdSource of type AdSourceType.Bitmovin.
        val pauseAdSource = AdSource(AdSourceType.Bitmovin, NON_LINEAR_AD)

        // The "pause" position makes the AdItem a pause ad, which is presented while the main content is paused.
        // A visible pause ad is dismissed through the UI, player.ads.skip(), seeking, or resuming playback.
        val pauseAd = AdItem(position = "pause", sources = arrayOf(pauseAdSource))

        // Pause ads can also be scheduled at runtime using player.ads.schedule(pauseAd).
        val playerConfig = PlayerConfig(advertisingConfig = AdvertisingConfig(schedule = listOf(pauseAd)))

        val analyticsKey = "{ANALYTICS_LICENSE_KEY}"
        player = Player(
            this,
            playerConfig,
            AnalyticsPlayerConfig.Enabled(AnalyticsConfig(analyticsKey)),
        ).also {
            binding.playerView.player = it
        }

        // Observe the pause ad presentation lifecycle
        player.on<PlayerEvent.NonLinearAdStarted> { Log.i(TAG, "Started") }
        player.on<PlayerEvent.NonLinearAdFinished> { Log.i(TAG, "Finished") }
        player.on<PlayerEvent.NonLinearAdSkipped> { Log.i(TAG, "Skipped") }

        player.load(SourceConfig.fromUrl(Sintel))
    }
}
