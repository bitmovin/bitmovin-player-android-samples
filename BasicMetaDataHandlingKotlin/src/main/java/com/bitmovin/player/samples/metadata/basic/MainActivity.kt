package com.bitmovin.player.samples.metadata.basic

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import com.bitmovin.player.BitmovinPlayer
import com.bitmovin.player.api.event.data.MetadataEvent
import com.bitmovin.player.api.event.listener.OnMetadataListener
import com.bitmovin.player.api.event.listener.OnMetadataParsedListener
import com.bitmovin.player.config.media.SourceConfiguration
import com.bitmovin.player.model.emsg.EventMessage
import com.bitmovin.player.model.id3.Id3Frame
import com.bitmovin.player.model.scte.ScteMessage
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var bitmovinPlayer: BitmovinPlayer? = null
    private var gson: Gson? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.gson = Gson()
        this.bitmovinPlayer = bitmovinPlayerView.player

        // Adding the metadata listener to the player
        this.bitmovinPlayer?.addEventListener(metadataParsedListener)
        this.bitmovinPlayer?.addEventListener(metadataListener)

        this.initializePlayer()
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
        // Removing metadata listener from player
        this.bitmovinPlayer?.removeEventListener(metadataParsedListener)
        this.bitmovinPlayer?.removeEventListener(metadataListener)
        this.bitmovinPlayerView.onDestroy()

        super.onDestroy()
    }

    private fun initializePlayer() {
        // Create a new source configuration
        val sourceConfiguration = SourceConfiguration()

        // Add a new source item
        //TODO: add source containing metadata
        sourceConfiguration.addSourceItem("")

        // load source using the created source configuration
        this.bitmovinPlayer?.load(sourceConfiguration)
    }

    private fun logMetadata(metadataEvent: MetadataEvent) {
        val metadata = metadataEvent.metadata

        when (metadataEvent.type) {
            ScteMessage.TYPE -> for (i in 0 until metadata.length()) {
                val scteMessage = metadata.get(i) as ScteMessage

                Log.i("METADATA", "SCTE: " + gson?.toJson(scteMessage))
            }
            Id3Frame.TYPE -> for (i in 0 until metadata.length()) {
                val id3Frame = metadata.get(i) as Id3Frame

                Log.i("METADATA", "ID3Frame: " + gson?.toJson(id3Frame))
            }
            EventMessage.TYPE -> for (i in 0 until metadata.length()) {
                val eventMessage = metadata.get(i) as EventMessage

                Log.i("METADATA", "EMSG: " + gson?.toJson(eventMessage))
            }
        }
    }

    // Metadata Listener
    private val metadataListener = OnMetadataListener { metadataEvent ->
        Log.i("METADATA", "onMetadata:")
        logMetadata(metadataEvent)
    }

    // Parsed Metadata Listener
    private val metadataParsedListener = OnMetadataParsedListener { metadataEvent ->
        Log.i("METADATA", "onMetadataParsed:")
        logMetadata(metadataEvent)
    }

}
