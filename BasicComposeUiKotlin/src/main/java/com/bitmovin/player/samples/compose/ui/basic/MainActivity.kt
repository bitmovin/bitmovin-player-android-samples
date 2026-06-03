package com.bitmovin.player.samples.compose.ui.basic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.bitmovin.player.api.ExperimentalBitmovinApi
import com.bitmovin.player.api.Player
import com.bitmovin.player.api.source.SourceConfig
import com.bitmovin.player.api.ui.PlayerViewConfig
import com.bitmovin.player.api.ui.compose.wrapper.PlayerView

private const val Sintel = "https://cdn.bitmovin.com/content/assets/sintel/sintel.mpd"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black,
                ) {
                    BasicComposeUiSample()
                }
            }
        }
    }
}

@OptIn(ExperimentalBitmovinApi::class)
@Composable
private fun BasicComposeUiSample() {
    val context = LocalContext.current
    val player = remember(context) {
        Player(context).apply {
            load(SourceConfig.fromUrl(Sintel))
        }
    }

    DisposableEffect(player) {
        onDispose {
            player.destroy()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .windowInsetsPadding(WindowInsets.systemBars),
    ) {
        PlayerView(
            player = player,
            modifier = Modifier.fillMaxSize(),
            playerViewConfig = PlayerViewConfig(),
        )
    }
}
