package com.bitmovin.player.samples.drm.basic

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.bitmovin.player.api.Player
import com.bitmovin.player.api.drm.WidevineConfig
import com.bitmovin.player.api.source.SourceConfig
import com.bitmovin.player.samples.drm.basic.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var player: Player? = null
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
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

        player = binding.playerView.player
        initializePlayer()
    }

    override fun onStart() {
        binding.playerView.onStart()
        super.onStart()
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
        // Create a new source config
        val sourceConfig = SourceConfig.fromUrl("https://cdn.bitmovin.com/content/assets/art-of-motion_drm/mpds/11331.mpd")

        // Attach DRM handling to the source config
        sourceConfig.drmConfig = WidevineConfig("https://cwip-shaka-proxy.appspot.com/no_auth")

        // Load the source
        player?.load(sourceConfig)
    }
}
