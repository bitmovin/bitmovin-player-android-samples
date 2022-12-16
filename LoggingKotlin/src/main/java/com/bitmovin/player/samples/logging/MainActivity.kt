package com.bitmovin.player.samples.logging

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bitmovin.player.api.Player
import com.bitmovin.player.api.source.Source
import com.bitmovin.player.api.source.SourceConfig
import com.bitmovin.player.samples.logging.databinding.ActivityMainBinding

private const val ART_OF_MOTION = "https://bitmovin-a.akamaihd.net/content/MI201109210084_1/mpds/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.mpd"

class MainActivity : AppCompatActivity() {
    private lateinit var player: Player
    private lateinit var binding: ActivityMainBinding

    // Initialize loggers
    private val playerLogger = EventLogger(LoggerConfig.PlayerLoggerConfig())
    private val sourceLogger = EventLogger(LoggerConfig.SourceLoggerConfig())
    private val viewLogger = EventLogger(LoggerConfig.ViewLoggerConfig())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializePlayer()
    }

    private fun initializePlayer() {
        player = Player.create(this).also { binding.playerView.player = it }
        val source = Source.create(SourceConfig.fromUrl(ART_OF_MOTION))

        // Attach all loggers to their respective components
        playerLogger.attach(player)
        sourceLogger.attach(source)
        viewLogger.attach(binding.playerView)

        player.load(source)
    }

    override fun onStart() {
        super.onStart()
        binding.playerView.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.playerView.onResume()
    }

    override fun onPause() {
        binding.playerView.onPause()
        super.onPause()
    }

    override fun onStop() {
        binding.playerView.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        binding.playerView.onDestroy()

        playerLogger.detach()
        sourceLogger.detach()
        viewLogger.detach()

        super.onDestroy()
    }
}
