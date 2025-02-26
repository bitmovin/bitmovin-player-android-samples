package com.bitmovin.player.samples.custom.adaptation;

import android.os.Bundle;
import android.view.Window;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.bitmovin.analytics.api.AnalyticsConfig;
import com.bitmovin.player.PlayerView;
import com.bitmovin.player.api.Player;
import com.bitmovin.player.api.PlayerBuilder;
import com.bitmovin.player.api.PlayerConfig;
import com.bitmovin.player.api.media.AdaptationConfig;
import com.bitmovin.player.api.media.video.quality.VideoAdaptation;
import com.bitmovin.player.api.media.video.quality.VideoAdaptationData;
import com.bitmovin.player.api.media.video.quality.VideoQuality;
import com.bitmovin.player.api.source.SourceConfig;
import com.bitmovin.player.api.source.SourceType;

import java.util.List;


public class MainActivity extends AppCompatActivity {
    private Player player;
    private PlayerView playerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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

        String key = "{ANALYTICS_LICENSE_KEY}";
        player = new PlayerBuilder(this)
                .setPlayerConfig(createPlayerConfig())
                .configureAnalytics(new AnalyticsConfig(key))
                .build();
        playerView = new PlayerView(this, player);

        player.load(new SourceConfig("https://cdn.bitmovin.com/content/assets/sintel/sintel.mpd", SourceType.Dash));

        LinearLayout rootView = findViewById(R.id.root);
        playerView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        playerView.setKeepScreenOn(true);
        rootView.addView(playerView, 0);
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

    private PlayerConfig createPlayerConfig() {
        // Setup adaptation config
        AdaptationConfig.Builder adaptationConfigBuilder = new AdaptationConfig.Builder()
            .setRebufferingAllowed(true)
            .setMaxSelectableVideoBitrate(800_000)
            .setInitialBandwidthEstimateOverride(1_200_000L)
            .setVideoAdaptation(videoAdaptationListener);


        // Assign adaptation to player config
        PlayerConfig.Builder playerConfigBuilder = new PlayerConfig.Builder();
        AdaptationConfig adaptationConfig = adaptationConfigBuilder.build();
        playerConfigBuilder.setAdaptationConfig(adaptationConfig);
        return playerConfigBuilder.build();
    }

    private final VideoAdaptation videoAdaptationListener = new VideoAdaptation() {
        /*
         *  Customize this method to return a different video quality id than what is suggested
         */
        @Override
        public String onVideoAdaptation(VideoAdaptationData videoAdaptationData) {
            // Get the suggested video quality id
            String suggestedVideoQualityId = videoAdaptationData.getSuggested();

            // Add your own logic to choose a different video quality
            List<VideoQuality> videoQualities = player.getAvailableVideoQualities();

            return suggestedVideoQualityId;
        }
    };
}
