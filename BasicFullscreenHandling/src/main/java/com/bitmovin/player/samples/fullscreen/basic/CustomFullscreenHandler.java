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

import com.bitmovin.player.BitmovinPlayerView;
import com.bitmovin.player.ui.FullscreenHandler;
import com.bitmovin.player.ui.FullscreenUtil;

public class CustomFullscreenHandler implements FullscreenHandler
{
    private Activity activity;
    private View decorView;
    private BitmovinPlayerView playerView;
    private Toolbar toolbar;

    private PlayerOrientationListener playerOrientationListener;

    private boolean isFullscreen;


    public CustomFullscreenHandler(Activity activity, BitmovinPlayerView playerView, Toolbar toolbar)
    {
        this.activity = activity;
        this.playerView = playerView;
        this.toolbar = toolbar;
        this.decorView = activity.getWindow().getDecorView();
        this.playerOrientationListener = new PlayerOrientationListener(activity);

        this.playerOrientationListener.enable();
    }

    private void handleFullscreen(boolean fullscreen)
    {
        this.isFullscreen = fullscreen;

        this.doSystemUiVisibility(fullscreen);
        this.doLayoutChanges(fullscreen);
    }

    private void doSystemUiVisibility(final boolean fullScreen)
    {
        this.decorView.post(new Runnable()
        {
            @Override
            public void run()
            {
                int uiParams = FullscreenUtil.getSystemUiVisibilityFlags(fullScreen, true);

                decorView.setSystemUiVisibility(uiParams);
            }
        });
    }

    private void doLayoutChanges(final boolean fullscreen)
    {
        Looper mainLooper = Looper.getMainLooper();
        boolean isMainLooperAlready = Looper.myLooper() == mainLooper;


        UpdateLayoutRunnable updateLayoutRunnable = new UpdateLayoutRunnable((AppCompatActivity) this.activity, fullscreen);

        if (isMainLooperAlready)
        {
            updateLayoutRunnable.run();
        }
        else
        {
            Handler handler = new Handler(mainLooper);
            handler.post(updateLayoutRunnable);
        }
    }

    @Override
    public void onFullscreenRequested()
    {
        this.handleFullscreen(true);
    }

    @Override
    public void onFullscreenExitRequested()
    {
        this.handleFullscreen(false);
    }

    @Override
    public void onResume()
    {
        if (isFullscreen)
        {
            doSystemUiVisibility(isFullscreen);
        }
    }

    @Override
    public void onPause()
    {
    }

    @Override
    public void onDestroy()
    {
        this.playerOrientationListener.disable();
    }

    public boolean isFullScreen()
    {
        return isFullscreen;
    }

    private class UpdateLayoutRunnable implements Runnable
    {
        private AppCompatActivity activity;
        private boolean fullscreen;

        private UpdateLayoutRunnable(AppCompatActivity activity, boolean fullscreen)
        {
            this.activity = activity;
            this.fullscreen = fullscreen;
        }

        @Override
        @SuppressLint("RestrictedApi")
        public void run()
        {
            if (CustomFullscreenHandler.this.toolbar != null)
            {
                if (this.fullscreen)
                {
                    CustomFullscreenHandler.this.toolbar.setVisibility(View.GONE);
                }
                else
                {
                    CustomFullscreenHandler.this.toolbar.setVisibility(View.VISIBLE);
                }
            }

            if (CustomFullscreenHandler.this.playerView.getParent() instanceof ViewGroup)
            {
                ViewGroup parentView = (ViewGroup) CustomFullscreenHandler.this.playerView.getParent();

                for (int i = 0; i < parentView.getChildCount(); i++)
                {
                    View child = parentView.getChildAt(i);
                    if (child != playerView)
                    {
                        child.setVisibility(fullscreen ? View.GONE : View.VISIBLE);
                    }
                }
            }
        }
    }
}
