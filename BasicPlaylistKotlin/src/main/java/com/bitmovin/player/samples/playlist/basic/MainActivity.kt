package com.bitmovin.player.samples.playlist.basic

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.bitmovin.analytics.api.AnalyticsConfig
import com.bitmovin.player.api.Player
import com.bitmovin.player.api.analytics.AnalyticsPlayerConfig
import com.bitmovin.player.api.event.PlayerEvent
import com.bitmovin.player.api.event.SourceEvent
import com.bitmovin.player.api.event.next
import com.bitmovin.player.api.event.on
import com.bitmovin.player.api.playlist.PlaylistApi
import com.bitmovin.player.api.playlist.PlaylistConfig
import com.bitmovin.player.api.playlist.PlaylistOptions
import com.bitmovin.player.api.playlist.ReplayMode
import com.bitmovin.player.api.source.Source
import com.bitmovin.player.api.source.SourceConfig
import com.bitmovin.player.api.source.SourceType
import com.bitmovin.player.samples.playlist.basic.databinding.ActivityMainBinding

private const val SintelHls = "https://cdn.bitmovin.com/content/assets/sintel/hls/playlist.m3u8"
private const val ArtOfMotionProgressive = "https://cdn.bitmovin.com/content/assets/MI201109210084/MI201109210084_mpeg-4_hd_high_1080p25_10mbits.mp4"
private const val SintelDash = "https://cdn.bitmovin.com/content/assets/sintel/sintel.mpd"
private const val KronehitLiveHls = "https://bitcdn-kronehit.bitmovin.com/v2/hls/playlist.m3u8"

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

        val analyticsKey = "{ANALYTICS_LICENSE_KEY}"
        player = Player(
            context = this,
            analyticsConfig = AnalyticsPlayerConfig.Enabled(AnalyticsConfig(analyticsKey)),
        ).also {
            binding.playerView.player = it
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
        player.off(::onPlaylistTransition)
        super.onDestroy()
    }

    private fun initializePlayer() {
        val sources = listOf(
                Source(
                        SourceConfig(
                                url = SintelHls,
                                type = SourceType.Hls,
                                title = "(1/4) Sintel HLS",
                        )
                ),
                Source(
                        SourceConfig(
                                url = ArtOfMotionProgressive,
                                type = SourceType.Progressive,
                                title = "(2/4) Art of Motion Progressive",
                        )
                ),
                Source(
                        SourceConfig(
                                url = SintelDash,
                                type = SourceType.Dash,
                                title = "(3/4) Sintel DASH",
                        )
                ),
                Source(
                        SourceConfig(
                                url = KronehitLiveHls,
                                type = SourceType.Hls,
                                title = "(4/4) Kronehit Live HLS",
                        )
                )
        )

        sources.forEach { source ->
            source.next<SourceEvent.Loaded> {
                println("Loaded ${it.source.config.title} with duration ${it.source.duration}")
            }
        }

        val playlistConfig = PlaylistConfig(
                sources,
                PlaylistOptions(
                        preloadAllSources = true,
                        replayMode = ReplayMode.Playlist
                )
        )

        player.on(::onPlaylistTransition)

        player.load(playlistConfig)
    }

    private fun onPlaylistTransition(event: PlayerEvent.PlaylistTransition) {
        binding.playerView.post {
            val text = "Transitioned from ${event.from.config.title} to ${event.to.config.title}."
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
        }
    }

    fun previous(view: View) = player.playlist.previous()

    fun next(view: View) = player.playlist.next()
}


fun PlaylistApi.next(time: Double = 0.0) {
    (sources.indexOf(activeSource) + 1)
            .takeIf { it < sources.count() }
            ?.let { seek(sources[it], time) }
}

fun PlaylistApi.previous(time: Double = 0.0) {
    (sources.indexOf(activeSource) - 1)
            .takeIf { it > -1 }
            ?.let { seek(sources[it], time) }
}

private val PlaylistApi.activeSource: Source? get() = sources.firstOrNull { it.isActive }
