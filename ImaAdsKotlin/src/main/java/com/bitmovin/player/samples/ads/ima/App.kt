package com.bitmovin.player.samples.ads.ima

import android.app.Application
import com.google.ads.interactivemedia.v3.api.ImaSdkFactory
import com.google.ads.interactivemedia.v3.api.ImaSdkSettings
import com.bitmovin.player.api.advertising.ima.ImaConfig

class App : Application() {
    override fun onCreate() {
        val imaSdkFactory = ImaSdkFactory.getInstance()
        // The Kotlin compiler gets confused by the overloaded initialize methods, so we need to
        // help it a bit by creating a variable with the correct function reference
        val initializeImaSdkFactory = imaSdkFactory::initialize

        // Initialize IMA early to speed up ad loading and resource preparation.
        // See https://developers.google.com/interactive-media-ads/docs/sdks/android/client-side/load-time
        initializeImaSdkFactory(
            this,
            imaSdkFactory.createImaSdkSettings().applyImaSdkSettings()
        )
        super.onCreate()
    }
}

/**
 * Apply custom settings to the IMA SDK settings instance. The same settings should be applied to
 * the initialize method and [ImaConfig.beforeInitialization] to ensure efficient pre-loading of
 * resources.
 */
fun ImaSdkSettings.applyImaSdkSettings(): ImaSdkSettings = apply {
    language = "en"
}
