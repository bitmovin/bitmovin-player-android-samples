package com.bitmovin.player.samples.custom.ui.html

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.webkit.JavascriptInterface
import android.widget.LinearLayout
import com.bitmovin.player.BitmovinPlayerView
import com.bitmovin.player.config.PlayerConfiguration
import com.bitmovin.player.config.StyleConfiguration
import com.bitmovin.player.config.media.SourceConfiguration
import com.bitmovin.player.ui.CustomMessageHandler
import kotlinx.android.synthetic.main.activity_playback.*

class PlaybackActivity : AppCompatActivity() {

    private var bitmovinPlayerView: BitmovinPlayerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playback)

        // Create new StyleConfiguration
        val styleConfiguration = StyleConfiguration()
        /*
         * Go to https://github.com/bitmovin/bitmovin-player-ui to get started with creating a custom player UI.
         */
        // Set URLs for the JavaScript and the CSS
        styleConfiguration.playerUiJs = "file:///android_asset/custom-bitmovinplayer-ui.min.js"
        styleConfiguration.playerUiCss = "file:///android_asset/custom-bitmovinplayer-ui.min.css"

        // Create a new source configuration
        val sourceConfiguration = SourceConfiguration()
        // Add a new source item
        sourceConfiguration.addSourceItem("https://bitdash-a.akamaihd.net/content/sintel/sintel.mpd")

        // Creating a new PlayerConfiguration
        val playerConfiguration = PlayerConfiguration()
        // Assign created StyleConfiguration to the PlayerConfiguration
        playerConfiguration.styleConfiguration = styleConfiguration
        // Assign created SourceConfiguration to the PlayerConfiguration
        playerConfiguration.sourceConfiguration = sourceConfiguration

        // Create a custom javascriptInterface object which takes over the Bitmovin Web UI -> native calls
        val javascriptInterface = object : Any() {
            @JavascriptInterface
            fun closePlayer(data: String): String? {
                finish()
                return null
            }
        }

        // Setup CustomMessageHandler for communication with Bitmovin Web UI
        val customMessageHandler = CustomMessageHandler(javascriptInterface)

        // Create new BitmovinPlayerView with our PlayerConfiguration
        this.bitmovinPlayerView = BitmovinPlayerView(this, playerConfiguration)
        this.bitmovinPlayerView?.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)

        // Set the CustomMessageHandler to the bitmovinPlayerView
        this.bitmovinPlayerView?.setCustomMessageHandler(customMessageHandler)

        // Add BitmovinPlayerView to the layout as first child
        playerRootLayout.addView(this.bitmovinPlayerView, 0)

        toggleCloseButtonStateButton.setOnClickListener {
            customMessageHandler.sendMessage("toggleCloseButton", null)
        }
    }

    override fun onStart() {
        super.onStart()
        this.bitmovinPlayerView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        this.bitmovinPlayerView?.onResume()
    }

    override fun onPause() {
        this.bitmovinPlayerView?.onPause()
        super.onPause()
    }

    override fun onStop() {
        this.bitmovinPlayerView?.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        this.bitmovinPlayerView?.onDestroy()
        super.onDestroy()
    }
}
