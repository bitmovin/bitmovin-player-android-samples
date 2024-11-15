package com.bitmovin.samples.tv.playback.basic

import android.os.Bundle
import android.view.KeyEvent
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.bitmovin.analytics.api.AnalyticsConfig
import com.bitmovin.player.PlayerView
import com.bitmovin.player.api.PlaybackConfig
import com.bitmovin.player.api.Player
import com.bitmovin.player.api.PlayerConfig
import com.bitmovin.player.api.analytics.AnalyticsPlayerConfig
import com.bitmovin.player.api.source.SourceConfig
import com.bitmovin.player.api.source.SourceType
import com.bitmovin.player.api.ui.PlayerViewConfig
import com.bitmovin.player.api.ui.UiConfig.WebUi
import com.bitmovin.player.samples.tv.playback.basic.R
import com.bitmovin.player.samples.tv.playback.basic.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var playerView: PlayerView
    private lateinit var player: Player
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        // Switch from splash screen to main theme when we are done loading
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializePlayer()
    }

    private fun initializePlayer() {
        // Initialize PlayerView from layout and attach a new Player instance
        val analyticsKey = "{ANALYTICS_LICENSE_KEY}"

        player = Player(
            this,
            PlayerConfig(
                playbackConfig = PlaybackConfig(isAutoplayEnabled = true)
            ),
            AnalyticsPlayerConfig.Enabled(AnalyticsConfig(analyticsKey)),
        )

        playerView = PlayerView(
            this,
            player,
            PlayerViewConfig(
                uiConfig = WebUi(variant = WebUi.Variant.TvUi)
            )
        )
        playerView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        playerView.keepScreenOn = true
        binding.playerRootLayout.addView(playerView, 0)

        // Create a new SourceConfig. In this case we are loading a DASH source.
        val sourceURL = "https://bitmovin-a.akamaihd.net/content/MI201109210084_1/mpds/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.mpd"

        player.load(SourceConfig(sourceURL, SourceType.Dash))
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        // This method is called on key down and key up, so avoid being called twice
        if (event.action == KeyEvent.ACTION_DOWN) {
            if (handleUserInput(event.keyCode)) {
                return true
            }
        }

        // Make sure to return super.dispatchKeyEvent(event) so that any key not handled yet will work as expected
        return super.dispatchKeyEvent(event)
    }

    private fun handleUserInput(keycode: Int): Boolean {
        return when (keycode) {
            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> {
                togglePlay()
                true
            }

            KeyEvent.KEYCODE_MEDIA_PLAY -> {
                player.play()
                true
            }

            KeyEvent.KEYCODE_MEDIA_PAUSE -> {
                player.pause()
                true
            }

            else -> false
        }
    }

    private fun togglePlay() {
        if (player.isPlaying) {
            player.pause()
        } else {
            player.play()
        }
    }

    override fun onResume() {
        super.onResume()
        playerView.onResume()
    }

    override fun onStart() {
        super.onStart()
        playerView.onStart()
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
