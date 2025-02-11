/*
 * Bitmovin Player Android SDK
 * Copyright (C) 2017, Bitmovin GmbH, All Rights Reserved
 *
 * This source code and its use and distribution, is subject to the terms
 * and conditions of the applicable license agreement.
 */

package com.bitmovin.player.samples.offline.playback;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.bitmovin.player.PlayerView;
import com.bitmovin.player.api.Player;
import com.bitmovin.player.api.source.SourceConfig;

public class PlayerActivity extends AppCompatActivity {

    public static final String SOURCE_ITEM = "SOURCE_ITEM";
    public static final String OFFLINE_SOURCE_ITEM = "OFFLINE_SOURCE_ITEM";

    private PlayerView playerView;
    private Player player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
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

        Intent intent = getIntent();

        SourceConfig sourceConfig;
        if (intent.hasExtra(SOURCE_ITEM)) {
            sourceConfig = intent.getParcelableExtra(SOURCE_ITEM);
        } else if (intent.hasExtra(OFFLINE_SOURCE_ITEM)) {
            sourceConfig = intent.getParcelableExtra(OFFLINE_SOURCE_ITEM);
        } else {
            finish();
            return;
        }

        playerView = findViewById(R.id.playerView);
        player = playerView.getPlayer();

        initializePlayer(sourceConfig);
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

    protected void initializePlayer(SourceConfig sourceConfig) {
        // load source
        player.load(sourceConfig);
    }
}
