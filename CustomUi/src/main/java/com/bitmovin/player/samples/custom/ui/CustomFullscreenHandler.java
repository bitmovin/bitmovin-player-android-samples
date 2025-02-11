/*
 * Bitmovin Player Android SDK
 * Copyright (C) 2017, Bitmovin GmbH, All Rights Reserved
 *
 * This source code and its use and distribution, is subject to the terms
 * and conditions of the applicable license agreement.
 */

package com.bitmovin.player.samples.custom.ui;

import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bitmovin.player.api.ui.FullscreenHandler;

public class CustomFullscreenHandler implements FullscreenHandler {
    private final AppCompatActivity activity;
    private final View decorView;
    private final PlayerUI playerUI;

    private PlayerOrientationListener playerOrientationListener;

    private boolean isFullscreen;

    public CustomFullscreenHandler(AppCompatActivity activity, View rootView, PlayerUI playerUI) {
        this.activity = activity;
        this.playerUI = playerUI;
        this.decorView = activity.getWindow().getDecorView();
        this.playerOrientationListener = new PlayerOrientationListener(activity);

        ViewCompat.setOnApplyWindowInsetsListener(rootView, (view, insets) -> {
            if (isFullscreen) {
                view.setPadding(0, 0, 0, 0);
            } else {
                Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                view.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            }
            return WindowInsetsCompat.CONSUMED;
        });

        this.playerOrientationListener.enable();
    }

    private void handleFullscreen(boolean fullscreen) {
        this.isFullscreen = fullscreen;

        this.doSystemUiVisibility(fullscreen);
        this.doLayoutChanges(fullscreen);
    }

    private void doSystemUiVisibility(final boolean fullScreen) {
        this.decorView.post(new Runnable() {
            @Override
            public void run() {
                int uiParams = getSystemUiVisibilityFlags(fullScreen);

                decorView.setSystemUiVisibility(uiParams);
            }
        });
    }

    private void doLayoutChanges(final boolean fullscreen) {
        ActionBar actionBar = ((AppCompatActivity) this.activity).getSupportActionBar();
        if (actionBar != null) {
            if (fullscreen) {
                actionBar.hide();
            } else {
                actionBar.show();
            }
        }

        if (this.playerUI.getParent() instanceof ViewGroup) {
            ViewGroup parentView = (ViewGroup) this.playerUI.getParent();

            for (int i = 0; i < parentView.getChildCount(); i++) {
                View child = parentView.getChildAt(i);
                if (child != playerUI) {
                    child.setVisibility(fullscreen ? View.GONE : View.VISIBLE);
                }
            }

            ViewGroup.LayoutParams params = this.playerUI.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            this.playerUI.setLayoutParams(params);
            this.playerUI.setPadding(0, 0, 0, 0);
        }
    }

    @Override
    public void onFullscreenRequested() {
        this.handleFullscreen(true);
    }

    @Override
    public void onFullscreenExitRequested() {
        this.handleFullscreen(false);
    }

    @Override
    public void onResume() {
        if (this.isFullscreen) {
            this.doSystemUiVisibility(this.isFullscreen);
        }
    }

    @Override
    public void onPause() {
    }

    @Override
    public void onDestroy() {
        this.playerOrientationListener.disable();
    }

    @Override
    public boolean isFullscreen() {
        return this.isFullscreen;
    }

    private int getSystemUiVisibilityFlags(boolean fullScreen) {
        if (fullScreen) {
            return View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        } else {
            return View.SYSTEM_UI_FLAG_VISIBLE;
        }
    }
}
