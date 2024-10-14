package com.bitmovin.samples.tv.playback.basic;


import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.bitmovin.analytics.api.AnalyticsConfig;
import com.bitmovin.player.PlayerView;
import com.bitmovin.player.api.PlaybackConfig;
import com.bitmovin.player.api.Player;
import com.bitmovin.player.api.PlayerBuilder;
import com.bitmovin.player.api.PlayerConfig;
import com.bitmovin.player.api.event.EventListener;
import com.bitmovin.player.api.event.PlayerEvent;
import com.bitmovin.player.api.event.SourceEvent;
import com.bitmovin.player.api.source.SourceConfig;
import com.bitmovin.player.api.source.SourceType;
import com.bitmovin.player.api.ui.PlayerViewConfig;
import com.bitmovin.player.api.ui.ScalingMode;
import com.bitmovin.player.api.ui.UiConfig;
import com.bitmovin.player.samples.tv.playback.basic.R;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int SEEKING_OFFSET = 10;

    private PlayerView playerView;
    private Player player;
    private Double pendingSeekTarget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        // Here a custom bitmovinplayer-ui.js is loaded which utilizes the cast-UI as this matches our needs here perfectly.
        // I.e. UI controls get shown / hidden whenever the Player API is called. This is needed due to the fact that on Android TV no touch events are received
        PlayerViewConfig viewConfig = new PlayerViewConfig(
                new UiConfig.WebUi(
                        "file:///android_asset/bitmovinplayer-ui.css",
                        null,
                        "file:///android_asset/bitmovinplayer-ui.js",
                        true,
                        false,
                        null
                ),
                false,
                ScalingMode.Fit
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
        String sourceURL = "https://bitmovin-a.akamaihd.net/content/MI201109210084_1/mpds/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.mpd";
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
    protected void onResume() {
        super.onResume();

        playerView.onResume();
        addEventListener();
        player.play();
    }

    @Override
    protected void onStart() {
        super.onStart();
        playerView.onStart();
    }

    @Override
    protected void onPause() {
        removeEventListener();
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
        Log.d(TAG, "Keycode " + keycode);
        switch (keycode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_NUMPAD_ENTER:
            case KeyEvent.KEYCODE_SPACE:
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                togglePlay();
                return true;
            case KeyEvent.KEYCODE_MEDIA_PLAY:
                player.play();
                return true;
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
                player.pause();
                return true;
            case KeyEvent.KEYCODE_MEDIA_STOP:
                stopPlayback();
                return true;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
                seekForward();
                return true;
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_MEDIA_REWIND:
                seekBackward();
                return true;
            default:
                return false;
        }
    }

    private void togglePlay() {
        if (player.isPlaying()) {
            player.pause();
        } else {
            player.play();
        }
    }

    private void stopPlayback() {
        player.pause();
        player.seek(0);
    }

    private void seekForward() {
        seekRelative(SEEKING_OFFSET);
    }

    private void seekBackward() {
        seekRelative(-SEEKING_OFFSET);
    }

    private void seekRelative(double seekAmount) {
        double seekStart = pendingSeekTarget == null ? player.getCurrentTime() : pendingSeekTarget;
        double seekTarget = seekStart + seekAmount;
        pendingSeekTarget = seekTarget;
        player.seek(seekTarget);
    }


    private void addEventListener() {
        if (player == null) return;

        player.on(PlayerEvent.Error.class, onPlayerError);
        player.on(SourceEvent.Error.class, onSourceError);
        player.on(PlayerEvent.Seeked.class, onSeeked);
    }

    private void removeEventListener() {
        if (player == null) return;

        player.off(onPlayerError);
        player.off(onSourceError);
        player.off(onSeeked);
    }

    private final EventListener<PlayerEvent.Error> onPlayerError = errorEvent ->
            Log.e(TAG, "A player error occurred (" + errorEvent.getCode() + "): " + errorEvent.getMessage());

    private final EventListener<SourceEvent.Error> onSourceError = errorEvent ->
            Log.e(TAG, "A source error occurred (" + errorEvent.getCode() + "): " + errorEvent.getMessage());

    private final EventListener<PlayerEvent.Seeked> onSeeked = event -> pendingSeekTarget = null;

}
