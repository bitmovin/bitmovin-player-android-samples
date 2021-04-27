/*
 * Bitmovin Player Android SDK
 * Copyright (C) 2017, Bitmovin GmbH, All Rights Reserved
 *
 * This source code and its use and distribution, is subject to the terms
 * and conditions of the applicable license agreement.
 */

package com.bitmovin.player.samples.casting.basic;

import android.app.Application;

import com.bitmovin.player.casting.BitmovinCastManager;


public class SampleApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();

        // Initialize the BitmovinCastManager in the Application class
        // To use a custom expanded cast controller Activity use:
        // BitmovinCastManager.initialize(CustomCastControllerActivity.class);
        // Or to use a custom cast application:
        // BitmovinCastManager.initialize("APPID","MESSAGENAMESPACE");
        BitmovinCastManager.initialize();
    }
}
