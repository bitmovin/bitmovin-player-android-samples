package com.bitmovin.player.samples.analytics;

import android.app.Application;

import com.bitmovin.player.api.DebugConfig;

public class App extends Application {
    @Override
    public void onCreate() {
        // DebugConfig.setLoggingEnabled(true); // Enable verbose debug logging in case of issues
        super.onCreate();
    }
}
