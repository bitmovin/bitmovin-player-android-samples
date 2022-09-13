package com.bitmovin.player.samples.notification.basic;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bitmovin.player.PlayerView;
import com.bitmovin.player.api.Player;
import com.bitmovin.player.api.source.SourceConfig;
import com.bitmovin.player.ui.notification.PlayerNotificationManager;

public class MainActivity extends AppCompatActivity {
    private static final String NOTIFICATION_CHANNEL_ID = "com.bitmovin.player";
    private static final int NOTIFICATION_ID = 1;

    private PlayerView playerView;
    private Player player;
    private PlayerNotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playerView = findViewById(R.id.playerView);
        player = playerView.getPlayer();

        // Create a PlayerNotificationManager with the static create method
        // By passing null for the mediaDescriptionAdapter, a DefaultMediaDescriptionAdapter will be used internally.
        notificationManager = PlayerNotificationManager.createWithNotificationChannel(
            this,
            NOTIFICATION_CHANNEL_ID,
            R.string.control_notification_channel,
            NOTIFICATION_ID,
            null
        );
        // Allow to dismiss the Notification
        notificationManager.setOngoing(false);

        // Attach the Player to the PlayerNotificationManager
        notificationManager.setPlayer(player);

        initializePlayer();
    }

    @Override
    protected void onStart() {
        playerView.onStart();
        super.onStart();
        requestMissingPermissions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        playerView.onResume();
    }

    @Override
    protected void onPause() {
        playerView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        playerView.onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // The BitmovinPlayer must be removed from the BitmovinPlayerNotificationManager before it is destroyed
        notificationManager.setPlayer(null);
        playerView.onDestroy();
        super.onDestroy();
    }

    protected void initializePlayer() {
        // Load a new source
        SourceConfig sourceConfig = SourceConfig.fromUrl("https://bitdash-a.akamaihd.net/content/sintel/sintel.mpd");
        sourceConfig.setPosterSource("https://bitmovin-a.akamaihd.net/content/sintel/poster.png");

        player.load(sourceConfig);
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> { /* Do nothing */ }
    );

    private void requestMissingPermissions() {
        if (Build.VERSION.SDK_INT < 33) return;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }
}
