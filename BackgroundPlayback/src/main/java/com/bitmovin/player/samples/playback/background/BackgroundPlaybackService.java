package com.bitmovin.player.samples.playback.background;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bitmovin.player.api.Player;

import com.bitmovin.player.api.ui.notification.CustomActionReceiver;
import com.bitmovin.player.api.ui.notification.NotificationListener;
import com.bitmovin.player.ui.notification.DefaultMediaDescriptor;
import com.bitmovin.player.ui.notification.PlayerNotificationManager;
import com.google.android.exoplayer2.util.NotificationUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BackgroundPlaybackService extends Service {
    private static final String NOTIFICATION_CHANNEL_ID = "com.bitmovin.player";
    private static final int NOTIFICATION_ID = 1;

    // Binder given to clients
    private final IBinder binder = new BackgroundBinder();

    private Player player;
    private PlayerNotificationManager playerNotificationManager;

    /**
     * Class used for the client Binder. Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class BackgroundBinder extends Binder {
        Player getPlayer() {
            // Return this instance of Player so clients can use the player instance
            return BackgroundPlaybackService.this.player;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        player = Player.create(this);

        // Create a PlayerNotificationManager with the static create method
        // By passing null for the mediaDescriptionAdapter, a DefaultMediaDescriptionAdapter will be used internally.
        this.playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(
                this,
                NOTIFICATION_CHANNEL_ID,
                R.string.control_notification_channel,
                NOTIFICATION_ID,
                new DefaultMediaDescriptor(this.getAssets())
            );

        playerNotificationManager.setNotificationListener(new NotificationListener() {
            @Override
            public void onNotificationStarted(int notificationId, Notification notification) {
                startForeground(notificationId, notification);
            }

            @Override
            public void onNotificationCancelled(int notificationId) {
                stopSelf();
            }
        });

        // Attaching the Player to the PlayerNotificationManager
        playerNotificationManager.setPlayer(player);
    }

    @Override
    public void onDestroy() {
        playerNotificationManager.setPlayer(null);
        player.destroy();
        player = null;

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
}
