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

    private boolean isFullscreen;


    public CustomFullscreenHandler(Activity activity, PlayerUI playerUI)
    {
        this.activity = activity;
        this.playerUI = playerUI;
        this.decorView = activity.getWindow().getDecorView();
    }


    private void handleFullscreen(boolean fullscreen)
    {
        this.isFullscreen = fullscreen;

        this.doRotation(fullscreen);
        this.doSystemUiVisibility(fullscreen);
        this.doLayoutChanges(fullscreen);
    }

    private void doRotation(boolean fullScreen)
    {
        int rotation = this.activity.getWindowManager().getDefaultDisplay().getRotation();

        if (playerUI != null)
        {
            playerUI.setVisible(false);
        }

        if (fullScreen)
        {
            switch (rotation)
            {
                case Surface.ROTATION_270:
                    this.activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                    break;

                default:
                    this.activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }
        else
        {
            this.activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
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

            ViewGroup.LayoutParams params = playerUI.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            playerUI.setLayoutParams(params);
            playerUI.setPadding(0, 0, 0, 0);
        }

        if (playerUI != null)
        {
            playerUI.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    playerUI.setVisible(true);
                }
            }, 600L);
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
    }

    public boolean isFullScreen()
    {
        return isFullscreen;
    }
}
