package com.bitmovin.player.samples.casting.basic

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bitmovin.player.api.Player
import com.bitmovin.player.api.source.SourceConfig
import com.bitmovin.player.casting.BitmovinCastManager
import com.bitmovin.player.samples.casting.basic.databinding.ActivityPlayerBinding

const val SOURCE_URL = "SOURCE_URL"
const val SOURCE_TITLE = "SOURCE_TITLE"

class PlayerActivity : AppCompatActivity() {
    private lateinit var player: Player
    private lateinit var binding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Update the context the BitmovinCastManager is using
        // This should be done in every Activity's onCreate using the cast function
        BitmovinCastManager.getInstance().updateContext(this)

        val sourceUrl = intent.getStringExtra(SOURCE_URL)
        val sourceTitle = intent.getStringExtra(SOURCE_TITLE)
        if (sourceUrl == null || sourceTitle == null) {
            finish()
            return
        }

        player = binding.bitmovinPlayerView.player!!

        initializePlayer(sourceUrl, sourceTitle)
    }

    override fun onStart() {
        binding.bitmovinPlayerView.onStart()
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.bitmovinPlayerView.onResume()
    }

    override fun onPause() {
        binding.bitmovinPlayerView.onPause()
        super.onPause()
    }

    override fun onStop() {
        binding.bitmovinPlayerView.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        binding.bitmovinPlayerView.onDestroy()
        super.onDestroy()
    }

    private fun initializePlayer(sourceUrl: String, sourceTitle: String) {
        // Create a new source item
        val sourceItem = SourceConfig.fromUrl(sourceUrl).apply {
            title = sourceTitle
        }

        // load source using the created source item
        player.load(sourceItem)
    }
}
