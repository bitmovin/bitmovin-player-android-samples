package com.bitmovin.player.samples.vr.basic

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bitmovin.player.api.Player
import com.bitmovin.player.api.source.SourceConfig
import com.bitmovin.player.api.source.SourceType
import com.bitmovin.player.api.vr.VrConfig
import com.bitmovin.player.api.vr.VrContentType
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var player: Player

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        player = playerView.player!!

        // Enabling the gyroscopic controlling for the 360° video
        player.vr.isGyroscopeEnabled = true
        initializePlayer()
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

    private fun initializePlayer() {
        // Create a new SourceItem
        val vrSourceItem = SourceConfig(
                url = "https://bitmovin-a.akamaihd.net/content/playhouse-vr/mpds/105560.mpd",
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
