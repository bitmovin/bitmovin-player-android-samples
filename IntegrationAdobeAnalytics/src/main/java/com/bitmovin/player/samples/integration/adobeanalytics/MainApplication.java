package com.bitmovin.player.samples.integration.adobeanalytics;

import android.app.Application;

import com.adobe.marketing.mobile.*;
import com.adobe.marketing.mobile.AdobeCallback;
import com.adobe.marketing.mobile.Analytics;
import com.adobe.marketing.mobile.Identity;
import com.adobe.marketing.mobile.InvalidInitException;
import com.adobe.marketing.mobile.Lifecycle;
import com.adobe.marketing.mobile.LoggingMode;
import com.adobe.marketing.mobile.Media;
import com.adobe.marketing.mobile.MobileCore;
import com.adobe.marketing.mobile.Signal;

import android.util.Log;

public class MainApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();

        // register adobe extensions for the Application
        MobileCore.setApplication(this);
        MobileCore.setLogLevel(LoggingMode.DEBUG);
        try {
            Media.registerExtension();
            Analytics.registerExtension();
            Identity.registerExtension();
            MobileCore.start(new AdobeCallback () {
                @Override
                public void call(Object o) {
                    // replace "your-launch-app-id" with your Adobe "launch-app-id"
                    MobileCore.configureWithAppID("your-launch-app-id");
                }
            });
        } catch (InvalidInitException e) {
            Log.e ("MainApplication", "Failed to setup Adobe Analytics Mobile SDK extensions", e);
        }
    }
}
