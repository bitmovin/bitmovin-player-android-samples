/*
 * Bitmovin Player Android SDK
 * Copyright (C) 2019, Bitmovin GmbH, All Rights Reserved
 *
 * This source code and its use and distribution, is subject to the terms
 * and conditions of the applicable license agreement.
 */

package com.bitmovin.player.samples.custom.ui.html;

import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.bitmovin.analytics.api.AnalyticsConfig;
import com.bitmovin.player.PlayerView;
import com.bitmovin.player.api.Player;
import com.bitmovin.player.api.PlayerConfig;
import com.bitmovin.player.api.analytics.PlayerFactory;
import com.bitmovin.player.api.source.SourceConfig;
import com.bitmovin.player.api.source.SourceType;
import com.bitmovin.player.api.ui.PlayerViewConfig;
import com.bitmovin.player.api.ui.ScalingMode;
import com.bitmovin.player.api.ui.UiConfig;
import com.bitmovin.player.ui.CustomMessageHandler;

public class PlaybackActivity extends AppCompatActivity {
    private PlayerView playerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playback);

        /*
         * Go to https://github.com/bitmovin/bitmovin-player-ui to get started with creating a custom player UI.
         */
        PlayerViewConfig viewConfig = new PlayerViewConfig(
                new UiConfig.WebUi(
                        "file:///android_asset/custom-bitmovinplayer-ui.min.css",
                        null,
                        "file:///android_asset/custom-bitmovinplayer-ui.min.js"
                ),
                false,
                ScalingMode.Fit
        );

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

        // Create new Player with our PlayerConfig
        String key = "{ANALYTICS_LICENSE_KEY}";
        Player player = PlayerFactory.create(
                this,
                new PlayerConfig(),
                new AnalyticsConfig(key)
        );

        // Create a PlayerView with our Player and PlayerViewConfig
        playerView = new PlayerView(this, player, viewConfig);
        playerView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        // Set the CustomMessageHandler to the playerView
        playerView.setCustomMessageHandler(customMessageHandler);

        //load the SourceConfig into the player
        player.load(new SourceConfig("https://bitdash-a.akamaihd.net/content/sintel/sintel.mpd", SourceType.Dash));

        LinearLayout playerRootLayout = (LinearLayout) findViewById(R.id.player_view);

        // Add PlayerView to the layout as first child
        playerRootLayout.addView(playerView, 0);

        Button toggleCloseButtonStateButton = (Button) findViewById(R.id.toggle_button);

        toggleCloseButtonStateButton.setOnClickListener(v -> {
            customMessageHandler.sendMessage("toggleCloseButton", null);
        });
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
}
