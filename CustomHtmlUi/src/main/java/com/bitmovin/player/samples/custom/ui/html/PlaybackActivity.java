/*
 * Bitmovin Player Android SDK
 * Copyright (C) 2019, Bitmovin GmbH, All Rights Reserved
 *
 * This source code and its use and distribution, is subject to the terms
 * and conditions of the applicable license agreement.
 */

package com.bitmovin.player.samples.custom.ui.html;

import android.os.Bundle;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.bitmovin.analytics.api.AnalyticsConfig;
import com.bitmovin.player.PlayerView;
import com.bitmovin.player.api.Player;
import com.bitmovin.player.api.PlayerBuilder;
import com.bitmovin.player.api.source.SourceConfig;
import com.bitmovin.player.api.source.SourceType;
import com.bitmovin.player.api.ui.PlayerViewConfig;
import com.bitmovin.player.api.ui.ScalingMode;
import com.bitmovin.player.api.ui.SurfaceType;
import com.bitmovin.player.api.ui.UiConfig;
import com.bitmovin.player.ui.CustomMessageHandler;

public class PlaybackActivity extends AppCompatActivity {
    private PlayerView playerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playback);
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

        /*
         * Go to https://github.com/bitmovin/bitmovin-player-ui to get started with creating a custom player UI.
         */
        PlayerViewConfig viewConfig = new PlayerViewConfig(
                new UiConfig.WebUi(
                        "file:///android_asset/custom-bitmovinplayer-ui.min.css",
                        null,
                        "file:///android_asset/custom-bitmovinplayer-ui.min.js",
                        true,
                        false,
                        null,
                        UiConfig.WebUi.Variant.SmallScreenUi.INSTANCE,
                        false
                ),
                false,
                ScalingMode.Fit,
                false,
                SurfaceType.SurfaceView
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
        Player player = new PlayerBuilder(this)
                .configureAnalytics(new AnalyticsConfig(key))
                .build();

        // Create a PlayerView with our Player and PlayerViewConfig
        playerView = new PlayerView(this, player, viewConfig);
        playerView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        playerView.setKeepScreenOn(true);

        // Set the CustomMessageHandler to the playerView
        playerView.setCustomMessageHandler(customMessageHandler);

        //load the SourceConfig into the player
        player.load(new SourceConfig("https://cdn.bitmovin.com/content/assets/sintel/sintel.mpd", SourceType.Dash));

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
