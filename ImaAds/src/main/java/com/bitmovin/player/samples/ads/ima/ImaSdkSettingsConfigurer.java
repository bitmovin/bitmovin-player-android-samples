package com.bitmovin.player.samples.ads.ima;

import com.google.ads.interactivemedia.v3.api.ImaSdkSettings;
import com.bitmovin.player.api.advertising.ima.ImaConfig;

/**
 * Apply custom settings to the IMA SDK settings instance. The same settings should be applied to
 * the initialize method and {@link ImaConfig#beforeInitialization} to ensure
 * efficient pre-loading of resources.
 */
class ImaSdkSettingsConfigurer {
    public static ImaSdkSettings apply(ImaSdkSettings settings) {
        settings.setLanguage("en");
        return settings;
    }
}
