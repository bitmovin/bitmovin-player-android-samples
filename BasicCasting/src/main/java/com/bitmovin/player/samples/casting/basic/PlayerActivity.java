/*
 * Bitmovin Player Android SDK
 * Copyright (C) 2017, Bitmovin GmbH, All Rights Reserved
 *
 * This source code and its use and distribution, is subject to the terms
 * and conditions of the applicable license agreement.
 */

package com.bitmovin.player.samples.casting.basic;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.bitmovin.player.PlayerView;
import com.bitmovin.player.api.Player;
import com.bitmovin.player.api.source.SourceConfig;
import com.bitmovin.player.casting.BitmovinCastManager;

public class PlayerActivity extends AppCompatActivity
{
    public static final String SOURCE_URL = "SOURCE_URL";
    public static final String SOURCE_TITLE = "SOURCE_TITLE";

    private PlayerView playerView;
    private Player player;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        // Update the context the BitmovinCastManager is using
        // This should be done in every Activity's onCreate using the cast function
        BitmovinCastManager.getInstance().updateContext(this);

        String sourceUrl = getIntent().getStringExtra(SOURCE_URL);
        String sourceTitle = getIntent().getStringExtra(SOURCE_TITLE);
        if (sourceUrl == null || sourceTitle == null)
        {
            finish();
        }

        this.playerView = this.findViewById(R.id.bitmovinPlayerView);
        this.player = this.playerView.getPlayer();

        this.initializePlayer(sourceUrl, sourceTitle);
    }

    @Override
    protected void onStart()
    {
        this.playerView.onStart();
        super.onStart();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        this.playerView.onResume();
    }

    @Override
    protected void onPause()
    {
        this.playerView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop()
    {
        this.playerView.onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        this.playerView.onDestroy();
        super.onDestroy();
    }

    protected void initializePlayer(String sourceUrl, String sourceTitle)
    {

        // Create a new source item
        SourceConfig sourceItem = new SourceConfig(sourceUrl);
        sourceItem.setTitle(sourceTitle);


        // load source using the created source item
        this.player.load(sourceItem);
    }
}
