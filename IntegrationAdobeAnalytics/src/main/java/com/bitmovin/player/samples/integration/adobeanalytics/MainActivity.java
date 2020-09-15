package com.bitmovin.player.samples.integration.adobeanalytics;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.bitmovin.player.BitmovinPlayer;
import com.bitmovin.player.BitmovinPlayerView;
import com.bitmovin.player.api.event.data.AdBreakStartedEvent;
import com.bitmovin.player.api.event.data.AdStartedEvent;
import com.bitmovin.player.config.PlayerConfiguration;
import com.bitmovin.player.config.StyleConfiguration;
import com.bitmovin.player.config.advertising.AdItem;
import com.bitmovin.player.config.advertising.AdSource;
import com.bitmovin.player.config.advertising.AdSourceType;
import com.bitmovin.player.config.advertising.AdvertisingConfiguration;
import com.bitmovin.player.config.media.SourceItem;
import com.bitmovin.player.integration.adobeanalytics.AdobeMediaAnalyticsDataOverride;
import com.bitmovin.player.integration.adobeanalytics.AdobeMediaAnalyticsTracker;
import com.bitmovin.player.model.advertising.AdBreak;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity
{
    // These are IMA Sample Tags from https://developers.google.com/interactive-media-ads/docs/sdks/android/tags
    private static final String VMAP_SOURCE = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=vmap&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar%3Dpremidpostlongpod&cmsid=496&vid=short_tencue&correlator=";

    private BitmovinPlayerView bitmovinPlayerView;
    private AdobeMediaAnalyticsTracker adobeAnalyticsTracker;
    private AdobeMediaAnalyticsDataOverride customDataOverride;

    public class CustomAdobeMediaDataOverride extends AdobeMediaAnalyticsDataOverride {

        private long activeAdBreakPosition = 0L;
        private long activeAdPosition = 0L;

        @Override
        public HashMap<String, String> getMediaContextData (BitmovinPlayer player) {
            HashMap<String, String> contextData = new HashMap<String, String>();
            contextData.put("os", "Android");
            contextData.put("version", "9.0");
            return contextData;
        }

        @Override
        public String getMediaName (BitmovinPlayer player, SourceItem activeSourceItem) {

            // return appropriate value for media name
            String mediaName = activeSourceItem.getTitle();
            return mediaName;
        }

        @Override
        public String getMediaUid (BitmovinPlayer player, SourceItem activeSourceItem) {
            // return appropriate value for media id
            String mediaId = activeSourceItem.getDashSource().getUrl();
            return mediaId;
        }

        @Override
        public String getAdBreakId (BitmovinPlayer player, AdBreakStartedEvent event) {
            // reset ad position in adBreak when receiving new adBreak event
            activeAdPosition = 0L;

            // return appropriate value for adBreak id
            AdBreak adBreak = event.getAdBreak();
            String adBreakId = adBreak.getId();
            return adBreakId;
        }

        @Override
        public long getAdBreakPosition (BitmovinPlayer player, AdBreakStartedEvent event) {
            // reset ad position in adBreak when receiving new adBreak event
            activeAdPosition = 0L;

            // return position of AdBreak in the content playback
            Double scheduledTime = event.getAdBreak().getScheduleTime();
            if (scheduledTime == 0.0D) {
                // preroll adBreak
                activeAdBreakPosition = 1L;
            } else if (scheduledTime == player.getDuration()) {
                // postroll adBreak
                activeAdBreakPosition++;
            } else {
                // midroll adBreak
                activeAdBreakPosition++;
            }
            return activeAdBreakPosition;
        }

        @Override
        public String getAdName (BitmovinPlayer player, AdStartedEvent event) {
            // return appropriate value representing Ad name
            return event.getAd().getMediaFileUrl();
        }

        @Override
        public String getAdId (BitmovinPlayer player, AdStartedEvent event) {
            // return appropriate value representing Ad Id
            return event.getAd().getId();
        }

        @Override
        public long getAdPosition (BitmovinPlayer player, AdStartedEvent event) {
            // return position of Ad in Ad break
            return ++activeAdPosition;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Add a new source item
        SourceItem sourceItem = new SourceItem("https://bitmovin-a.akamaihd.net/content/MI201109210084_1/mpds/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.mpd");
        sourceItem.setTitle("Art of Motion");

        // Create AdSources
        AdSource vmapAdSource = new AdSource(AdSourceType.IMA, VMAP_SOURCE);

        // Setup a post-roll ad
        AdItem vmap = new AdItem("", vmapAdSource);

        // Add the AdItems to the AdvertisingConfiguration
        AdvertisingConfiguration advertisingConfiguration = new AdvertisingConfiguration(vmap);

        StyleConfiguration styleConfiguration = new StyleConfiguration();
        styleConfiguration.setHideFirstFrame(true);

        // Creating a new PlayerConfiguration
        PlayerConfiguration playerConfiguration = new PlayerConfiguration();
        // Assign created SourceItem to the PlayerConfiguration
        playerConfiguration.setSourceItem(sourceItem);
        // Assing the AdvertisingConfiguration to the PlayerConfiguration
        // All ads in the AdvertisingConfiguration will be scheduled automatically
        playerConfiguration.setAdvertisingConfiguration(advertisingConfiguration);

        playerConfiguration.setStyleConfiguration(styleConfiguration);

        // Create new BitmovinPlayerView with our PlayerConfiguration
        this.bitmovinPlayerView = new BitmovinPlayerView(this, playerConfiguration);
        this.bitmovinPlayerView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        LinearLayout rootView = (LinearLayout) this.findViewById(R.id.activity_main);

        // Add BitmovinPlayerView to the layout
        rootView.addView(this.bitmovinPlayerView, 0);

        // setup and start Adobe Media analytics tracker
        adobeAnalyticsTracker = new AdobeMediaAnalyticsTracker();
        customDataOverride = new CustomAdobeMediaDataOverride();
        adobeAnalyticsTracker.createTracker(this.bitmovinPlayerView.getPlayer(), customDataOverride);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        this.bitmovinPlayerView.onStart();
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
    protected void onStop()
    {
        this.bitmovinPlayerView.onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        // destroy Adobe Media analytics tracker
        this.adobeAnalyticsTracker.destroyTracker();
        this.bitmovinPlayerView.onDestroy();
        super.onDestroy();
    }
}
