package com.bitmovin.player.samples.integration.adobeanalytics

import android.app.Application
import android.util.Log
import com.adobe.marketing.mobile.*

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // register adobe extensions for the Application
        MobileCore.setApplication(this)
        MobileCore.setLogLevel(LoggingMode.DEBUG)
        try {
            Media.registerExtension()
            Analytics.registerExtension()
            Identity.registerExtension()
            MobileCore.start {
                // replace "your-launch-app-id" with your Adobe "launch-app-id"
                MobileCore.configureWithAppID("your-launch-app-id")
            }
        } catch (e: InvalidInitException) {
            Log.e("MainApplication", "Failed to setup Adobe Analytics Mobile SDK extensions", e)
        }
    }
}