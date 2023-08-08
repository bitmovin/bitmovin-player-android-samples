package com.bitmovin.player.samples.custom.ui.html

import android.os.Bundle
import android.webkit.JavascriptInterface
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.bitmovin.analytics.api.AnalyticsConfig
import com.bitmovin.player.PlayerView
import com.bitmovin.player.api.Player
import com.bitmovin.player.api.PlayerConfig
import com.bitmovin.player.api.analytics.create
import com.bitmovin.player.api.source.SourceConfig
import com.bitmovin.player.api.ui.StyleConfig
import com.bitmovin.player.samples.custom.ui.html.databinding.ActivityPlaybackBinding
import com.bitmovin.player.ui.CustomMessageHandler

class PlaybackActivity : AppCompatActivity() {
    private lateinit var playerView: PlayerView
    private lateinit var binding: ActivityPlaybackBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaybackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Go to https://github.com/bitmovin/bitmovin-player-ui to get started with creating a custom player UI.
        // Creating a new PlayerConfig with a StyleConfig
        val playerConfig = PlayerConfig(
            styleConfig = StyleConfig(
                // Set URLs for the JavaScript and the CSS
                playerUiJs = "file:///android_asset/custom-bitmovinplayer-ui.min.js",
                playerUiCss = "file:///android_asset/custom-bitmovinplayer-ui.min.css"
            )
        )

        // Create a Player with our PlayerConfig
        val analyticsKey = "{ANALYTICS_LICENSE_KEY}"
        val player = Player.create(this, playerConfig, AnalyticsConfig(analyticsKey))

        // Create new PlayerView with our Player
        playerView = PlayerView(this, player)
        playerView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)

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

        // Set the CustomMessageHandler to the playerView
        playerView.setCustomMessageHandler(customMessageHandler)

        //load the SourceConfig into the player
        player.load(SourceConfig.fromUrl("https://bitdash-a.akamaihd.net/content/sintel/sintel.mpd"))

        // Add PlayerView to the layout as first child
        binding.playerRootLayout.addView(playerView, 0)

        binding.toggleCloseButtonStateButton.setOnClickListener {
            customMessageHandler.sendMessage("toggleCloseButton", null)
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
        playerView.onDestroy()
        super.onDestroy()
    }
}
