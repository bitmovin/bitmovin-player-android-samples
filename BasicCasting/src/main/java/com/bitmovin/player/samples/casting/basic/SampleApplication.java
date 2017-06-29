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
