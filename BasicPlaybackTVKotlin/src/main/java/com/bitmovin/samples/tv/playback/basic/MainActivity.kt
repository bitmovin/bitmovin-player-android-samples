package com.bitmovin.samples.tv.playback.basic

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.KeyEvent
import com.bitmovin.player.BitmovinPlayer
import com.bitmovin.player.api.event.listener.OnErrorListener
import com.bitmovin.player.config.PlaybackConfiguration
import com.bitmovin.player.config.PlayerConfiguration
import com.bitmovin.player.config.StyleConfiguration
import com.bitmovin.player.config.media.DASHSource
import com.bitmovin.player.config.media.SourceConfiguration
import com.bitmovin.player.config.media.SourceItem
import com.bitmovin.player.samples.tv.playback.basic.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName
    private val SEEKING_OFFSET = 10

    private var bitmovinPlayer: BitmovinPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        // Switch from splash screen to main theme when we are done loading
        this.setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        this.setContentView(R.layout.activity_main)

        this.initializePlayer()
    }

    private fun initializePlayer() {
        // Initialize BitmovinPlayerView from layout
        // Fetch BitmovinPlayer from BitmovinPlayerView
        this.bitmovinPlayer = bitmovinPlayerView.player

        this.bitmovinPlayer?.setup(this.createPlayerConfiguration())
    }

    private fun createPlayerConfiguration(): PlayerConfiguration {
        // Create a new SourceItem. In this case we are loading a DASH source.
        val sourceURL = "https://bitmovin-a.akamaihd.net/content/MI201109210084_1/mpds/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.mpd"
        val sourceItem = SourceItem(DASHSource(sourceURL))

        // Creating a new PlayerConfiguration
        val playerConfiguration = PlayerConfiguration()

        // Assign created SourceConfiguration to the PlayerConfiguration
        val sourceConfiguration = SourceConfiguration()
        sourceConfiguration.addSourceItem(sourceItem)
        playerConfiguration.sourceConfiguration = sourceConfiguration

        // Here a custom bitmovinplayer-ui.js is loaded which utilizes the Cast-UI as this matches our needs here perfectly.
        // I.e. UI controls get shown / hidden whenever the Player API is called. This is needed due to the fact that on Android TV no touch events are received
        val styleConfiguration = StyleConfiguration()
        styleConfiguration.playerUiJs = "file:///android_asset/bitmovinplayer-ui.js"
        playerConfiguration.styleConfiguration = styleConfiguration

        val playbackConfiguration = PlaybackConfiguration()
        playbackConfiguration.isAutoplayEnabled = true
        playerConfiguration.playbackConfiguration = playbackConfiguration

        return playerConfiguration
    }

    override fun onResume() {
        super.onResume()

        bitmovinPlayerView.onResume()
        this.addEventListener()
        this.bitmovinPlayer?.play()
    }

    override fun onStart() {
        super.onStart()
        bitmovinPlayerView.onStart()
    }

    override fun onPause() {
        this.removeEventListener()
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
        when (event.action) {
            KeyEvent.ACTION_DOWN -> if (this.handleUserInput(event.keyCode)) {
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
                togglePlay()
                return true
            }
            KeyEvent.KEYCODE_MEDIA_PLAY -> {
                this.bitmovinPlayer?.play()
                return true
            }
            KeyEvent.KEYCODE_MEDIA_PAUSE -> {
                this.bitmovinPlayer?.pause()
                return true
            }
            KeyEvent.KEYCODE_MEDIA_STOP -> {
                stopPlayback()
                return true
            }
            KeyEvent.KEYCODE_DPAD_RIGHT,
            KeyEvent.KEYCODE_MEDIA_FAST_FORWARD -> {
                seekForward()
            }
            KeyEvent.KEYCODE_DPAD_LEFT,
            KeyEvent.KEYCODE_MEDIA_REWIND -> {
                seekBackward()
            }
        }

        return false
    }

    private fun togglePlay() {
        bitmovinPlayer?.let { player ->
            if (player.isPlaying) {
                player.pause()
            } else {
                player.play()
            }
        }
    }

    private fun stopPlayback() {
        this.bitmovinPlayer?.pause()
        this.bitmovinPlayer?.seek(0.0)
    }

    private fun seekForward() {
        bitmovinPlayer?.let { player ->
            val currentTime = player.currentTime
            player.seek(currentTime.plus(SEEKING_OFFSET))
        }
    }

    private fun seekBackward() {
        bitmovinPlayer?.let { player ->
            val currentTime = player.currentTime
            player.seek(currentTime.minus(SEEKING_OFFSET))
        }
    }

    private fun addEventListener() {
        this.bitmovinPlayer?.addEventListener(this.onErrorListener)
    }

    private fun removeEventListener() {
        this.bitmovinPlayer?.removeEventListener(this.onErrorListener)
    }

    private val onErrorListener = OnErrorListener { errorEvent ->
        Log.e(TAG, "An Error occurred (${errorEvent.code}): ${errorEvent.message}")
    }

}
