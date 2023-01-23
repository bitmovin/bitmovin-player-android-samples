package com.bitmovin.player.samples.metadata.basic

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import com.bitmovin.player.api.Player
import com.bitmovin.player.api.event.PlayerEvent
import com.bitmovin.player.api.event.SourceEvent
import com.bitmovin.player.api.event.on
import com.bitmovin.player.api.metadata.emsg.EventMessage
import com.bitmovin.player.api.metadata.id3.Id3Frame
import com.bitmovin.player.api.metadata.scte.ScteMessage
import com.bitmovin.player.api.source.SourceConfig
import com.bitmovin.player.samples.metadata.basic.databinding.ActivityMainBinding
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    private lateinit var player: Player
    private val gson: Gson = Gson()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        player = binding.bitmovinPlayerView.player!!

        // Adding the metadata listener to the player
        player.on(::onMetadataParsed)
        player.on(::onMetadata)

        initializePlayer()
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
        // Removing metadata listener from player
        player.off(::onMetadataParsed)
        player.off(::onMetadata)
        binding.bitmovinPlayerView.onDestroy()

        super.onDestroy()
    }

    private fun initializePlayer() {
        //TODO: add source containing metadata

        // load source using the created source item
        player.load(SourceConfig.fromUrl(""))
    }

    private fun logMetadata(metadata: com.bitmovin.player.api.metadata.Metadata, type: String) {
        when (type) {
            ScteMessage.TYPE -> (0 until metadata.length())
                    .map { metadata.get(it) as ScteMessage }
                    .forEach { Log.i("METADATA", "SCTE: " + gson.toJson(it)) }
            Id3Frame.TYPE -> (0 until metadata.length())
                    .map { metadata.get(it) as Id3Frame }
                    .forEach { Log.i("METADATA", "ID3Frame: " + gson.toJson(it)) }
            EventMessage.TYPE -> (0 until metadata.length())
                    .map { metadata.get(it) as EventMessage }
                    .forEach { Log.i("METADATA", "EMSG: " + gson.toJson(it)) }
        }
    }

    private fun onMetadataParsed(event: SourceEvent.MetadataParsed){
        Log.i("METADATA", "onMetadataParsed:")
        logMetadata(event.metadata, event.type)
    }

    private fun onMetadata(event: PlayerEvent.Metadata){
        Log.i("METADATA", "onMetadata:")
        logMetadata(event.metadata, event.type)
    }

}
