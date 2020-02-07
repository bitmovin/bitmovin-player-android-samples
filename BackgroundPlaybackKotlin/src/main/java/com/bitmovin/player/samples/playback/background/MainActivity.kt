package com.bitmovin.player.samples.playback.background

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.bitmovin.player.BitmovinPlayer
import com.bitmovin.player.BitmovinPlayerView
import com.bitmovin.player.config.media.SourceConfiguration
import com.bitmovin.player.config.media.SourceItem
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var bitmovinPlayerView: BitmovinPlayerView? = null
    private var bitmovinPlayer: BitmovinPlayer? = null
    private var bound = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Create a BitmovinPlayerView without a BitmovinPlayer and add it to the View hierarchy
        this.bitmovinPlayerView = BitmovinPlayerView(this, null as BitmovinPlayer?)
        this.bitmovinPlayerView?.layoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        root.addView(this.bitmovinPlayerView)
    }

    override fun onStart() {
        super.onStart()
        this.bitmovinPlayerView?.onStart()
        // Bind and start the BackgroundPlaybackService
        val intent = Intent(this, BackgroundPlaybackService::class.java)
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
        // If the Service is not started, it would get destroyed as soon as the Activity unbinds.
        startService(intent)
    }

    override fun onResume() {
        super.onResume()
        // Attach the BitmovinPlayer to allow the BitmovinPlayerView to control the player
        this.bitmovinPlayerView?.player = this.bitmovinPlayer
        this.bitmovinPlayerView?.onResume()
    }

    override fun onPause() {
        // Detach the BitmovinPlayer to decouple it from the BitmovinPlayerView lifecycle
        this.bitmovinPlayerView?.player = null
        this.bitmovinPlayerView?.onPause()
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
        // Unbind the Service and reset the BitmovinPlayer reference
        unbindService(mConnection)
        this.bitmovinPlayer = null
        this.bound = false
        this.bitmovinPlayerView?.onStop()
    }

    override fun onDestroy() {
        this.bitmovinPlayerView?.onDestroy()
        super.onDestroy()
    }

    private fun initializePlayer() {
        // Create a new source configuration
        val sourceConfiguration = SourceConfiguration()

        // Add a new source item
        val sourceItem = SourceItem("https://bitmovin-a.akamaihd.net/content/MI201109210084_1/mpds/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.mpd")
        sourceItem.setPosterSource("https://bitmovin-a.akamaihd.net/content/poster/hd/RedBull.jpg")
        sourceConfiguration.addSourceItem(sourceItem)

        // load source using the created source configuration
        this.bitmovinPlayer?.load(sourceConfiguration)
    }

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private val mConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to the Service, cast the IBinder and get the BitmovinPlayer instance
            val binder = service as BackgroundPlaybackService.BackgroundBinder
            bitmovinPlayer = binder.player
            // Attach the BitmovinPlayer as soon as we have a reference
            bitmovinPlayerView?.player = bitmovinPlayer
            // If not already initialized, initialize the player with a source.
            if (bitmovinPlayer?.config?.sourceItem == null) {
                initializePlayer()
            }
            bound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            bound = false
        }
    }

}
