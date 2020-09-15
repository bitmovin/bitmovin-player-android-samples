/*
 * Bitmovin Player Android SDK
 * Copyright (C) 2019, Bitmovin GmbH, All Rights Reserved
 *
 * This source code and its use and distribution, is subject to the terms
 * and conditions of the applicable license agreement.
 */

package com.bitmovin.player.samples.custom.ui.html;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.widget.Button;
import android.widget.LinearLayout;

import com.bitmovin.player.BitmovinPlayerView;
import com.bitmovin.player.config.PlayerConfiguration;
import com.bitmovin.player.config.StyleConfiguration;
import com.bitmovin.player.config.media.SourceItem;
import com.bitmovin.player.ui.CustomMessageHandler;

public class PlaybackActivity extends AppCompatActivity
{

    private BitmovinPlayerView bitmovinPlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playback);

        // Create new StyleConfiguration
        StyleConfiguration styleConfiguration = new StyleConfiguration();
        /*
         * Go to https://github.com/bitmovin/bitmovin-player-ui to get started with creating a custom player UI.
         */
        // Set URLs for the JavaScript and the CSS
        styleConfiguration.setPlayerUiJs("file:///android_asset/custom-bitmovinplayer-ui.min.js");
        styleConfiguration.setPlayerUiCss("file:///android_asset/custom-bitmovinplayer-ui.min.css");

        // Creating a new PlayerConfiguration
        PlayerConfiguration playerConfiguration = new PlayerConfiguration();
        // Assign created StyleConfiguration to the PlayerConfiguration
        playerConfiguration.setStyleConfiguration(styleConfiguration);

        // Create a custom javascriptInterface object which takes over the Bitmovin Web UI -> native calls
        Object javascriptInterface = new Object() {
            @JavascriptInterface
            public String closePlayer(String data) {
                finish();
                return null;
            }
        };

        // Setup CustomMessageHandler for communication with Bitmovin Web UI
        CustomMessageHandler customMessageHandler = new CustomMessageHandler(javascriptInterface);

        // Create new BitmovinPlayerView with our PlayerConfiguration
        this.bitmovinPlayerView = new BitmovinPlayerView(this, playerConfiguration);
        this.bitmovinPlayerView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        // Set the CustomMessageHandler to the bitmovinPlayerView
        this.bitmovinPlayerView.setCustomMessageHandler(customMessageHandler);

        //load the SourceItem into the player
        this.bitmovinPlayerView.getPlayer().load(new SourceItem("https://bitdash-a.akamaihd.net/content/sintel/sintel.mpd"));

        LinearLayout playerRootLayout = (LinearLayout) this.findViewById(R.id.player_view);

        // Add BitmovinPlayerView to the layout as first child
        playerRootLayout.addView(this.bitmovinPlayerView, 0);

        Button toggleCloseButtonStateButton = (Button) this.findViewById(R.id.toggle_button);

        toggleCloseButtonStateButton.setOnClickListener(v -> {
            customMessageHandler.sendMessage("toggleCloseButton", null);
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        this.bitmovinPlayerView.onStart();
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
}
