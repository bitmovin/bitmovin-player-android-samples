package com.bitmovin.player.samples.vr.basic

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.bitmovin.player.api.Player
import com.bitmovin.player.api.source.SourceConfig
import com.bitmovin.player.api.source.SourceType
import com.bitmovin.player.api.vr.VrConfig
import com.bitmovin.player.api.vr.VrContentType
import com.bitmovin.player.samples.vr.basic.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var player: Player
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

        player = binding.playerView.player!!

        // Enabling the gyroscopic controlling for the 360° video
        player.vr.isGyroscopeEnabled = true
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
        // Create a new SourceItem
        val vrSourceItem = SourceConfig(
                url = "https://cdn.bitmovin.com/content/assets/playhouse-vr/mpds/105560.mpd",
                type = SourceType.Dash,
                vrConfig = VrConfig(
                        vrContentType = VrContentType.Single,
                        startPosition = 180.0
                )
        )

        // load source using the created source item
        player.load(vrSourceItem)
    }
}
