package com.bitmovin.player.samples.fullscreen.basic

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.bitmovin.player.api.Player
import com.bitmovin.player.api.source.SourceConfig
import com.bitmovin.player.samples.fullscreen.basic.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var player: Player? = null
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }

        player = binding.playerView.player

        // Instantiate a custom FullscreenHandler
        val customFullscreenHandler = CustomFullscreenHandler(this, binding.root, binding.playerView, supportActionBar)
        // Set the FullscreenHandler to the PlayerView
        binding.playerView.setFullscreenHandler(customFullscreenHandler)

        initializePlayer()
    }

    override fun onStart() {
        super.onStart()
        binding.playerView.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.playerView.onResume()
    }

    override fun onPause() {
        binding.playerView.onPause()
        super.onPause()
    }

    override fun onStop() {
        binding.playerView.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        binding.playerView.onDestroy()
        super.onDestroy()
    }

    private fun initializePlayer() {
        // Load a new source
        player?.load(SourceConfig.fromUrl("https://cdn.bitmovin.com/content/assets/sintel/sintel.mpd"))
    }
}
