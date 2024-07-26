# bitmovin-player-android-samples
This repository contains sample apps using the Bitmovin Player Android SDK.

**Table of Content**

* [Available Sample Apps](#available-sample-apps)
* [Sample Apps Setup Instructions](#sample-apps-setup-instructions)
* [How to integrate the Bitmovin Player Android SDK](#how-to-integrate-the-bitmovin-player-android-sdk)
    * [Project Requirements](#project-requirements)
    * [Add Dependencies to your build file](#using-the-bitmovin-player-android-sdk)
    * [Additional SDK dependencies](#additional-sdk-dependencies)
* [Proguard Configuration](#proguard-configuration)
* [Documentation & Release Notes](#documentation--release-notes)
* [Support](#support)

---

## Available Sample Apps
Every example is available in `Java` and `Kotlin` :+1:

### Basics
+   **BasicPlayback:** Shows how the Bitmovin Player can be setup for basic playback of DASH, HLS or progressive streams.
+   **BasicPlaylist:** Shows how to play back multiple sources / playlists.
+   **BasicMetadataHandling** Shows how the Bitmovin Player can be setup and configured for playback of content which contains metadata.
+   **BasicPlaybackTV** Shows how the Bitmovin Player can be setup for basic playback in an Android TV application.
+   **Logging** Shows how logging can be enabled in different Bitmovin Player components individually.

### DRM
+   **BasicDRMPlayback:** Shows how the Bitmovin Player can be setup and configured for playback of Widevine protected content.

### Offline Playback
+   **OfflinePlayback** Shows how the Bitmovin Android SDK can be used to download DRM-protected and unprotected content for offline playback.

### Playback & Casting
+ **BasicCasting** Shows how the ChromeCast support of the Bitmovin Player can be setup and configured.
+ **BackgroundPlayback** Shows how background playback can be implemented for the Bitmovin Player.
+ **BasicVrPlayback** Shows how the Bitmovin Player can be setup and configured for playback of VR content.
+ **BasicPiPHandling** Shows how the `BitmovinPlayerView` can be configured to allow the Picture in Picture mode.
+ **BasicControlNotification** Shows how the `BitmovinPlayerNotificationManager` can be used to show playback control notifications.
+ **BasicMediaControl** Shows how the `BitmovinPlayerNotificationManager` can be used to connect to Android's system media controls.
+ **BasicLowLatencyPlayback** Shows how the Bitmovin Player can be setup to playback streams in low latency mode.
+ **CustomAdaptation** Shows how the Bitmovin Player can be setup to implement custom adaptation behavior.

### Advertising
+   **ImaAds** Shows how the Bitmovin Player can be setup and configured for playback of ads using IMA SDK.
+   **BitmovinAds** Shows how the Bitmovin Player can be setup and configured for playback of ads using Bitmovin Advertising Module.
+   **BitmovinCustomAdsUi** Shows how the Bitmovin Player can be setup and configured to use a custom UI for ads.
+   **CompanionAds** Show how the Bitmovin Player can be setup and configured to show companion ads using IMA SDK.

### UI & Subtitles
+   **CustomUi** Shows how the Bitmovin Player can be setup and configured to be controlled by a custom native UI.
+   **CustomUiSubtitleView** Shows how the Bitmovin Player can be used with the native BitmovinSubtitleView.
+   **CustomHtmlUi** Shows how the Bitmovin Player can be setup and configured to use a custom HTML UI.
                     In addition this sample includes how to communication between the javascript UI and the native code.
+   **BasicFullscreenHandling** Shows how a simple FullscreenHandler can be implemented and configured on the Bitmovin Player

## Sample Apps Setup Instructions
1) **Add your Bitmovin Player License Key** -
   In each sample app you have to add your Bitmovin Player license key inside the `application` tag in
   the manifest file as shown below. `{PLAYER_LICENSE_KEY}` has to be replaced by your own license key.

    ```
   <meta-data
       android:name="BITMOVIN_PLAYER_LICENSE_KEY"
       android:value="{PLAYER_LICENSE_KEY}" />
   ```

2) **Allow-list your package names for your player license** -
   In addition to that you have to log in to [https://bitmovin.com/dashboard](https://bitmovin.com/dashboard)
   where you have to add the following package names of the sample applications as allowed domains under `Player -> Licenses`:
   ```
    com.bitmovin.player.samples.playback.basic
    com.bitmovin.player.samples.playlist.basic
    com.bitmovin.player.samples.drm.basic
    com.bitmovin.player.samples.metadata.basic
    com.bitmovin.player.samples.ads.ima
    com.bitmovin.player.samples.ads.bitmovin
    com.bitmovin.player.samples.ads.companion
    com.bitmovin.player.samples.casting.basic
    com.bitmovin.player.samples.vr.basic
    com.bitmovin.player.samples.custom.ui
    com.bitmovin.player.samples.custom.ui.subtitleview
    com.bitmovin.player.samples.fullscreen.basic
    com.bitmovin.player.samples.custom.ui.html
    com.bitmovin.player.samples.offline.playback
    com.bitmovin.player.samples.pip.basic
    com.bitmovin.player.samples.notification.basic
    com.bitmovin.player.samples.mediacontrol.basic
    com.bitmovin.player.samples.playback.background
    com.bitmovin.player.samples.tv.playback.basic
    com.bitmovin.player.samples.playback.lowlatency
    com.bitmovin.player.samples.custom.adaptation
    com.bitmovin.player.samples.logging
    ```

## How to integrate the Bitmovin Player Android SDK

### Project Requirements
Starting with version `2.16.0` our SDK is using Java 8, so your application must enable Java 8 support.
This can be done by adding following code to your applications build gradle:

    ```
    compileOptions {
            sourceCompatibility JavaVersion.VERSION_1_8
            targetCompatibility JavaVersion.VERSION_1_8
        }
    ```

### Using The Bitmovin Player Android SDK
When you want to develop an own Android application using the Bitmovin Player Android SDK follow these steps:

1.  In the manifest file of your application also provide your Bitmovin Player license key by adding
    a `meta-data` tag inside the `application` tag. `{PLAYER_LICENSE_KEY}` has to be replaced by your own license key. 
    Your player license key can be found when logging in into [https://bitmovin.com/dashboard](https://bitmovin.com/dashboard)
    and navigating to `Player -> Licenses`.
    There you also have to add the package name of the Android application which is using the SDK as an allow-listed domain.
    The package name is defined in the manifest file of your Android application.

    ```
    <meta-data
            android:name="BITMOVIN_PLAYER_LICENSE_KEY"
            android:value="{PLAYER_LICENSE_KEY}" />
    ```

2.  Make sure to add the `INTERNET` permission, which is required by the SDK, to the manifest file of your application

    ```
    <uses-permission android:name="android.permission.INTERNET" />
    ```

3.  Add a link to our release repository to your applications `build.gradle` file.
    In addition to that, the google maven repository must be added.

    ```
    allprojects {
        repositories {
            google()
            mavenCentral()

            maven {
                url 'https://artifacts.bitmovin.com/artifactory/public-releases'
            }
        }
    }
    ```

4.  Add the Bitmovin Player Android SDK as a dependency to your project.
    It is recommended to reference a specific version as you can see below:

    ```
    implementation 'com.bitmovin.player:player:3.77.1'
    ```

#### Additional SDK dependencies

1. **Chromecast Support** -
    If you want to use the Chromecast feature, add the following dependencies to your project:

    ```
    implementation 'com.google.android.gms:play-services-cast-framework:21.4.0'
    implementation 'androidx.mediarouter:mediarouter:1.3.1'
    implementation 'androidx.appcompat:appcompat:1.6.1'
    ```

    Furthermore, the `BitmovinCastOptionsProvider` and the `ExpandedControllerActivity` must be declared in the
    `<application>` tag of the `AndroidManifest.xml`. For more details see the  `BasicCasting` sample application.

    ```
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
     ```

2. **Advertising with IMA SDK** -
    If advertising support should be enabled, also add the following dependencies to your project:

    ```
    implementation 'com.google.ads.interactivemedia.v3:interactivemedia:3.31.0'
    implementation 'com.google.android.gms:play-services-ads-identifier:18.0.1'
     ```

    And update the manifest file with an Ad Manager <meta-data> tag:

    ```
    <meta-data
            android:name="com.google.android.gms.ads.AD_MANAGER_APP"
            android:value="true"/>
    ```

    Alternatively, you can use an AdMob App ID `<meta-data>` tag, as shown below:

     ```
     <meta-data
             android:name="com.google.android.gms.ads.APPLICATION_ID"
             android:value="{ADMOB_LICENSE_KEY}"/>
     ```

3. **Offline Playback Support** - 
    If the Bitmovin Android SDK is used to download content for offline playback, the following dependency has to be added:

    ```
    implementation 'androidx.localbroadcastmanager:localbroadcastmanager:1.0.0'
    ```

    In addition, also add the permission for checking the network state and starting a foreground service.
    If targeting Android SDK version 33+ the permission for posting notifications has to be added as well.
    When targeting Android SDK 34+, the `FOREGROUND_SERVICE_DATA_SYNC` permission is also required:

    ```
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE_DATA_SYNC"/>
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS"/>
    ```

4. **Player Notification** -
    Beginning with Android 13, a permission is needed for posting notification. 
    This means that if the `PlayerNotificationManager` is used, like in the **Background Playback** sample,
    the post notification permission has to be added when targeting SDK version 33+:

    ```
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS"/>
    ```

    Additionally the permission also has to be requested at runtime.
    This is explained in the [official Android docs](https://developer.android.com/training/permissions/requesting).

## Proguard Configuration

When using `Proguard`, we recommend doing no further optimization nor code obfuscation for symbols contained in
the package `com.bitmovin.player`. This can be achieved by adding following `Proguard` rules to your project.

```proguard
#### Bitmovin
-keep class com.bitmovin.player.** { *; }
-keep interface com.bitmovin.player.** { *; }
```

The Bitmovin Player Android SDK logs against a logging facade (SLF4J).
In order to be able to compile successfully with proguard enabled, the following line must be added to the proguard file:
```
-dontwarn org.slf4j.**
```

## Documentation & Release Notes

+ **Documentation for your IDE** - Our release repository also holds a `.jar` containing the javadoc for the `player`, as well as the source code of the api package.
                                   If AndroidStudio is used, the documentation and sources should be downloaded and included automatically .
+ **Android API Reference documentation** - You can find the latest one [here](https://cdn.bitmovin.com/player/android/3/docs/index.html)
+ **Android SDK Release Notes** can be found [here](https://bitmovin.com/release-notes-android-sdk/)

## Support
If you have any questions or issues with this SDK or its examples, or you require other technical support for our services,
please login to your Bitmovin Dashboard at https://bitmovin.com/dashboard and [create a new support case](https://bitmovin.com/dashboard/support/cases/create).
Our team will get back to you as soon as possible :+1:
