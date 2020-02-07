package com.bitmovin.player.samples.custom.adaptation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.LinearLayout
import com.bitmovin.player.BitmovinPlayer
import com.bitmovin.player.BitmovinPlayerView
import com.bitmovin.player.config.AdaptationConfiguration
import com.bitmovin.player.config.PlayerConfiguration
import com.bitmovin.player.config.adaptation.VideoAdaptation
import com.bitmovin.player.config.media.SourceConfiguration
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var bitmovinPlayer: BitmovinPlayer? = null
    private var bitmovinPlayerView: BitmovinPlayerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val playerConfiguration = this.createPlayerConfiguration()
        this.bitmovinPlayerView = BitmovinPlayerView(this, playerConfiguration)
        this.bitmovinPlayer = this.bitmovinPlayerView?.player

        this.bitmovinPlayerView?.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        root.addView(this.bitmovinPlayerView, 0)
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

    private fun createPlayerConfiguration(): PlayerConfiguration {
        // Setup adaptation configuration
        val adaptationConfiguration = AdaptationConfiguration()
        adaptationConfiguration.isAllowRebuffering = true
        adaptationConfiguration.maxSelectableVideoBitrate = 800_000
        adaptationConfiguration.startupBitrate = 1_200_000
        adaptationConfiguration.videoAdaptation = videoAdaptationListener

        // Setup source configuration
        val sourceConfiguration = SourceConfiguration()
        sourceConfiguration.addSourceItem("https://bitdash-a.akamaihd.net/content/sintel/sintel.mpd")

        // Assign adaptation and source configuration to player configuration
        val playerConfiguration = PlayerConfiguration()
        playerConfiguration.adaptationConfiguration = adaptationConfiguration
        playerConfiguration.sourceConfiguration = sourceConfiguration
        return playerConfiguration
    }

    /*
     *  Customize this callback to return a different video quality id than what is suggested
     */
    private val videoAdaptationListener = VideoAdaptation { videoAdaptationData ->
        // Get the suggested video quality id
        val suggestedVideoQualityId = videoAdaptationData.suggested

        // Add your own logic to choose a different video quality
        val videoQualities = bitmovinPlayer?.availableVideoQualities

        // Return video quality id
        suggestedVideoQualityId
    }
}
