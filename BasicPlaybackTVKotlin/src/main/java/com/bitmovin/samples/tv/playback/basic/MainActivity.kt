package com.bitmovin.samples.tv.playback.basic

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.bitmovin.analytics.api.AnalyticsConfig
import com.bitmovin.player.PlayerView
import com.bitmovin.player.api.PlaybackConfig
import com.bitmovin.player.api.Player
import com.bitmovin.player.api.PlayerConfig
import com.bitmovin.player.api.analytics.AnalyticsPlayerConfig
import com.bitmovin.player.api.deficiency.ErrorEvent
import com.bitmovin.player.api.event.PlayerEvent
import com.bitmovin.player.api.event.SourceEvent
import com.bitmovin.player.api.event.on
import com.bitmovin.player.api.source.SourceConfig
import com.bitmovin.player.api.source.SourceType
import com.bitmovin.player.api.ui.PlayerViewConfig
import com.bitmovin.player.api.ui.UiConfig
import com.bitmovin.player.samples.tv.playback.basic.R
import com.bitmovin.player.samples.tv.playback.basic.databinding.ActivityMainBinding

private const val SEEKING_OFFSET = 10
private val TAG = MainActivity::class.java.simpleName


class MainActivity : AppCompatActivity() {
    private lateinit var playerView: PlayerView
    private lateinit var player: Player
    private lateinit var binding: ActivityMainBinding
    private var pendingSeekTarget: Double? = null

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
            // Here a custom bitmovinplayer-ui.js is loaded which utilizes the Cast-UI as this
            // matches our needs here perfectly.
            // I.e. UI controls get shown / hidden whenever the Player API is called.
            // This is needed due to the fact that on Android TV no touch events are received
            PlayerViewConfig(
                uiConfig = UiConfig.WebUi(
                    jsLocation = "file:///android_asset/bitmovinplayer-ui.js",
                )
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

    override fun onResume() {
        super.onResume()

        playerView.onResume()
        addEventListener()
        player.play()
    }

    override fun onStart() {
        super.onStart()
        playerView.onStart()
    }

    override fun onPause() {
        removeEventListener()
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
        Log.d(TAG, "Keycode $keycode")
        return when (keycode) {
            KeyEvent.KEYCODE_DPAD_CENTER,
            KeyEvent.KEYCODE_ENTER,
            KeyEvent.KEYCODE_NUMPAD_ENTER,
            KeyEvent.KEYCODE_SPACE,
            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> {
                player.togglePlay()
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
            KeyEvent.KEYCODE_MEDIA_STOP -> {
                player.stopPlayback()
                true
            }
            KeyEvent.KEYCODE_DPAD_RIGHT,
            KeyEvent.KEYCODE_MEDIA_FAST_FORWARD -> {
                player.seekForward()
                true
            }
            KeyEvent.KEYCODE_DPAD_LEFT,
            KeyEvent.KEYCODE_MEDIA_REWIND -> {
                player.seekBackward()
                true
            }
            else -> return false
        }
    }

    private fun addEventListener() {
        player.on<PlayerEvent.Error>(::onErrorEvent)
        player.on<SourceEvent.Error>(::onErrorEvent)
        player.on(::onSeeked)
    }

    private fun removeEventListener() {
        player.off(::onErrorEvent)
        player.off(::onSeeked)
    }

    private fun onErrorEvent(errorEvent: ErrorEvent) {
        Log.e(TAG, "An Error occurred (${errorEvent.code}): ${errorEvent.message}")
    }

    private fun onSeeked(event: PlayerEvent.Seeked) {
        pendingSeekTarget = null
    }

    private fun Player.seekForward() {
        val seekTarget = (pendingSeekTarget ?: currentTime) + SEEKING_OFFSET
        pendingSeekTarget = seekTarget
        seek(seekTarget)
    }

    private fun Player.seekBackward() {
        val seekTarget = (pendingSeekTarget ?: currentTime) - SEEKING_OFFSET
        pendingSeekTarget = seekTarget
        seek(seekTarget)
    }
}

private fun Player.togglePlay() = if (isPlaying) pause() else play()

private fun Player.stopPlayback() {
    pause()
    seek(0.0)
}
