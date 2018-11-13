package com.bitmovin.player.samples.ads.basic;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.bitmovin.player.BitmovinPlayerView;
import com.bitmovin.player.config.PlayerConfiguration;
import com.bitmovin.player.config.advertising.AdItem;
import com.bitmovin.player.config.advertising.AdSource;
import com.bitmovin.player.config.advertising.AdSourceType;
import com.bitmovin.player.config.advertising.AdvertisingConfiguration;
import com.bitmovin.player.config.media.SourceConfiguration;

public class MainActivity extends AppCompatActivity
{
    // These are IMA Sample Tags from https://developers.google.com/interactive-media-ads/docs/sdks/android/tags
    private static final String AD_SOURCE_1 = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dredirecterror&nofb=1&correlator=";
    private static final String AD_SOURCE_2 = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dlinear&correlator=";
    private static final String AD_SOURCE_3 = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dskippablelinear&correlator=";
    private static final String AD_SOURCE_4 = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dredirectlinear&correlator=";

    private BitmovinPlayerView bitmovinPlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create a new source configuration
        SourceConfiguration sourceConfiguration = new SourceConfiguration();
        // Add a new source item
        sourceConfiguration.addSourceItem("https://bitdash-a.akamaihd.net/content/sintel/sintel.mpd");

        // Create AdSources
        AdSource firstAdSource = new AdSource(AdSourceType.IMA, AD_SOURCE_1);
        AdSource secondAdSource = new AdSource(AdSourceType.IMA, AD_SOURCE_2);
        AdSource thirdAdSource = new AdSource(AdSourceType.IMA, AD_SOURCE_3);
        AdSource fourthAdSource = new AdSource(AdSourceType.IMA, AD_SOURCE_4);

        // Setup a pre-roll ad
        AdItem preRoll = new AdItem("pre", thirdAdSource);
        // Setup a mid-roll waterfalling ad at 10% of the content duration
        // NOTE: AdItems containing more than one AdSource, will be executed as waterfalling ad
        AdItem midRoll = new AdItem("10%", firstAdSource, secondAdSource);
        // Setup a post-roll ad
        AdItem postRoll = new AdItem("post", fourthAdSource);

        // Add the AdItems to the AdvertisingConfiguration
        AdvertisingConfiguration advertisingConfiguration = new AdvertisingConfiguration(preRoll, midRoll, postRoll);

        // Creating a new PlayerConfiguration
        PlayerConfiguration playerConfiguration = new PlayerConfiguration();
        // Assign created SourceConfiguration to the PlayerConfiguration
        playerConfiguration.setSourceConfiguration(sourceConfiguration);
        // Assing the AdvertisingConfiguration to the PlayerConfiguration
        // All ads in the AdvertisingConfiguration will be scheduled automatically
        playerConfiguration.setAdvertisingConfiguration(advertisingConfiguration);

        // Create new BitmovinPlayerView with our PlayerConfiguration
        this.bitmovinPlayerView = new BitmovinPlayerView(this, playerConfiguration);
        this.bitmovinPlayerView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        LinearLayout rootView = (LinearLayout) this.findViewById(R.id.activity_main);

        // Add BitmovinPlayerView to the layout
        rootView.addView(this.bitmovinPlayerView, 0);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        this.bitmovinPlayerView.onResume();
    }

    @Override
    protected void onPause()
    {
        this.bitmovinPlayerView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy()
    {
        this.bitmovinPlayerView.onDestroy();
        super.onDestroy();
    }
}
