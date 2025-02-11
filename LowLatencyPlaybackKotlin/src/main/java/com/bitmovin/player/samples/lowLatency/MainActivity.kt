package com.bitmovin.player.samples.lowLatency

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.bitmovin.analytics.api.AnalyticsConfig
import com.bitmovin.player.PlayerView
import com.bitmovin.player.api.PlaybackConfig
import com.bitmovin.player.api.Player
import com.bitmovin.player.api.PlayerConfig
import com.bitmovin.player.api.analytics.AnalyticsPlayerConfig
import com.bitmovin.player.api.buffer.BufferType
import com.bitmovin.player.api.live.LiveConfig
import com.bitmovin.player.api.live.LiveSynchronizationMethod
import com.bitmovin.player.api.live.SourceLiveConfig
import com.bitmovin.player.api.live.SynchronizationConfigEntry
import com.bitmovin.player.api.live.TargetSynchronizationConfig
import com.bitmovin.player.api.media.MediaType
import com.bitmovin.player.api.source.Source
import com.bitmovin.player.api.source.SourceConfig
import com.bitmovin.player.api.source.SourceType
import com.bitmovin.player.samples.lowLatency.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var playerView: PlayerView
    private lateinit var binding: ActivityMainBinding
    private lateinit var player: Player
    private val scope = CoroutineScope(Dispatchers.Main)
    private val clockFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())

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

        scope.launch { continuouslyUpdateCurrentTime() }
        scope.launch { continuouslyUpdateLatencyAndBufferLevel() }
    }

    private fun initializePlayer() {
        val sourceLiveConfig = SourceLiveConfig(
            // Initial target latency that the player tries to achieve
            targetLatency = 3.0,
            // Configure catchup and fallback behavior.
            // The values provided here are exaggerated for demonstration purposes,
            // using the default values is recommended.
            catchupConfig = TargetSynchronizationConfig(
                seekThreshold = TargetSynchronizationConfig.DEFAULT_SEEK_THRESHOLD,
                playbackRate = 2.0f,
            ),
            fallbackConfig = TargetSynchronizationConfig(
                seekThreshold = 5.0,
                playbackRate = 0.5f,
            ),
        )
        val playerLiveConfig = LiveConfig(
            // Optional time synchronization configuration
            synchronization = listOf(
                SynchronizationConfigEntry(
                    "time.akamai.com",
                    LiveSynchronizationMethod.Ntp,
                )
            )
        )

        val source = Source(
            SourceConfig(
                url = "https://akamaibroadcasteruseast.akamaized.net/cmaf/live/657078/akasource/out.mpd",
                type = SourceType.Dash,
                liveConfig = sourceLiveConfig,
            )
        )

        val analyticsKey = "{ANALYTICS_LICENSE_KEY}"
        player = Player(
            this,
            playerConfig = PlayerConfig(
                playbackConfig = PlaybackConfig(isAutoplayEnabled = true),
                liveConfig = playerLiveConfig,
            ),
            analyticsConfig = AnalyticsPlayerConfig.Enabled(AnalyticsConfig(analyticsKey)),
        )
        playerView = binding.playerView
        playerView.player = player

        player.load(source)
    }

    private suspend fun continuouslyUpdateCurrentTime() {
        while (scope.isActive) {
            binding.currentTimeTextView.text = clockFormat.format(System.currentTimeMillis())

            delay(100)
        }
    }

    @SuppressLint("SetTextI18n")
    private suspend fun continuouslyUpdateLatencyAndBufferLevel() {
        while (scope.isActive) {
            val currentLatency = player.lowLatency.latency
            val targetLatency = player.lowLatency.targetLatency
            val videoBuffer = player.buffer.getLevel(
                BufferType.ForwardDuration,
                MediaType.Video,
            ).level

            binding.currentLatencyTextView.text = "Current Latency: $currentLatency"
            binding.targetLatencyTextView.text = "Target Latency: $targetLatency"
            binding.bufferTextView.text = "Forward buffer: $videoBuffer"

            delay(500)
        }
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
        scope.cancel()
        playerView.onDestroy()
        super.onDestroy()
    }
}
