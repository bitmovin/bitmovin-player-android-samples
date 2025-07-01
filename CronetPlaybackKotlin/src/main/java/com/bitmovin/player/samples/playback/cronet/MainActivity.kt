package com.bitmovin.player.samples.playback.cronet

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import com.bitmovin.analytics.api.AnalyticsConfig
import com.bitmovin.player.api.Player
import com.bitmovin.player.api.analytics.AnalyticsPlayerConfig
import com.bitmovin.player.api.source.NetworkEngine
import com.bitmovin.player.api.source.SourceConfig
import com.bitmovin.player.api.source.SourceNetworkConfig
import com.bitmovin.player.api.source.SourceType
import com.bitmovin.player.samples.playback.cronet.databinding.ActivityMainBinding
import com.google.android.gms.net.CronetProviderInstaller
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.chromium.net.CronetEngine

private const val Sintel = "https://cdn.bitmovin.com/content/assets/sintel/sintel.mpd"

class MainActivity : AppCompatActivity() {
    private lateinit var player: Player
    private lateinit var binding: ActivityMainBinding

    /**
     * For best performance, the networkEngine (eg: Cronet) should be loaded only once and
     * use throughout your app.
     * In particular the same engine instance should be used by all players
     * to share and reuse TCP connections.
     * See [SourceNetworkConfig.engine].
     */
    private lateinit var networkEngine: NetworkEngine

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

        lifecycleScope.launch {
            // The network engine should be loaded as early as possible in the lifecycle of your app
            networkEngine = loadNetworkEngine()

            initializePlayer()
        }
    }

    private fun initializePlayer() {
        val analyticsKey = "{ANALYTICS_LICENSE_KEY}"
        player = Player(
            context = this,
            analyticsConfig = AnalyticsPlayerConfig.Enabled(AnalyticsConfig(analyticsKey)),
        ).also {
            binding.playerView.player = it
        }

        val sourceConfig = SourceConfig(
            url = Sintel,
            type = SourceType.Dash,
            networkConfig = SourceNetworkConfig(
                engine = networkEngine
            )
        )
        player.load(sourceConfig)
    }

    private suspend fun loadNetworkEngine(): NetworkEngine {
        // See https://developer.android.com/codelabs/cronet#5
        try {
            CronetProviderInstaller.installProvider(this).await()
            val cronetEngine = CronetEngine.Builder(this).build()
            return NetworkEngine.Cronet(cronetEngine)
        } catch (e: Exception) {
            Log.w("BitmovinPlayer", "Cronet engine creation failed, fallback to HTTP/1", e)
        }
        return NetworkEngine.HttpURLConnection
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
}
