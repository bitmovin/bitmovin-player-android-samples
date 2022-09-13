package com.bitmovin.player.samples.playback.background

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bitmovin.player.PlayerView
import com.bitmovin.player.api.Player
import com.bitmovin.player.api.source.SourceConfig
import com.bitmovin.player.api.source.SourceType
import com.bitmovin.player.samples.playback.background.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var playerView: PlayerView
    private var player: Player? = null
    private var bound = false
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Create a PlayerView without a Player and add it to the View hierarchy
        playerView = PlayerView(this, null as Player?).apply {
            layoutParams = RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        binding.root.addView(playerView)
    }

    override fun onStart() {
        super.onStart()
        playerView.onStart()
        requestMissingPermissions()

        // Bind and start the BackgroundPlaybackService
        val intent = Intent(this, BackgroundPlaybackService::class.java)
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE)

        // If the Service is not started, it would get destroyed as soon as the Activity unbinds.
        startService(intent)
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
        unbindService(mConnection)
        player = null
        bound = false
        playerView.onStop()
    }

    override fun onDestroy() {
        playerView.onDestroy()
        super.onDestroy()
    }

    private fun initializePlayer() {
        // Load a new source
        val sourceConfig = SourceConfig(
                "https://bitmovin-a.akamaihd.net/content/MI201109210084_1/mpds/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.mpd",
                SourceType.Dash,
                posterSource = "https://bitmovin-a.akamaihd.net/content/poster/hd/RedBull.jpg"
        )

        player?.load(sourceConfig)
    }

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private val mConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to the Service, cast the IBinder and get the Player instance
            val binder = service as BackgroundPlaybackService.BackgroundBinder
            player = binder.player

            // Attach the Player as soon as we have a reference
            playerView.player = player

            // If not already initialized, initialize the player with a source.
            if (player?.source == null) {
                initializePlayer()
            }

            bound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            bound = false
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(RequestPermission()) {
        // Do nothing
    }

    private fun requestMissingPermissions() {
        if (Build.VERSION.SDK_INT < 33) return

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
