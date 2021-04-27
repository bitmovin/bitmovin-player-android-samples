/*
 * Bitmovin Player Android SDK
 * Copyright (C) 2017, Bitmovin GmbH, All Rights Reserved
 *
 * This source code and its use and distribution, is subject to the terms
 * and conditions of the applicable license agreement.
 */

package com.bitmovin.player.samples.custom.ui;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.bitmovin.player.api.Player;
import com.bitmovin.player.api.PlayerConfig;
import com.bitmovin.player.api.source.SourceConfig;
import com.bitmovin.player.api.source.SourceType;
import com.bitmovin.player.api.ui.FullscreenHandler;
import com.bitmovin.player.api.ui.StyleConfig;

public class MainActivity extends AppCompatActivity {
    private PlayerUI playerUi;
    private FullscreenHandler fullscreenHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create new StyleConfig
        StyleConfig styleConfig = new StyleConfig();
        // Disable UI
        styleConfig.setUiEnabled(false);

        // Creating a new PlayerConfig
        PlayerConfig playerConfig = new PlayerConfig();
        // Assign created StyleConfig to the PlayerConfig
        playerConfig.setStyleConfig(styleConfig);
        // Assign a SourceItem to the PlayerConfig

        Player player = Player.create(this, playerConfig);
        playerUi = new PlayerUI(this, player);
        fullscreenHandler = new CustomFullscreenHandler(this, playerUi);

        player.load(new SourceConfig("https://bitdash-a.akamaihd.net/content/sintel/sintel.mpd", SourceType.Dash));

        // Set the FullscreenHandler of the PlayerUI
        playerUi.setFullscreenHandler(fullscreenHandler);

        LinearLayout rootView = (LinearLayout) findViewById(R.id.activity_main);

        playerUi.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        rootView.addView(playerUi);
    }

    @Override
    protected void onStart() {
        super.onStart();
        playerUi.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        playerUi.onResume();
    }

    @Override
    protected void onPause() {
        playerUi.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        playerUi.onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        playerUi.onDestroy();
        super.onDestroy();
    }
}
