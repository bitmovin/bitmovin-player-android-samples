package com.bitmovin.player.samples.playback.basic

import android.app.Application
import com.bitmovin.player.api.DebugConfig

class App : Application() {
    override fun onCreate() {
        // DebugConfig.isLoggingEnabled = true  // Enable verbose debug logging in case of issues
        super.onCreate()
    }
}
