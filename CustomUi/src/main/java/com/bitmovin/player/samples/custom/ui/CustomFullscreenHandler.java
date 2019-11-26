/*
 * Bitmovin Player Android SDK
 * Copyright (C) 2017, Bitmovin GmbH, All Rights Reserved
 *
 * This source code and its use and distribution, is subject to the terms
 * and conditions of the applicable license agreement.
 */

package com.bitmovin.player.samples.custom.ui;


import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;

import com.bitmovin.player.ui.FullscreenHandler;
import com.bitmovin.player.ui.FullscreenUtil;

public class CustomFullscreenHandler implements FullscreenHandler
{
    private Activity activity;
    private View decorView;
    private PlayerUI playerUI;

    private PlayerOrientationListener playerOrientationListener;

    private boolean isFullscreen;


    public CustomFullscreenHandler(Activity activity, PlayerUI playerUI)
    {
        this.activity = activity;
        this.playerUI = playerUI;
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
        ActionBar actionBar = ((AppCompatActivity) this.activity).getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setShowHideAnimationEnabled(false);
            if (fullscreen)
            {
                actionBar.hide();
            }
            else
            {
                actionBar.show();
            }
        }

        if (this.playerUI.getParent() instanceof ViewGroup)
        {
            ViewGroup parentView = (ViewGroup) this.playerUI.getParent();

            for (int i = 0; i < parentView.getChildCount(); i++)
            {
                View child = parentView.getChildAt(i);
                if (child != playerUI)
                {
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
        if (this.isFullscreen)
        {
            this.doSystemUiVisibility(this.isFullscreen);
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
        return this.isFullscreen;
    }
}
