package com.bitmovin.player.samples.notification.basic;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.bitmovin.player.BitmovinPlayer;
import com.bitmovin.player.BitmovinPlayerView;
import com.bitmovin.player.config.media.SourceConfiguration;
import com.bitmovin.player.config.media.SourceItem;
import com.bitmovin.player.notification.BitmovinPlayerNotificationManager;

public class MainActivity extends AppCompatActivity
{
    private static final String NOTIFICATION_CHANNEL_ID = "com.bitmovin.player";
    private static final int NOTIFICATION_ID = 1;

    private BitmovinPlayerView bitmovinPlayerView;
    private BitmovinPlayer bitmovinPlayer;
    private BitmovinPlayerNotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.bitmovinPlayerView = (BitmovinPlayerView) this.findViewById(R.id.bitmovinPlayerView);
        this.bitmovinPlayer = this.bitmovinPlayerView.getPlayer();

        // Create a BitmovinPlayerNotificationManager with the static create method
        // By passing null for the mediaDescriptionAdapter, a DefaultMediaDescriptionAdapter will be used internally.
        this.notificationManager = BitmovinPlayerNotificationManager.createWithNotificationChannel(
                this, NOTIFICATION_CHANNEL_ID, R.string.control_notification_channel, NOTIFICATION_ID, null);
        // Allow to dismiss the Notification
        this.notificationManager.setOngoing(false);

        // Attaching the BitmovinPlayer to the BitmovinPlayerNotificationManager
        this.notificationManager.setPlayer(this.bitmovinPlayer);

        this.initializePlayer();
    }

    @Override
    protected void onStart()
    {
        this.bitmovinPlayerView.onStart();
        super.onStart();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        this.bitmovinPlayerView.onResume();
    }

    @Override
    protected void onPause()
    {
        this.bitmovinPlayerView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop()
    {
        this.bitmovinPlayerView.onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        // The BitmovinPlayer must be removed from the BitmovinPlayerNotificationManager before it is destroyed
        this.notificationManager.setPlayer(null);
        this.bitmovinPlayerView.onDestroy();
        super.onDestroy();
    }

    protected void initializePlayer()
    {
        // Create a new source configuration
        SourceConfiguration sourceConfiguration = new SourceConfiguration();

        // Add a new source item
        SourceItem sourceItem = new SourceItem("https://bitdash-a.akamaihd.net/content/sintel/sintel.mpd");
        sourceItem.setPosterSource("https://bitmovin-a.akamaihd.net/content/sintel/poster.png");
        sourceConfiguration.addSourceItem(sourceItem);

        // load source using the created source configuration
        this.bitmovinPlayer.load(sourceConfiguration);
    }
}
