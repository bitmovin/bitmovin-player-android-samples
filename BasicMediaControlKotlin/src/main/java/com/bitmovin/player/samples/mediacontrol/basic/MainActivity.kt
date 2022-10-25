package com.bitmovin.player.samples.mediacontrol.basic

import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import androidx.appcompat.app.AppCompatActivity
import com.bitmovin.player.PlayerView
import com.bitmovin.player.api.Player
import com.bitmovin.player.api.source.SourceConfig
import com.bitmovin.player.ui.notification.PlayerNotificationManager

class MainActivity : AppCompatActivity() {
    private val notificationChannelId = "com.bitmovin.player"
    private val notificationId = 1

    private lateinit var playerView: PlayerView
    private var player: Player? = null
    private lateinit var notificationManager: PlayerNotificationManager

    private lateinit var mediaSessionCompat: MediaSessionCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playerView = findViewById(R.id.playerView)
        player = playerView.player

        notificationManager = PlayerNotificationManager.createWithNotificationChannel(
                this,
                notificationChannelId,
                R.string.control_notification_channel,
                notificationId,
                null
        )
        notificationManager.setOngoing(true)
        notificationManager.setPlayer(player)

        initializePlayer()

        mediaSessionCompat = MediaSessionCompat(this, "Bitmovin Sample Media Session")
        notificationManager.setMediaSessionToken(mediaSessionCompat.sessionToken)

        requestAudioFocus()
    }

    override fun onDestroy() {
        // The BitmovinPlayer must be removed from the BitmovinPlayerNotificationManager before it is destroyed
        notificationManager.setPlayer(null)
        playerView.onDestroy()
        mediaSessionCompat.release()
        super.onDestroy()
    }

    override fun onStart() {
        playerView.onStart()
        super.onStart()
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

    private fun initializePlayer() {
        // Load a new source
        val sourceConfig = SourceConfig.fromUrl(
            "https://bitmovin-a.akamaihd.net/content/MI201109210084_1/mpds/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.mpd"
        ).apply {
            posterSource = "https://bitmovin-a.akamaihd.net/content/poster/hd/RedBull.jpg"
            title = "Art of Motion"
            description = "Red Bull's Parkour event, this time in Santorini"
        }
        player?.load(sourceConfig)
    }

    private fun requestAudioFocus() {
        if (Build.VERSION.SDK_INT < 26) {
            requestAudioFocusBeforeApi26()
            return
        }

        val am = getSystemService(AUDIO_SERVICE) as AudioManager
        val focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN) // API 26
                .setAudioAttributes(
                        AudioAttributes.Builder() // API 21
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
                                .build()
                )
                .setAcceptsDelayedFocusGain(true)
                .setOnAudioFocusChangeListener { focusChange ->
                    // This listener is required or an exception will be thrown
                    // Default and expected behavior differs by Android versions. More details can be found at
                    // https://developer.android.com/guide/topics/media-apps/audio-focus

                    if (focusChange != AudioManager.AUDIOFOCUS_GAIN) {
                        player?.pause()
                    }
                }
                .build()

        val result = am.requestAudioFocus(focusRequest)

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            // App has audiofocus now
            player?.play()
        }
    }

    private fun requestAudioFocusBeforeApi26() {
        val am = getSystemService(AUDIO_SERVICE) as AudioManager
        val result = am.requestAudioFocus(
                { focusChange ->
                    // onAudioFocusChangeListener needs to handle stopping/ducking media if focus is lost.
                    // More details can be found at https://developer.android.com/guide/topics/media-apps/audio-focus

                    if (focusChange != AudioManager.AUDIOFOCUS_GAIN) {
                        player?.pause()
                    }
                },
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
        )

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            // App has audiofocus now
            player?.play()
        }
    }
}