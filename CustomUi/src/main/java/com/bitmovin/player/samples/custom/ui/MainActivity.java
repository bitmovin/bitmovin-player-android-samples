/*
 * Bitmovin Player Android SDK
 * Copyright (C) 2017, Bitmovin GmbH, All Rights Reserved
 *
 * This source code and its use and distribution, is subject to the terms
 * and conditions of the applicable license agreement.
 */

package com.bitmovin.player.samples.custom.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bitmovin.player.config.PlayerConfiguration;
import com.bitmovin.player.config.StyleConfiguration;
import com.bitmovin.player.config.media.SourceItem;
import com.bitmovin.player.ui.FullscreenHandler;

public class MainActivity extends AppCompatActivity
{
    private PlayerUI playerUi;
    private FullscreenHandler fullscreenHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create new StyleConfiguration
        StyleConfiguration styleConfiguration = new StyleConfiguration();
        // Disable UI
        styleConfiguration.setUiEnabled(false);

        // Creating a new PlayerConfiguration
        PlayerConfiguration playerConfiguration = new PlayerConfiguration();
        // Assign created StyleConfiguration to the PlayerConfiguration
        playerConfiguration.setStyleConfiguration(styleConfiguration);
        // Assign a SourceItem to the PlayerConfiguration
        playerConfiguration.setSourceItem(new SourceItem("https://bitdash-a.akamaihd.net/content/sintel/sintel.mpd"));

        this.playerUi = new PlayerUI(this, playerConfiguration);
        this.fullscreenHandler = new CustomFullscreenHandler(this, playerUi);

        // Set the FullscreenHandler of the PlayerUI
        this.playerUi.setFullscreenHandler(fullscreenHandler);

        LinearLayout rootView = (LinearLayout) this.findViewById(R.id.activity_main);

        this.playerUi.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        rootView.addView(this.playerUi);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        this.playerUi.onStart();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        this.playerUi.onResume();
    }

    @Override
    protected void onPause()
    {
        this.playerUi.onPause();
        super.onPause();
    }

    @Override
    protected void onStop()
    {
        this.playerUi.onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        this.playerUi.onDestroy();
        super.onDestroy();
    }
}
