package com.bitmovin.player.samples.playback.cronet

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.bitmovin.analytics.api.AnalyticsConfig
import com.bitmovin.player.api.Player
import com.bitmovin.player.api.PlayerConfig
import com.bitmovin.player.api.TweaksConfig
import com.bitmovin.player.api.analytics.AnalyticsPlayerConfig
import com.bitmovin.player.api.source.SourceConfig
import com.bitmovin.player.samples.playback.cronet.databinding.ActivityMainBinding
import com.google.android.gms.net.CronetProviderInstaller
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.future.asCompletableFuture
import org.chromium.net.CronetEngine
import java.util.concurrent.Future

private const val Sintel = "https://cdn.bitmovin.com/content/assets/sintel/sintel.mpd"

class MainActivity : AppCompatActivity() {
    private lateinit var player: Player
    private lateinit var binding: ActivityMainBinding

    /**
     * For best performance, Cronet should be loaded only once and use throughout your app.
     * In particular the same engine instance should be used by all players
     * to share and reuse TCP connections.
     * See [TweaksConfig.cronetEngine].
     */
    private lateinit var cronetEngine: Future<CronetEngine?>

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

        cronetEngine = loadCronet()
        initializePlayer()
    }

    private fun initializePlayer() {
        val analyticsKey = "{ANALYTICS_LICENSE_KEY}"
        player = Player(
            context = this,
            analyticsConfig = AnalyticsPlayerConfig.Enabled(AnalyticsConfig(analyticsKey)),
            playerConfig = PlayerConfig(tweaksConfig = TweaksConfig(cronetEngine = cronetEngine))
        ).also {
            binding.playerView.player = it
        }

        player.load(SourceConfig.fromUrl(Sintel))
    }

    private fun loadCronet(): Future<CronetEngine?> {
        // See https://developer.android.com/codelabs/cronet#5
        val result = CompletableDeferred<CronetEngine?>()
        CronetProviderInstaller.installProvider(this).addOnCompleteListener {
            if (it.isSuccessful) {
                result.complete(CronetEngine.Builder(this).build())
            } else {
                Log.w("BitmovinPlayer", "Cronet provider installation failed, fallback to HTTP/1")
                result.complete(null)
            }
        }
        return result.asCompletableFuture()
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
