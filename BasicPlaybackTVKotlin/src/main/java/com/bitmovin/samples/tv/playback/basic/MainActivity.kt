package com.bitmovin.samples.tv.playback.basic

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import com.bitmovin.player.api.PlaybackConfig
import com.bitmovin.player.api.Player
import com.bitmovin.player.api.PlayerConfig
import com.bitmovin.player.api.deficiency.ErrorEvent
import com.bitmovin.player.api.event.PlayerEvent
import com.bitmovin.player.api.event.SourceEvent
import com.bitmovin.player.api.event.on
import com.bitmovin.player.api.source.SourceConfig
import com.bitmovin.player.api.source.SourceType
import com.bitmovin.player.api.ui.StyleConfig
import com.bitmovin.player.samples.tv.playback.basic.R
import kotlinx.android.synthetic.main.activity_main.*

private const val SEEKING_OFFSET = 10
private val TAG = MainActivity::class.java.simpleName


class MainActivity : AppCompatActivity() {
    private lateinit var player: Player

    override fun onCreate(savedInstanceState: Bundle?) {
        // Switch from splash screen to main theme when we are done loading
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        initializePlayer()
    }

    private fun initializePlayer() {
        // Initialize PlayerView from layout and attach a new Player instance
        player = Player.create(this, createPlayerConfig()).also {
            bitmovinPlayerView.player = it
        }

        // Create a new SourceConfig. In this case we are loading a DASH source.
        val sourceURL = "https://bitmovin-a.akamaihd.net/content/MI201109210084_1/mpds/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.mpd"

        player.load(SourceConfig(sourceURL, SourceType.Dash))
    }

    override fun onResume() {
        super.onResume()

        bitmovinPlayerView.onResume()
        addEventListener()
        player.play()
    }

    override fun onStart() {
        super.onStart()
        bitmovinPlayerView.onStart()
    }

    override fun onPause() {
        removeEventListener()
        bitmovinPlayerView.onPause()
        super.onPause()
    }

    override fun onStop() {
        bitmovinPlayerView.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        bitmovinPlayerView.onDestroy()
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
        when (keycode) {
            KeyEvent.KEYCODE_DPAD_CENTER,
            KeyEvent.KEYCODE_ENTER,
            KeyEvent.KEYCODE_NUMPAD_ENTER,
            KeyEvent.KEYCODE_SPACE,
            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> {
                player.togglePlay()
                return true
            }
            KeyEvent.KEYCODE_MEDIA_PLAY -> {
                player.play()
                return true
            }
            KeyEvent.KEYCODE_MEDIA_PAUSE -> {
                player.pause()
                return true
            }
            KeyEvent.KEYCODE_MEDIA_STOP -> {
                player.stopPlayback()
                return true
            }
            KeyEvent.KEYCODE_DPAD_RIGHT,
            KeyEvent.KEYCODE_MEDIA_FAST_FORWARD -> {
                player.seekForward()
            }
            KeyEvent.KEYCODE_DPAD_LEFT,
            KeyEvent.KEYCODE_MEDIA_REWIND -> {
                player.seekBackward()
            }
        }

        return false
    }

    private fun addEventListener() {
        player.on<PlayerEvent.Error>(::onErrorEvent)
        player.on<SourceEvent.Error>(::onErrorEvent)
    }

    private fun removeEventListener() {
        player.off(::onErrorEvent)
    }

    private fun onErrorEvent(errorEvent: ErrorEvent) {
        Log.e(TAG, "An Error occurred (${errorEvent.code}): ${errorEvent.message}")
    }
}

private fun Player.togglePlay() = if (isPlaying) pause() else play()

private fun Player.stopPlayback() {
    pause()
    seek(0.0)
}

private fun Player.seekForward() = seek(this.currentTime + SEEKING_OFFSET)

private fun Player.seekBackward() = seek(currentTime - SEEKING_OFFSET)

private fun createPlayerConfig() = PlayerConfig(
        // Here a custom bitmovinplayer-ui.js is loaded which utilizes the Cast-UI as this
        // matches our needs here perfectly.
        // I.e. UI controls get shown / hidden whenever the Player API is called.
        // This is needed due to the fact that on Android TV no touch events are received
        styleConfig = StyleConfig(playerUiJs = "file:///android_asset/bitmovinplayer-ui.js"),
        playbackConfig = PlaybackConfig(isAutoplayEnabled = true)
)
