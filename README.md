# bitmovin-player-android-samples
This repository contains sample apps using the Bitmovin Player Android SDK. The following sample apps are included:

+   **BasicPlayback:** Shows how the Bitmovin Player can be setup for basic playback of DASH, HLS or progressive streams.
+   **BasicDRMPlayback:** Shows how the Bitmovin Player can be setup and configured for playback of Widevice Modular protected content.
+   **BasicMetadataHandling** Shows how the Bitmovin Player can be setup and configured for playback of content which contains metadata.
+   **BasicCasting** Shows how the ChromeCast support of the Bitmovin Player can be setup and configured.
+   **CustomUi** Shows how the Bitmovin Player can be setup and configured to be controlled by a custom native UI.
+   **CustomHtmlUi** Shows how the Bitmovin Player can be setup and configured to use a custom HTML UI.

## Using The Sample Apps
In each sample app you have to add you Bitmovin Player license key inside the `application` tag in the manifest file as shown below. `YOUR_LICENSE_KEY` has to be replaced by your own license key.
    
    <meta-data
                android:name="BITMOVIN_PLAYER_LICENSE_KEY"
                android:value="YOUR_LICENSE_KEY" />

In addition to that you have to log in to `https://app.bitmovin.com/` where you have to add the following package names of the sample applications as allowed domains:

    com.bitmovin.player.samples.playback.basic
    com.bitmovin.player.samples.drm.basic
    com.bitmovin.player.samples.metadata.basic
    com.bitmovin.player.samples.casting.basic
    com.bitmovin.player.samples.custom.ui
    com.bitmovin.player.samples.custom.ui.html

## Using The Bitmovin Player Android SDK
When you want to develop an own Android application using the Bitmovin Player Android SDK follow these steps:

1.  Add a link to our release repository to your applications `build.gradle` file.
    
        allprojects {
            repositories {
                jcenter()
                maven {
                    url 'http://bitmovin.bintray.com/maven'
                }
            }
        }
        
1.  Add the Bitmovin Player Android SDK as a dependency to your project. It is recommended to reference a specific version as you can see below:

        compile 'com.bitmovin.player:playercore:1.1.0'
        
1.  Additionally, if you want to use the Chromecast feature, add the following dependencies to your project:
    
        compile 'com.google.android.libraries.cast.companionlibrary:ccl:2.9.1'
        compile 'com.android.support:mediarouter-v7:25.3.1'
    
1.  Make sure to add the `INTERNET` permission, which is required by the SDK, to the manifest file of your application
        
        <uses-permission android:name="android.permission.INTERNET" />

1.  In the manifest file of your application also provide your Bitmovin Player license key by adding a `meta-data` tag inside the `application` tag. `YOUR_LICENSE_KEY` has to be replaced by your own license key:

        <meta-data
                android:name="BITMOVIN_PLAYER_LICENSE_KEY"
                android:value="YOUR_LICENSE_KEY" />

    Your player license key can be found when logging in into `https://app.bitmovin.com/` and navigating to `Player -> Overview`. There you also have to add the package name of the Android application which is using the SDK as a domain. The package name is defined in the manifest file of the Android application.

## Additional Resources

+   You can find the latest API documentation [here](https://bitmovin.com/android-sdk-documentation/)
+   The release notes can be found [here]( https://bitmovin.com/release-notes-android-sdk/)