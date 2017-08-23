/*
 * Bitmovin Player Android SDK
 * Copyright (C) 2017, Bitmovin GmbH, All Rights Reserved
 *
 * This source code and its use and distribution, is subject to the terms
 * and conditions of the applicable license agreement.
 */

package com.bitmovin.player.samples.casting.basic;

import android.app.Application;

import com.bitmovin.player.cast.BitmovinCastManager;

public class SampleApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();

        //The cast manager has to be initialized in the Application context, in order to work properly.
        BitmovinCastManager.initialize(this);
    }
}
