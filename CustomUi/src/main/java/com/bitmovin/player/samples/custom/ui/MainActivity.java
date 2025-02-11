/*
 * Bitmovin Player Android SDK
 * Copyright (C) 2017, Bitmovin GmbH, All Rights Reserved
 *
 * This source code and its use and distribution, is subject to the terms
 * and conditions of the applicable license agreement.
 */

package com.bitmovin.player.samples.custom.ui;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.bitmovin.analytics.api.AnalyticsConfig;
import com.bitmovin.player.api.Player;
import com.bitmovin.player.api.PlayerBuilder;
import com.bitmovin.player.api.source.SourceConfig;
import com.bitmovin.player.api.source.SourceType;
import com.bitmovin.player.api.ui.FullscreenHandler;

public class MainActivity extends AppCompatActivity {
    private PlayerUI playerUi;
    private FullscreenHandler fullscreenHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View root = findViewById(R.id.root);
        ViewCompat.setOnApplyWindowInsetsListener(root, (view, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        Window window = getWindow();
        WindowInsetsControllerCompat insetsController = WindowCompat.getInsetsController(window,
                window.getDecorView());
        insetsController.setAppearanceLightStatusBars(true);
        insetsController.setAppearanceLightNavigationBars(true);

        String key = "{ANALYTICS_LICENSE_KEY}";
        Player player = new PlayerBuilder(this)
                .configureAnalytics(new AnalyticsConfig(key))
                .build();
        playerUi = new PlayerUI(this, player);
        fullscreenHandler = new CustomFullscreenHandler(this, root, playerUi);

        player.load(new SourceConfig("https://cdn.bitmovin.com/content/assets/sintel/sintel.mpd", SourceType.Dash));

        // Set the FullscreenHandler of the PlayerUI
        playerUi.setFullscreenHandler(fullscreenHandler);

        LinearLayout rootView = (LinearLayout) root;

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
