/*
 * Bitmovin Player Android SDK
 * Copyright (C) 2017, Bitmovin GmbH, All Rights Reserved
 *
 * This source code and its use and distribution, is subject to the terms
 * and conditions of the applicable license agreement.
 */

package com.bitmovin.player.samples.analytics;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bitmovin.analytics.api.AnalyticsConfig;
import com.bitmovin.analytics.api.CustomData;
import com.bitmovin.analytics.api.SourceMetadata;
import com.bitmovin.player.PlayerView;
import com.bitmovin.player.api.Player;
import com.bitmovin.player.api.PlayerConfig;
import com.bitmovin.player.api.analytics.AnalyticsApi;
import com.bitmovin.player.api.analytics.PlayerFactory;
import com.bitmovin.player.api.analytics.SourceFactory;
import com.bitmovin.player.api.source.Source;
import com.bitmovin.player.api.source.SourceConfig;


public class MainActivity extends AppCompatActivity {
    private PlayerView playerView;
    private Player player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        player = PlayerFactory.create(this, new PlayerConfig(), analyticsConfig);
        playerView.setPlayer(player);

        // create a source with a sourceMetadata for custom analytics tracking
        SourceConfig sourceConfig = SourceConfig.fromUrl("https://bitdash-a.akamaihd.net/content/sintel/sintel.mpd");
        CustomData customData = new CustomData.Builder().setCustomData1("CustomData1").setExperimentName("Experiment1").build();
        SourceMetadata sourceMetadata = new SourceMetadata.Builder().setCustomData(customData).build();
        Source source = SourceFactory.create(sourceConfig, sourceMetadata);
        // load the source with custom metadata
        player.load(source);
    }
}
