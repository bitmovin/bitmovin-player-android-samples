package com.bitmovin.player.samples.custom.ui.subtitleview;

import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.bitmovin.analytics.api.AnalyticsConfig;
import com.bitmovin.player.PlayerView;
import com.bitmovin.player.SubtitleView;
import com.bitmovin.player.api.Player;
import com.bitmovin.player.api.PlayerBuilder;
import com.bitmovin.player.api.source.SourceConfig;
import com.bitmovin.player.api.source.SourceType;
import com.bitmovin.player.api.ui.PlayerViewConfig;
import com.bitmovin.player.api.ui.ScalingMode;
import com.bitmovin.player.api.ui.SurfaceType;
import com.bitmovin.player.api.ui.UiConfig;

public class MainActivity extends AppCompatActivity {
    private Player player;
    private PlayerView playerView;
    private SubtitleView subtitleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root), (view, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        Window window = getWindow();
        WindowInsetsControllerCompat insetsController = WindowCompat.getInsetsController(window,
                window.getDecorView());
        insetsController.setAppearanceLightStatusBars(true);
        insetsController.setAppearanceLightNavigationBars(true);

        RelativeLayout playerContainer = findViewById(R.id.player_container);

        // Creating a PlayerView and get it's Player instance.
        String key = "{ANALYTICS_LICENSE_KEY}";
        Player player = new PlayerBuilder(this)
                .configureAnalytics(new AnalyticsConfig(key))
                .build();
        PlayerViewConfig viewConfig = new PlayerViewConfig.Builder()
            .setUiConfig(UiConfig.Disabled.INSTANCE)
            .build();
        playerView = new PlayerView(this,
                player,
                viewConfig
        );
        playerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        playerView.setKeepScreenOn(true);
        player.load(new SourceConfig("https://cdn.bitmovin.com/content/assets/sintel/sintel.mpd", SourceType.Dash));

        // Creating a SubtitleView and assign the current player instance.
        subtitleView = new SubtitleView(this);
        subtitleView.setUserDefaultStyle();
        subtitleView.setUserDefaultTextSize();
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
