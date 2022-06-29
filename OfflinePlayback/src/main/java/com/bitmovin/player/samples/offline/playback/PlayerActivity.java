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

import androidx.appcompat.app.AppCompatActivity;

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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

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
