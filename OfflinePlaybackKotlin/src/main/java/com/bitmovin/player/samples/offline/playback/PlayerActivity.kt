package com.bitmovin.player.samples.offline.playback

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bitmovin.player.api.Player
import com.bitmovin.player.api.source.SourceConfig
import com.bitmovin.player.samples.offline.playback.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity() {

    companion object {
        const val SOURCE_CONFIG = "SOURCE_CONFIG"
        const val OFFLINE_SOURCE_CONFIG = "OFFLINE_SOURCE_CONFIG"
    }

    private var player: Player? = null

    private lateinit var binding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sourceConfig: SourceConfig = when {
            intent.hasExtra(SOURCE_CONFIG) -> {
                intent.getParcelableExtra(SOURCE_CONFIG)!!
            }
            intent.hasExtra(OFFLINE_SOURCE_CONFIG) -> {
                intent.getParcelableExtra(OFFLINE_SOURCE_CONFIG)!!
            }
            else -> {
                finish()
                return
            }
        }

        player = binding.playerView.player

        initializePlayer(sourceConfig)
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

    private fun initializePlayer(sourceConfig: SourceConfig) {
        // load source
        player?.load(sourceConfig)
    }
}
