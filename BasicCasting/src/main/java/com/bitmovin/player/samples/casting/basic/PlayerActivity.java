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

import com.bitmovin.player.BitmovinPlayer;
import com.bitmovin.player.BitmovinPlayerView;
import com.bitmovin.player.cast.BitmovinCastManager;
import com.bitmovin.player.config.media.SourceItem;

public class PlayerActivity extends AppCompatActivity
{
    public static final String SOURCE_URL = "SOURCE_URL";
    public static final String SOURCE_TITLE = "SOURCE_TITLE";

    private BitmovinPlayerView bitmovinPlayerView;
    private BitmovinPlayer bitmovinPlayer;

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

        this.bitmovinPlayerView = (BitmovinPlayerView) this.findViewById(R.id.bitmovinPlayerView);
        this.bitmovinPlayer = this.bitmovinPlayerView.getPlayer();

        this.initializePlayer(sourceUrl, sourceTitle);
    }

    @Override
    protected void onStart()
    {
        this.bitmovinPlayerView.onStart();
        super.onStart();
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
    protected void onStop()
    {
        this.bitmovinPlayerView.onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        this.bitmovinPlayerView.onDestroy();
        super.onDestroy();
    }

    protected void initializePlayer(String sourceUrl, String sourceTitle)
    {

        // Create a new source item
        SourceItem sourceItem = new SourceItem(sourceUrl);
        sourceItem.setTitle(sourceTitle);


        // load source using the created source item
        this.bitmovinPlayer.load(sourceItem);
    }
}
