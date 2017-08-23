/*
 * Bitmovin Player Android SDK
 * Copyright (C) 2017, Bitmovin GmbH, All Rights Reserved
 *
 * This source code and its use and distribution, is subject to the terms
 * and conditions of the applicable license agreement.
 */

package com.bitmovin.player.samples.offline.playback;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.bitmovin.player.BitmovinPlayer;
import com.bitmovin.player.BitmovinPlayerView;
import com.bitmovin.player.config.media.SourceConfiguration;
import com.bitmovin.player.config.media.SourceItem;
import com.bitmovin.player.offline.OfflineSourceItem;
import com.google.gson.Gson;

public class PlayerActivity extends AppCompatActivity
{

    public static final String SOURCE_ITEM = "SOURCE_ITEM";
    public static final String OFFLINE_SOURCE_ITEM = "OFFLINE_SOURCE_ITEM";

    private BitmovinPlayerView bitmovinPlayerView;
    private BitmovinPlayer bitmovinPlayer;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        SourceItem sourceItem;
        if (getIntent().hasExtra(SOURCE_ITEM))
        {
            sourceItem = this.gson.fromJson(getIntent().getStringExtra(SOURCE_ITEM), SourceItem.class);
        }
        else if (getIntent().hasExtra(OFFLINE_SOURCE_ITEM))
        {
            sourceItem = this.gson.fromJson(getIntent().getStringExtra(OFFLINE_SOURCE_ITEM), OfflineSourceItem.class);
        }
        else
        {
            finish();
            return;
        }

        this.bitmovinPlayerView = (BitmovinPlayerView) this.findViewById(R.id.bitmovinPlayerView);
        this.bitmovinPlayer = this.bitmovinPlayerView.getPlayer();

        this.initializePlayer(sourceItem);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        this.bitmovinPlayerView.onResume();
    }

    @Override
    protected void onPause()
    {
        this.bitmovinPlayerView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy()
    {
        this.bitmovinPlayerView.onDestroy();
        super.onDestroy();
    }

    protected void initializePlayer(SourceItem sourceItem)
    {
        // Create a new source configuration
        SourceConfiguration sourceConfiguration = new SourceConfiguration();

        // Add source item to source configuration
        sourceConfiguration.addSourceItem(sourceItem);

        // load source using the created source configuration
        this.bitmovinPlayer.load(sourceConfiguration);
    }
}