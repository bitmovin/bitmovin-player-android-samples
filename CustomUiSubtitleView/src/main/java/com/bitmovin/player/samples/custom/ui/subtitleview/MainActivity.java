package com.bitmovin.player.samples.custom.ui.subtitleview;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.bitmovin.analytics.api.AnalyticsConfig;
import com.bitmovin.player.PlayerView;
import com.bitmovin.player.SubtitleView;
import com.bitmovin.player.api.Player;
import com.bitmovin.player.api.PlayerConfig;
import com.bitmovin.player.api.analytics.PlayerFactory;
import com.bitmovin.player.api.source.SourceConfig;
import com.bitmovin.player.api.source.SourceType;
import com.bitmovin.player.api.ui.PlayerViewConfig;
import com.bitmovin.player.api.ui.ScalingMode;
import com.bitmovin.player.api.ui.StyleConfig;
import com.bitmovin.player.api.ui.UiConfig;

public class MainActivity extends AppCompatActivity {
    private Player player;
    private PlayerView playerView;
    private SubtitleView subtitleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RelativeLayout playerContainer = findViewById(R.id.player_container);

        // Creating a PlayerView and get it's Player instance.
        String key = "{ANALYTICS_LICENSE_KEY}";
        Player player = PlayerFactory.create(this, new PlayerConfig(), new AnalyticsConfig(key));
        PlayerViewConfig viewConfig = new PlayerViewConfig(
                // Disable default Player UI
                UiConfig.Disabled.INSTANCE,
                false,
                ScalingMode.Fit
        );
        playerView = new PlayerView(this,
                player,
                viewConfig
        );
        playerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        player.load(new SourceConfig("https://bitmovin-a.akamaihd.net/content/sintel/sintel.mpd", SourceType.Dash));

        // Creating a SubtitleView and assign the current player instance.
        subtitleView = new SubtitleView(this);
        subtitleView.setPlayer(player);

        // Setup minimalistic controls for the player
        PlayerControls playerControls = findViewById(R.id.player_controls);
        playerControls.setPlayer(player);

        // Add the SubtitleView to the layout
        playerContainer.addView(subtitleView);

        // Add the PlayerView to the layout as first position (so it is the behind the SubtitleView)
        playerContainer.addView(playerView, 0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        playerView.onStart();
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
        playerView.onDestroy();
        super.onDestroy();
    }
}
