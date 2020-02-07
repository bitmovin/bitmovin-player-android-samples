package com.bitmovin.player.samples.custom.ui.subtitleview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.ViewGroup
import com.bitmovin.player.BitmovinPlayer
import com.bitmovin.player.BitmovinPlayerView
import com.bitmovin.player.BitmovinSubtitleView
import com.bitmovin.player.config.PlayerConfiguration
import com.bitmovin.player.config.StyleConfiguration
import com.bitmovin.player.config.media.SourceConfiguration
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var bitmovinPlayer: BitmovinPlayer? = null
    private var bitmovinPlayerView: BitmovinPlayerView? = null
    private var bitmovinSubtitleView: BitmovinSubtitleView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Create new StyleConfiguration
        val styleConfiguration = StyleConfiguration()
        // Disable default Bitmovin UI
        styleConfiguration.isUiEnabled = false

        // Create a new source configuration
        val sourceConfiguration = SourceConfiguration()
        // Add a new source item
        sourceConfiguration.addSourceItem("https://bitmovin-a.akamaihd.net/content/sintel/sintel.mpd")

        // Creating a new PlayerConfiguration
        val playerConfiguration = PlayerConfiguration()
        // Assign created StyleConfiguration to the PlayerConfiguration
        playerConfiguration.styleConfiguration = styleConfiguration
        // Assign created SourceConfiguration to the PlayerConfiguration
        playerConfiguration.sourceConfiguration = sourceConfiguration

        // Creating a BitmovinPlayerView and get it's BitmovinPlayer instance.
        this.bitmovinPlayerView = BitmovinPlayerView(this, playerConfiguration)
        this.bitmovinPlayerView?.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        this.bitmovinPlayer = this.bitmovinPlayerView?.player

        // Creating a BitmovinSubtitleView and assign the current player instance.
        this.bitmovinSubtitleView = BitmovinSubtitleView(this)
        this.bitmovinSubtitleView?.setPlayer(this.bitmovinPlayer)

        // Setup minimalistic controls for the player
        bitmovinPlayer?.let { player -> playerControls.setPlayer(player) }

        // Add the BitmovinSubtitleView to the layout
        playerContainer.addView(this.bitmovinSubtitleView)

        // Add the BitmovinPlayerView to the layout as first position (so it is the behind the SubtitleView)
        playerContainer.addView(this.bitmovinPlayerView, 0)
    }

    override fun onStart() {
        super.onStart()
        this.bitmovinPlayerView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        this.bitmovinPlayerView?.onResume()
    }

    override fun onPause() {
        this.bitmovinPlayerView?.onPause()
        super.onPause()
    }

    override fun onStop() {
        this.bitmovinPlayerView?.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        this.bitmovinPlayerView?.onDestroy()
        super.onDestroy()
    }
}
