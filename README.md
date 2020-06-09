# bitmovin-player-android-samples
This repository contains sample apps using the Bitmovin Player Android SDK. The following sample apps are included:

+   **BasicPlayback:** Shows how the Bitmovin Player can be setup for basic playback of DASH, HLS or progressive streams.
+   **BasicPlaylist:** Shows how to implement queueing / playlists.
+   **BasicDRMPlayback:** Shows how the Bitmovin Player can be setup and configured for playback of Widevice Modular protected content.
+   **BasicMetadataHandling** Shows how the Bitmovin Player can be setup and configured for playback of content which contains metadata.
+   **BasicAds** Shows how the Bitmovin Player can be setup and configured for playback of ads.  
+   **BasicCasting** Shows how the ChromeCast support of the Bitmovin Player can be setup and configured.
+   **CustomUi** Shows how the Bitmovin Player can be setup and configured to be controlled by a custom native UI.
+   **CustomUiSubtitleView** Shows how the Bitmovin Player can be used with the native BitmovinSubtitleView.
+   **CustomHtmlUi** Shows how the Bitmovin Player can be setup and configured to use a custom HTML UI. In addition this sample includes how to communication between the javascript UI and the native code.
+   **BasicFullscreenHandling** Shows how a simple FullscreenHandler can be implemented and configured on the Bitmovin Player
+   **OfflinePlayback** Shows how the Bitmovin Android SDK can be used to download protected and unprotected content for offline playback.
+   **BasicVrPlayback** Shows how the Bitmovin Player can be setup and configured for playback of VR content.
+   **BasicPlayerFragment** Shows how the `BitmovinPlayerFragment` can be used to setup playback.
+   **BasicPiPHandling** Shows how the `BitmovinPlayerView` can be configured to allow the Picture in Picture mode.
+   **BasicControlNotification** Shows how the `BitmovinPlayerNotificationManager` can be used to show playback control notifications.
+   **BackgroundPlayback** Shows how background playback can be implemented for the Bitmovin Player.
+   **BasicPlaybackTV** Shows how the Bitmovin Player can be setup for basic playback in an Android TV application.
+   **BasicLowLatencyPlayback** Shows how the Bitmovin Player can be setup to playback streams in low latency mode.
+   **CustomAdaptation** Shows how the Bitmovin Player can be setup to implement custom adaptation behavior.

## Using The Sample Apps
In each sample app you have to add your Bitmovin Player license key inside the `application` tag in the manifest file as shown below. `YOUR_LICENSE_KEY` has to be replaced by your own license key.

    <meta-data
                android:name="BITMOVIN_PLAYER_LICENSE_KEY"
                android:value="YOUR_LICENSE_KEY" />

