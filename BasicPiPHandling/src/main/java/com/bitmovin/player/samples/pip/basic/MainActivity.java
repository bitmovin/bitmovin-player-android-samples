package com.bitmovin.player.samples.pip.basic;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bitmovin.player.PlayerView;
import com.bitmovin.player.api.Player;
import com.bitmovin.player.api.event.EventListener;
import com.bitmovin.player.api.event.PlayerEvent;
import com.bitmovin.player.api.source.SourceConfig;
import com.bitmovin.player.api.source.SourceType;
import com.bitmovin.player.ui.DefaultPictureInPictureHandler;

public class MainActivity extends AppCompatActivity {
    private PlayerView playerView;
    private Player player;
    private boolean playerShouldPause = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playerView = findViewById(R.id.bitmovinPlayerView);
        player = playerView.getPlayer();

        // Create a PictureInPictureHandler and set it on the PlayerView
        DefaultPictureInPictureHandler pictureInPictureHandler = new DefaultPictureInPictureHandler(this, player);
        playerView.setPictureInPictureHandler(pictureInPictureHandler);

        initializePlayer();
    }

    @Override
    protected void onStart() {
        playerView.onStart();
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Add the PictureInPictureEnterListener to the PlayerView
        playerView.on(PlayerEvent.PictureInPictureEnter.class, pipEnterListener);

        playerView.onResume();
    }

    @Override
    protected void onPause() {
        if (playerShouldPause) {
            playerView.onPause();
        }
        playerShouldPause = true;

        playerView.off(PlayerEvent.PictureInPictureEnter.class, pipEnterListener);

        super.onPause();
    }

    @Override
    protected void onStop() {
        playerView.onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        playerView.onDestroy();
        super.onDestroy();
    }

    protected void initializePlayer() {
        // load source using a source item
        player.load(new SourceConfig("https://bitdash-a.akamaihd.net/content/sintel/sintel.mpd", SourceType.Dash));
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);

        // Hiding the ActionBar
        if (isInPictureInPictureMode) {
            getSupportActionBar().hide();
        } else {
            getSupportActionBar().show();
        }
        playerView.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
    }

    private EventListener<PlayerEvent.PictureInPictureEnter> pipEnterListener = new EventListener<PlayerEvent.PictureInPictureEnter>() {
        @Override
        public void onEvent(PlayerEvent.PictureInPictureEnter pictureInPictureEnter) {
            // Android fires an onPause on the Activity when entering PiP mode.
            // However, we do not want the PlayerView to act on
            playerShouldPause = false;
        }
    };
}
