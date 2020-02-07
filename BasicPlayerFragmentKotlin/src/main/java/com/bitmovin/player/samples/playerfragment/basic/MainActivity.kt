package com.bitmovin.player.samples.playerfragment.basic

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bitmovin.player.BitmovinPlayerFragment
import com.bitmovin.player.config.media.SourceConfiguration

class MainActivity : AppCompatActivity() {

    private var playerFragment: BitmovinPlayerFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_main)

        // Create a new instance of BitmovinPlayerFragment
        this.playerFragment = BitmovinPlayerFragment.newInstance()

        // Begin a new Fragment transaction setting up the newly created fragment.
        // Alternatively the BitmovinPlayerFragment can also be setup via layout xml and then be fetched with FragmentManager.
        this.fragmentManager.beginTransaction().add(R.id.content_frame, this.playerFragment).commit()

        // Execute pending transactions so that fragment gets added immediately and PlayerView is available
        this.fragmentManager.executePendingTransactions()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // After fragment got loaded, we have full access to BitmovinPlayer and can initialize it
        this.initializePlayer()
    }

    private fun initializePlayer() {
        // Create a new source configuration
        val sourceConfiguration = SourceConfiguration()

        // Add a new source item
        sourceConfiguration.addSourceItem("https://bitdash-a.akamaihd.net/content/sintel/sintel.mpd")

        // load source using the created source configuration
        this.playerFragment?.player?.load(sourceConfiguration)
    }
}
