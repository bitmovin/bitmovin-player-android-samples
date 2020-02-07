package com.bitmovin.player.samples.casting.basic

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bitmovin.player.BitmovinPlayer
import com.bitmovin.player.cast.BitmovinCastManager
import com.bitmovin.player.config.media.SourceConfiguration
import com.bitmovin.player.config.media.SourceItem
import kotlinx.android.synthetic.main.activity_player.*

class PlayerActivity : AppCompatActivity() {

    companion object {
        const val SOURCE_URL = "SOURCE_URL"
        const val SOURCE_TITLE = "SOURCE_TITLE"
    }

    private var bitmovinPlayer: BitmovinPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        // Update the context the BitmovinCastManager is using
        // This should be done in every Activity's onCreate using the cast function
        BitmovinCastManager.getInstance().updateContext(this)

        val sourceUrl = intent.getStringExtra(SOURCE_URL)
        val sourceTitle = intent.getStringExtra(SOURCE_TITLE)
        if (sourceUrl == null || sourceTitle == null) {
            finish()
        }

        this.bitmovinPlayer = bitmovinPlayerView.player

        this.initializePlayer(sourceUrl, sourceTitle)
    }

    override fun onStart() {
        bitmovinPlayerView.onStart()
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        bitmovinPlayerView.onResume()
    }

    override fun onPause() {
        bitmovinPlayerView.onPause()
        super.onPause()
    }

    override fun onStop() {
        bitmovinPlayerView.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        bitmovinPlayerView.onDestroy()
        super.onDestroy()
    }

    private fun initializePlayer(sourceUrl: String, sourceTitle: String) {
        // Create a new source configuration
        val sourceConfiguration = SourceConfiguration()

        // Create a new source item
        val sourceItem = SourceItem(sourceUrl)
        sourceItem.title = sourceTitle

        // Add source item to source configuration
        sourceConfiguration.addSourceItem(sourceItem)

        // load source using the created source configuration
        this.bitmovinPlayer?.load(sourceConfiguration)
    }
}
