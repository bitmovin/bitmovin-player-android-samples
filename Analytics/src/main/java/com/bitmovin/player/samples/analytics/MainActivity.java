/*
 * Bitmovin Player Android SDK
 * Copyright (C) 2017, Bitmovin GmbH, All Rights Reserved
 *
 * This source code and its use and distribution, is subject to the terms
 * and conditions of the applicable license agreement.
 */

package com.bitmovin.player.samples.analytics;

import android.os.Bundle;
import android.view.Window;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.bitmovin.analytics.api.AnalyticsConfig;
import com.bitmovin.analytics.api.CustomData;
import com.bitmovin.analytics.api.SourceMetadata;
import com.bitmovin.player.PlayerView;
import com.bitmovin.player.api.Player;
import com.bitmovin.player.api.PlayerBuilder;
import com.bitmovin.player.api.source.Source;
import com.bitmovin.player.api.source.SourceBuilder;
import com.bitmovin.player.api.source.SourceConfig;


public class MainActivity extends AppCompatActivity {
    private PlayerView playerView;
    private Player player;

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

        playerView = findViewById(R.id.bitmovinPlayerView);

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

    protected void initializePlayer() {
        String key = "{ANALYTICS_LICENSE_KEY}";
        AnalyticsConfig analyticsConfig = new AnalyticsConfig.Builder(key).setAdTrackingDisabled(false).setRandomizeUserId(true).build();
        // create a player with analytics config
        player = new PlayerBuilder(this)
                .configureAnalytics(analyticsConfig)
                .build();
        playerView.setPlayer(player);

        // create a source with a sourceMetadata for custom analytics tracking
        SourceConfig sourceConfig = SourceConfig.fromUrl("https://cdn.bitmovin.com/content/assets/sintel/sintel.mpd");
        CustomData customData = new CustomData.Builder().setCustomData1("CustomData1").setExperimentName("Experiment1").build();
        SourceMetadata sourceMetadata = new SourceMetadata.Builder().setCustomData(customData).build();
        Source source = new SourceBuilder(sourceConfig)
                .configureAnalytics(sourceMetadata)
                .build();
        // load the source with custom metadata
        player.load(source);
    }
}
