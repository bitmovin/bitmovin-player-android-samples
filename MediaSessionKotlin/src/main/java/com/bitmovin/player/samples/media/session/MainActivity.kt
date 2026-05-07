package com.bitmovin.player.samples.media.session

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.bitmovin.player.PlayerView
import com.bitmovin.player.api.Player
import com.bitmovin.player.api.source.Source
import com.bitmovin.player.api.source.SourceConfig
import com.bitmovin.player.api.source.SourceType
import com.bitmovin.player.api.media.session.MediaMetadata
import com.bitmovin.player.api.media.session.MediaSessionSourceConfig
import com.bitmovin.player.samples.media.session.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var playerView: PlayerView
    private var serviceBinder: MediaSessionPlaybackService.ServiceBinder? = null
    private val player: Player? get() = serviceBinder?.player
    private var isBound = false
    private lateinit var uiBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        uiBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(uiBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(uiBinding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(insets.left, insets.top, insets.right, insets.bottom)
            WindowInsetsCompat.CONSUMED
        }
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }

        // Create a PlayerView without a Player and add it to the View hierarchy
        playerView = PlayerView(this, null as Player?).apply {
            layoutParams = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        playerView.keepScreenOn = true
        uiBinding.root.addView(playerView)
    }

    private fun initializePlayer() {
        // Load a new source
        val sourceConfig = SourceConfig(
            "https://cdn.bitmovin.com/content/assets/MI201109210084/mpds/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.mpd",
            SourceType.Dash,
            title = "Art of Motion",
            posterSource = "https://cdn.bitmovin.com/content/assets/poster/hd/RedBull.jpg"
        )
        val mediaMetadata = MediaMetadata(
            subtitle = "Those Who Don't Jump Will Never Fly",
            releaseYear = 2013,
            genre = "Documentary",
            mediaType = MediaMetadata.MediaType.Movie,
        )
        val source = Source(
            sourceConfig,
            mediaSessionConfig = MediaSessionSourceConfig.Enabled(mediaMetadata)
        )

        player?.load(source)
    }


    private fun bindService() {
        val intent = Intent(this, MediaSessionPlaybackService::class.java)
        intent.setAction(Intent.ACTION_MEDIA_BUTTON)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
        startService(intent)
    }

    private fun unbindService() {
        unbindService(connection)
    }

    override fun onStart() {
        super.onStart()
        playerView.onStart()
        bindService()
    }

    override fun onResume() {
        super.onResume()

        // Attach the Player to allow the PlayerView to control the player
        playerView.player = player
        playerView.onResume()
    }

    override fun onPause() {
        // Detach the Player to decouple it from the PlayerView lifecycle
        playerView.player = null
        playerView.onPause()
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
        // Unbind the Service and reset the Player reference
        unbindService()
        playerView.onStop()
    }

    override fun onDestroy() {
        playerView.onDestroy()
        super.onDestroy()
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to the Service, cast the IBinder and get the Player instance
            val binder = service as MediaSessionPlaybackService.ServiceBinder
            serviceBinder = binder
            val player = binder.player
            playerView.player = player
            if (player.source == null) {
               initializePlayer()
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            isBound = false
        }
    }
}
