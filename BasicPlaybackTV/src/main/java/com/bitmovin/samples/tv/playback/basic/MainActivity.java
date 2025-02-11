package com.bitmovin.samples.tv.playback.basic;


import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bitmovin.analytics.api.AnalyticsConfig;
import com.bitmovin.player.PlayerView;
import com.bitmovin.player.api.PlaybackConfig;
import com.bitmovin.player.api.Player;
import com.bitmovin.player.api.PlayerBuilder;
import com.bitmovin.player.api.PlayerConfig;
import com.bitmovin.player.api.source.SourceConfig;
import com.bitmovin.player.api.source.SourceType;
import com.bitmovin.player.api.ui.PlayerViewConfig;
import com.bitmovin.player.api.ui.ScalingMode;
import com.bitmovin.player.api.ui.SurfaceType;
import com.bitmovin.player.api.ui.UiConfig;
import com.bitmovin.player.samples.tv.playback.basic.R;

public class MainActivity extends AppCompatActivity {

    private PlayerView playerView;
    private Player player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        // Switch from splash screen to main theme when we are done loading
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initializePlayer();
    }

    private void initializePlayer() {
        String key = "{ANALYTICS_LICENSE_KEY}";
        player = new PlayerBuilder(this)
                .setPlayerConfig(createPlayerConfig())
                .configureAnalytics(new AnalyticsConfig(key))
                .build();

        PlayerViewConfig viewConfig = new PlayerViewConfig(
                new UiConfig.WebUi(
                        "file:///android_asset/bitmovinplayer-ui.css",
                        null,
                        "file:///android_asset/bitmovinplayer-ui.js",
                        true,
                        false,
                        null,
                        UiConfig.WebUi.Variant.TvUi.INSTANCE,
                        true
                ),
                false,
                ScalingMode.Fit,
                false,
                SurfaceType.SurfaceView
        );

        playerView = new PlayerView(this, player, viewConfig);

        playerView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));
        playerView.setKeepScreenOn(true);

        LinearLayout layout = findViewById(R.id.playerRootLayout);
        layout.addView(playerView, 0);

        // Create a new SourceItem. In this case we are loading a DASH source.
        String sourceURL = "https://cdn.bitmovin.com/content/assets/MI201109210084/mpds/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.mpd";
        SourceConfig sourceConfig = new SourceConfig(sourceURL, SourceType.Dash);

        player.load(sourceConfig);
    }

    private PlayerConfig createPlayerConfig() {
        // Creating a new PlayerConfig
        PlayerConfig playerConfig = new PlayerConfig();

        PlaybackConfig playbackConfig = new PlaybackConfig();
        playbackConfig.setAutoplayEnabled(true);
        playerConfig.setPlaybackConfig(playbackConfig);

        return playerConfig;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // This method is called on key down and key up, so avoid being called twice
        if (playerView != null && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (handleUserInput(event.getKeyCode())) {
                return true;
            }
        }

        // Make sure to return super.dispatchKeyEvent(event) so that any key not handled yet will work as expected
        return super.dispatchKeyEvent(event);
    }

    private boolean handleUserInput(int keycode) {
        return switch (keycode) {
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> {
                togglePlay();
                yield true;
            }
            case KeyEvent.KEYCODE_MEDIA_PLAY -> {
                player.play();
                yield true;
            }
            case KeyEvent.KEYCODE_MEDIA_PAUSE -> {
                player.pause();
                yield true;
            }
            default -> false;
        };
    }

    private void togglePlay() {
        if (player.isPlaying()) {
            player.pause();
        } else {
            player.play();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        playerView.onResume();
        player.play();
    }

    @Override
    protected void onStart() {
        super.onStart();
        playerView.onStart();
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
        playerView.onDestroy();
        super.onDestroy();
    }
}
