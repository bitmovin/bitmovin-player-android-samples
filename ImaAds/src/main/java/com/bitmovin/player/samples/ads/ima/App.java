package com.bitmovin.player.samples.ads.ima;

import android.app.Application;
import com.google.ads.interactivemedia.v3.api.ImaSdkFactory;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize IMA early to speed up ad loading and resource preparation.
        // See https://developers.google.com/interactive-media-ads/docs/sdks/android/client-side/load-time
        ImaSdkFactory imaSdkFactory = ImaSdkFactory.getInstance();
        imaSdkFactory.initialize(
                this,
                ImaSdkSettingsConfigurer.apply(imaSdkFactory.createImaSdkSettings())
        );
    }
}

