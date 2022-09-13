package com.bitmovin.player.samples.playback.background;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bitmovin.player.PlayerView;
import com.bitmovin.player.api.Player;
import com.bitmovin.player.api.source.SourceConfig;
import com.bitmovin.player.api.source.SourceType;

public class MainActivity extends AppCompatActivity {
    private PlayerView playerView;
    private Player player;
    private boolean bound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create a PlayerView without a Player and add it to the View hierarchy
        RelativeLayout rootLayout = this.findViewById(R.id.root);
        playerView = new PlayerView(this, (Player) null);
        playerView.setLayoutParams(
            new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        );
        rootLayout.addView(playerView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        playerView.onStart();
        requestMissingPermissions();

        // Bind and start the BackgroundPlaybackService
        Intent intent = new Intent(this, BackgroundPlaybackService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        // If the Service is not started, it would get destroyed as soon as the Activity unbinds.
        startService(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Attach the Player to allow the PlayerView to control the player
        playerView.setPlayer(player);
        playerView.onResume();
    }

    @Override
    protected void onPause() {
        // Detach the Player to decouple it from the PlayerView lifecycle
        playerView.setPlayer(null);
        playerView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Unbind the Service and reset the Player reference
        unbindService(mConnection);
        player = null;
        bound = false;
        playerView.onStop();
    }

    @Override
    protected void onDestroy() {
        playerView.onDestroy();
        super.onDestroy();
    }

    protected void initializePlayer() {
        // Load a new source
        SourceConfig sourceConfig = new SourceConfig(
            "https://bitmovin-a.akamaihd.net/content/MI201109210084_1/mpds/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.mpd",
            SourceType.Dash
        );
        sourceConfig.setPosterSource("https://bitmovin-a.akamaihd.net/content/poster/hd/RedBull.jpg");

        player.load(sourceConfig);
    }

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to the Service, cast the IBinder and get the Player instance
            BackgroundPlaybackService.BackgroundBinder binder = (BackgroundPlaybackService.BackgroundBinder) service;
            player = binder.getPlayer();

            // Attach the Player as soon as we have a reference
            playerView.setPlayer(player);

            // If not already initialized, initialize the player with a source.
            if (player.getSource() == null) {
                initializePlayer();
            }

            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
        }
    };

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
