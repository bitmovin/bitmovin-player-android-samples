/*
 * Bitmovin Player Android SDK
 * Copyright (C) 2017, Bitmovin GmbH, All Rights Reserved
 *
 * This source code and its use and distribution, is subject to the terms
 * and conditions of the applicable license agreement.
 */

package com.bitmovin.player.samples.fullscreen.basic;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import com.bitmovin.player.PlayerView;
import com.bitmovin.player.api.ui.FullscreenHandler;
import com.bitmovin.player.ui.FullscreenUtil;

public class CustomFullscreenHandler implements FullscreenHandler {
    private Activity activity;
    private View decorView;
    private PlayerView playerView;
    private Toolbar toolbar;

    private PlayerOrientationListener playerOrientationListener;

    private boolean isFullscreen;


    public CustomFullscreenHandler(Activity activity, PlayerView playerView, Toolbar toolbar) {
        this.activity = activity;
        this.playerView = playerView;
        this.toolbar = toolbar;
        decorView = activity.getWindow().getDecorView();
        playerOrientationListener = new PlayerOrientationListener(activity);

        playerOrientationListener.enable();
    }

    private void handleFullscreen(boolean fullscreen) {
        this.isFullscreen = fullscreen;

        doSystemUiVisibility(fullscreen);
        doLayoutChanges(fullscreen);
    }

    private void doSystemUiVisibility(final boolean fullScreen) {
        this.decorView.post(() -> {
            int uiParams = getSystemUiVisibilityFlags(fullScreen);

            decorView.setSystemUiVisibility(uiParams);
        });
    }

    private void doLayoutChanges(final boolean fullscreen) {
        Looper mainLooper = Looper.getMainLooper();
        boolean isAlreadyMainLooper = Looper.myLooper() == mainLooper;

        UpdateLayoutRunnable updateLayoutRunnable = new UpdateLayoutRunnable((AppCompatActivity) activity, fullscreen);

        if (isAlreadyMainLooper) {
            updateLayoutRunnable.run();
        } else {
            Handler handler = new Handler(mainLooper);
            handler.post(updateLayoutRunnable);
        }
    }

    @Override
    public void onFullscreenRequested() {
        handleFullscreen(true);
    }

    @Override
    public void onFullscreenExitRequested() {
        handleFullscreen(false);
    }

    @Override
    public void onResume() {
        if (isFullscreen) {
            doSystemUiVisibility(isFullscreen);
        }
    }

    @Override
    public void onPause() {}

    @Override
    public void onDestroy() {
        playerOrientationListener.disable();
    }

    @Override
    public boolean isFullscreen() {
        return isFullscreen;
    }

    private class UpdateLayoutRunnable implements Runnable {
        private AppCompatActivity activity;
        private boolean fullscreen;

        private UpdateLayoutRunnable(AppCompatActivity activity, boolean fullscreen) {
            this.activity = activity;
            this.fullscreen = fullscreen;
        }

        @Override
        @SuppressLint("RestrictedApi")
        public void run() {
            if (CustomFullscreenHandler.this.toolbar != null) {
                if (this.fullscreen) {
                    CustomFullscreenHandler.this.toolbar.setVisibility(View.GONE);
                } else {
                    CustomFullscreenHandler.this.toolbar.setVisibility(View.VISIBLE);
                }
            }

            if (CustomFullscreenHandler.this.playerView.getParent() instanceof ViewGroup) {
                ViewGroup parentView = (ViewGroup) CustomFullscreenHandler.this.playerView.getParent();

                for (int i = 0; i < parentView.getChildCount(); i++) {
                    View child = parentView.getChildAt(i);

                    if (child != playerView) {
                        child.setVisibility(fullscreen ? View.GONE : View.VISIBLE);
                    }
                }
            }
        }
    }

    public int getSystemUiVisibilityFlags(boolean fullScreen) {
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
