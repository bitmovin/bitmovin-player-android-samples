package com.bitmovin.player.samples.media.session

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.bitmovin.player.api.PlaybackConfig
import com.bitmovin.player.api.Player
import com.bitmovin.player.api.PlayerConfig
import com.bitmovin.player.api.media.session.ControllerInfo
import com.bitmovin.player.api.media.session.MediaSession
import com.bitmovin.player.api.media.session.MediaSessionService

class MediaSessionPlaybackService : MediaSessionService() {
    inner class ServiceBinder : Binder() {
        val player get() = this@MediaSessionPlaybackService.player
        fun connectSession() = addSession(mediaSession)
        fun disconnectSession() = removeSession(mediaSession)
    }

    private val binder = ServiceBinder()
    private lateinit var player: Player
    private lateinit var mediaSession: MediaSession

    override fun onGetSession(controllerInfo: ControllerInfo) = mediaSession

    override fun onCreate() {
        super.onCreate()
        player = Player(
            this, PlayerConfig(
                playbackConfig = PlaybackConfig(
                    handleAudioFocus = true
                )
            )
        )
        mediaSession = MediaSession(
            this,
            mainLooper,
            player,
        )
    }

    override fun onDestroy() {
        mediaSession.release()
        player.destroy()

        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder {
        super.onBind(intent)
        return binder
    }
}
