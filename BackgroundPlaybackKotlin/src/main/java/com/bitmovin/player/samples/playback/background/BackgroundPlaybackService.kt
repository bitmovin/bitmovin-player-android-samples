package com.bitmovin.player.samples.playback.background

import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.bitmovin.player.BitmovinPlayer
import com.bitmovin.player.notification.BitmovinPlayerNotificationManager
import com.bitmovin.player.notification.CustomActionReceiver
import com.bitmovin.player.notification.DefaultMediaDescriptor
import com.bitmovin.player.notification.NotificationListener
import java.util.*

class BackgroundPlaybackService : Service() {

    private val NOTIFICATION_CHANNEL_ID = "com.bitmovin.player"
    private val NOTIFICATION_ID = 1

    // Binder given to clients
    private val binder = BackgroundBinder()

    private var player: BitmovinPlayer? = null
    private var playerNotificationManager: BitmovinPlayerNotificationManager? = null

    /**
     * Class used for the client Binder. Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    inner class BackgroundBinder : Binder() {
        // Return this instance of BitmovinPlayer so clients can use the player instance
        val player: BitmovinPlayer?
            get() = this@BackgroundPlaybackService.player
    }

    override fun onCreate() {
        super.onCreate()
        this.player = BitmovinPlayer(this)

        // Create a BitmovinPlayerNotificationManager with the static create method
        // By passing null for the mediaDescriptionAdapter, a DefaultMediaDescriptionAdapter will be used internally.
        this.playerNotificationManager =
            BitmovinPlayerNotificationManager.createWithNotificationChannel(
                this,
                NOTIFICATION_CHANNEL_ID,
                R.string.control_notification_channel,
                NOTIFICATION_ID,
                DefaultMediaDescriptor(this.assets)
            )

        this.playerNotificationManager?.setNotificationListener(object : NotificationListener {
            override fun onNotificationStarted(notificationId: Int, notification: Notification) {
                startForeground(notificationId, notification)
            }

            override fun onNotificationCancelled(notificationId: Int) {
                stopSelf()
            }
        })

        // Attaching the BitmovinPlayer to the BitmovinPlayerNotificationManager
        this.playerNotificationManager?.setPlayer(this.player)
    }

    override fun onDestroy() {
        this.playerNotificationManager?.setPlayer(null)
        this.player?.destroy()
        this.player = null

        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        return this.binder
    }

    override fun onUnbind(intent: Intent): Boolean {
        return super.onUnbind(intent)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int = START_STICKY
}
