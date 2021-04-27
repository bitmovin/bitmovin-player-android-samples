package com.bitmovin.player.samples.casting.basic

import android.app.Application
import com.bitmovin.player.casting.BitmovinCastManager

class SampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize the BitmovinCastManager in the Application class
        // To use a custom expanded cast controller Activity use:
        // BitmovinCastManager.initialize(CustomCastControllerActivity.class);
        // Or to use a custom cast application:
        // BitmovinCastManager.initialize("APPID","MESSAGENAMESPACE");
        BitmovinCastManager.initialize()
    }
}
