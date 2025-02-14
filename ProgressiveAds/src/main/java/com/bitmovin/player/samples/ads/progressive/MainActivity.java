package com.bitmovin.player.samples.ads.progressive;

import android.os.Bundle;
import android.view.Window;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
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
import com.bitmovin.player.api.advertising.AdItem;
import com.bitmovin.player.api.advertising.AdSource;
import com.bitmovin.player.api.advertising.AdSourceType;
import com.bitmovin.player.api.advertising.AdvertisingConfig;
import com.bitmovin.player.api.source.SourceConfig;
import com.bitmovin.player.api.source.SourceType;

public class MainActivity extends AppCompatActivity {
    private static final String AD_SOURCE_1 = "https://cdn.bitmovin.com/content/assets/testing/ads/testad2s.mp4";
    private static final String AD_SOURCE_2 = "file:///android_asset/testad2s.mp4";

    private PlayerView playerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_main), (view, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        Window window = getWindow();
        WindowInsetsControllerCompat insetsController = WindowCompat.getInsetsController(window,
                window.getDecorView());
        insetsController.setAppearanceLightStatusBars(true);
        insetsController.setAppearanceLightNavigationBars(true);

        // Create AdSources
        AdSource firstAdSource = new AdSource(AdSourceType.Progressive, AD_SOURCE_1);
        AdSource secondAdSource = new AdSource(AdSourceType.Progressive, AD_SOURCE_2);

        // Setup a pre-roll ad
        AdItem preRoll = new AdItem("pre", firstAdSource);
        AdItem midRoll = new AdItem("10%", secondAdSource);

        // Add the AdItems to the AdvertisingConfig
        AdvertisingConfig advertisingConfig = new AdvertisingConfig(preRoll, midRoll);

        // Creating a new PlayerConfig
        PlayerConfig.Builder playerConfigBuilder = new PlayerConfig.Builder();
        // Assign the AdvertisingConfig to the PlayerConfig
        // All ads in the AdvertisingConfig will be scheduled automatically
        playerConfigBuilder.setAdvertisingConfig(advertisingConfig);

        // Create new PlayerView with our PlayerConfig
        String key = "{ANALYTICS_LICENSE_KEY}";
        Player player = new PlayerBuilder(this)
                .setPlayerConfig(playerConfigBuilder.build())
                .configureAnalytics(new AnalyticsConfig(key))
                .build();
        playerView = new PlayerView(this, player);
        playerView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        playerView.setKeepScreenOn(true);

        // Load the SourceItem
        playerView.getPlayer().load(new SourceConfig("https://cdn.bitmovin.com/content/assets/sintel/sintel.mpd", SourceType.Dash));

        LinearLayout rootView = findViewById(R.id.activity_main);

        // Add PlayerView to the layout
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
}
