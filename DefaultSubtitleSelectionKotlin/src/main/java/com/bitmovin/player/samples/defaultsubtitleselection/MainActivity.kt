package com.bitmovin.player.samples.defaultsubtitleselection

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
import com.bitmovin.player.api.analytics.AnalyticsPlayerConfig
import com.bitmovin.player.api.event.SourceEvent
import com.bitmovin.player.api.event.on
import com.bitmovin.player.api.source.SourceConfig
import com.bitmovin.player.api.source.SourceType
import com.bitmovin.player.samples.defaultsubtitleselection.databinding.ActivityMainBinding

private const val TAG = "DefaultSubtitleSelection"

// Replace with your own stream that has subtitle tracks with isDefault=true.
private const val StreamUrl = "https://cdn.bitmovin.com/content/assets/sintel/hls/playlist.m3u8"

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
        player.off(::onSubtitleTracksChanged)
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

        player.on(::onSubtitleTracksChanged)

        player.load(SourceConfig(StreamUrl, SourceType.Hls))
    }

    private fun onSubtitleTracksChanged(event: SourceEvent.SubtitleTracksChanged) {
        val tracks = event.newSubtitleTracks
        Log.i(TAG, "Available subtitle tracks (${tracks.size}):")
        tracks.forEach { track ->
            Log.i(TAG, "  id=${track.id} label=${track.label} lang=${track.language} isDefault=${track.isDefault}")
        }

        // The Bitmovin Player intentionally does not auto-select subtitle tracks,
        // even when isDefault=true. Apps that want to honor the default flag can do so
        // explicitly, as shown here.
        val defaultTrack = tracks.firstOrNull { it.isDefault }
        if (defaultTrack != null) {
            Log.i(TAG, "Selecting default subtitle track: id=${defaultTrack.id} label=${defaultTrack.label}")
            player.source?.setSubtitleTrack(defaultTrack.id)
        }
    }
}
