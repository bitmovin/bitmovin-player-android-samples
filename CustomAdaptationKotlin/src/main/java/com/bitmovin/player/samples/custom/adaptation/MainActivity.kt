package com.bitmovin.player.samples.custom.adaptation

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.bitmovin.player.PlayerView
import com.bitmovin.player.api.Player
import com.bitmovin.player.api.PlayerConfig
import com.bitmovin.player.api.media.AdaptationConfig
import com.bitmovin.player.api.media.video.quality.VideoAdaptation
import com.bitmovin.player.api.source.SourceConfig
import com.bitmovin.player.api.source.SourceType
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var player: Player
    private lateinit var playerView: PlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        player = Player.create(this, createPlayerConfig())
        playerView = PlayerView(this, player)

        player.load(SourceConfig("https://bitdash-a.akamaihd.net/content/sintel/sintel.mpd", SourceType.Dash))

        playerView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        root.addView(playerView, 0)
    }

    override fun onStart() {
        super.onStart()
        playerView.onStart()
    }

    override fun onResume() {
        super.onResume()
        playerView.onResume()
    }

    override fun onPause() {
        playerView.onPause()
        super.onPause()
    }

    override fun onStop() {
        playerView.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        playerView.onDestroy()
        super.onDestroy()
    }

    /**
     * Setup PlayerConfig with custom adaption config
     */
    private fun createPlayerConfig(): PlayerConfig =
            PlayerConfig(
                    adaptationConfig = AdaptationConfig(
                            isRebufferingAllowed = true,
                            maxSelectableVideoBitrate = 800_000,
                            startupBitrate = 1_200_000,
                    ).apply {
                        videoAdaptation = videoAdaptationListener
                    })

    /*
     *  Customize this callback to return a different video quality id than what is suggested
     */
    private val videoAdaptationListener = VideoAdaptation { videoAdaptationData ->
        // Get the suggested video quality id
        val suggestedVideoQualityId = videoAdaptationData.suggested

        // Add your own logic to choose a different video quality
        val videoQualities = player.availableVideoQualities

        // Return video quality id
        suggestedVideoQualityId
    }
}
