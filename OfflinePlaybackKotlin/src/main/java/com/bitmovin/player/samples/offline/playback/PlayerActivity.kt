package com.bitmovin.player.samples.offline.playback

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bitmovin.player.BitmovinPlayer
import com.bitmovin.player.config.media.SourceItem
import com.bitmovin.player.offline.OfflineSourceItem
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_player.*

class PlayerActivity : AppCompatActivity() {

    companion object {
        const val SOURCE_ITEM = "SOURCE_ITEM"
        const val OFFLINE_SOURCE_ITEM = "OFFLINE_SOURCE_ITEM"
    }

    private var bitmovinPlayer: BitmovinPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val gson = Gson()

        val sourceItem: SourceItem
        when {
            intent.hasExtra(SOURCE_ITEM) -> {
                sourceItem = gson.fromJson(intent.getStringExtra(SOURCE_ITEM), SourceItem::class.java)
            }
            intent.hasExtra(OFFLINE_SOURCE_ITEM) -> {
                sourceItem = gson.fromJson(intent.getStringExtra(OFFLINE_SOURCE_ITEM), OfflineSourceItem::class.java)
            }
            else -> {
                finish()
                return
            }
        }

        this.bitmovinPlayer = bitmovinPlayerView.player

        this.initializePlayer(sourceItem)
    }

    override fun onStart() {
        super.onStart()
        bitmovinPlayerView.onStart()
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

    private fun initializePlayer(sourceItem: SourceItem) {
        // load source
        this.bitmovinPlayer?.load(sourceItem)
    }
}