In addition to that you have to log in to [https://dashboard.bitmovin.com](https://dashboard.bitmovin.com) where you have to add the following package names of the sample applications as allowed domains under `Player -> Licenses`:

    com.bitmovin.player.samples.playback.basic
    com.bitmovin.player.samples.playlist.basic
    com.bitmovin.player.samples.drm.basic
    com.bitmovin.player.samples.metadata.basic
    com.bitmovin.player.samples.ads.basic
    com.bitmovin.player.samples.casting.basic
    com.bitmovin.player.samples.vr.basic
    com.bitmovin.player.samples.custom.ui
    com.bitmovin.player.samples.custom.ui.subtitleview
    com.bitmovin.player.samples.fullscreen.basic
    com.bitmovin.player.samples.custom.ui.html
    com.bitmovin.player.samples.offline.playback
    com.bitmovin.player.samples.playerfragment.basic
    com.bitmovin.player.samples.pip.basic
    com.bitmovin.player.samples.notification.basic
    com.bitmovin.player.samples.playback.background
    com.bitmovin.player.samples.tv.playback.basic
    com.bitmovin.player.samples.playback.lowlatency
    com.bitmovin.player.samples.custom.adaptation

## Using The Bitmovin Player Android SDK
When you want to develop an own Android application using the Bitmovin Player Android SDK follow these steps:

1.  Add a link to our release repository to your applications `build.gradle` file. In addition to that, the google maven repository must be added.

        allprojects {
            repositories {
                google()
                jcenter()

                maven {
                    url 'http://bitmovin.bintray.com/maven'
                }
            }
        }

1.  Add the Bitmovin Player Android SDK as a dependency to your project. It is recommended to reference a specific version as you can see below:

        implementation 'com.bitmovin.player:playercore:2.47.0'

1.  Additionally, if you want to use the Chromecast feature, add the following dependencies to your project:

        implementation 'com.google.android.gms:play-services-cast-framework:17.0.0'
        implementation 'androidx.legacy:legacy-support-v4:1.0.0'
        implementation 'androidx.mediarouter:mediarouter:1.1.0'
        implementation 'androidx.appcompat:appcompat:1.1.0'

    Furthermore, the `BitmovinCastOptionsProvider` and the `ExpandedControllerActivity` must be declared in the `<application>` tag of the `AndroidManifest.xml`.
    For more details see the  `BasicCasting` sample application.

         <activity
                 android:name="com.bitmovin.player.cast.ExpandedControllerActivity"
                 android:launchMode="singleTask"
                 android:screenOrientation="portrait"
                 ... >
         </activity>

         <meta-data
                 android:name="com.google.android.gms.cast.framework.OPTIONS_PROVIDER_CLASS_NAME"
                 android:value="com.bitmovin.player.cast.BitmovinCastOptionsProvider"
                 />

1.  If advertising support should be enabled, also add the following dependencies to your project:

         implementation 'com.google.ads.interactivemedia.v3:interactivemedia:3.11.3'
         implementation 'com.google.android.gms:play-services-ads:17.1.2'
         implementation 'androidx.legacy:legacy-support-v4:1.0.0'

     And update the manifest file with an Ad Manager <meta-data> tag:

         <meta-data
                 android:name="com.google.android.gms.ads.AD_MANAGER_APP"
                 android:value="true"/>

     Alternatively, you can use an AdMob App ID <meta-data> tag, as shown below:

         <meta-data
                 android:name="com.google.android.gms.ads.APPLICATION_ID"
                 android:value="--Your-AdMob-Id--"/>

1.  Make sure to add the `INTERNET` permission, which is required by the SDK, to the manifest file of your application

        <uses-permission android:name="android.permission.INTERNET" />

1.  If the Bitmovin Android SDK is used to download content for offline playback, also add the permission for checking the network state

        <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

1.  In the manifest file of your application also provide your Bitmovin Player license key by adding a `meta-data` tag inside the `application` tag. `YOUR_LICENSE_KEY` has to be replaced by your own license key:

        <meta-data
                android:name="BITMOVIN_PLAYER_LICENSE_KEY"
                android:value="YOUR_LICENSE_KEY" />

    Your player license key can be found when logging in into [https://dashboard.bitmovin.com](https://dashboard.bitmovin.com) and navigating to `Player -> Licenses`. There you also have to add the package name of the Android application which is using the SDK as a domain. The package name is defined in the manifest file of the Android application.

1.  Starting with `2.16.0` our SDK is using Java 8, so your application must enable Java 8 support. This can be done by adding following code to your applications build gradle:

        compileOptions {
                sourceCompatibility JavaVersion.VERSION_1_8
                targetCompatibility JavaVersion.VERSION_1_8
            }

## Proguard

When using Proguard, we recommend to do no further optimization nor code obfuscation for symbols contained in the package `com.bitmovin.player`.
This can be achieved by adding following Progurad rules to your project.

```proguard
#### Bitmovin
-keep class com.bitmovin.player.** { *; }
-keep interface com.bitmovin.player.** { *; }
```

The Bitmovin Player Android SDK logs against a logging facade (SLF4J). In order to be able to compile successfully with proguard enabled, the following line must be added to the proguard file:
```
-dontwarn org.slf4j.**
```

## Java Documentation

Our release repository also holds a `.jar` containing the javadoc for the `playercore`.
If AndroidStudio is used, the documentation should be downloaded and included automatically .

## Additional Resources

+   You can find the latest API documentation [here](https://bitmovin.com/android-sdk-documentation/)
+   The release notes can be found [here]( https://bitmovin.com/release-notes-android-sdk/)
