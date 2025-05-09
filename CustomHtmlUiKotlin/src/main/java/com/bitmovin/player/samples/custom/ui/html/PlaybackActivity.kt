package com.bitmovin.player.samples.custom.ui.html

import android.os.Bundle
import android.webkit.JavascriptInterface
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.bitmovin.analytics.api.AnalyticsConfig
import com.bitmovin.player.PlayerView
import com.bitmovin.player.api.Player
import com.bitmovin.player.api.analytics.AnalyticsPlayerConfig
import com.bitmovin.player.api.source.SourceConfig
import com.bitmovin.player.api.ui.PlayerViewConfig
import com.bitmovin.player.api.ui.UiConfig
import com.bitmovin.player.samples.custom.ui.html.databinding.ActivityPlaybackBinding
import com.bitmovin.player.ui.CustomMessageHandler

class PlaybackActivity : AppCompatActivity() {
    private lateinit var playerView: PlayerView
    private lateinit var binding: ActivityPlaybackBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityPlaybackBinding.inflate(layoutInflater)
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

        // Go to https://github.com/bitmovin/bitmovin-player-ui to get started with creating a custom player UI.
        // Creating a new PlayerViewConfig
        val viewConfig = PlayerViewConfig(
            uiConfig = UiConfig.WebUi(
                // Set URLs for the JavaScript and the CSS
                jsLocation = "file:///android_asset/custom-bitmovinplayer-ui.min.js",
                cssLocation = "file:///android_asset/custom-bitmovinplayer-ui.min.css"
            )
        )

        // Create a Player with our PlayerConfig
        val analyticsKey = "{ANALYTICS_LICENSE_KEY}"
        val player = Player(
            context = this,
            analyticsConfig = AnalyticsPlayerConfig.Enabled(AnalyticsConfig(analyticsKey)),
        )

        // Create new PlayerView with our Player
        playerView = PlayerView(this, player, viewConfig)
        playerView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        playerView.keepScreenOn = true

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
        player.load(SourceConfig.fromUrl("https://cdn.bitmovin.com/content/assets/sintel/sintel.mpd"))

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
