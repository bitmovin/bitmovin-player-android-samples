package com.bitmovin.player.samples.ads.companion;

import android.os.Bundle;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.bitmovin.analytics.api.AnalyticsConfig;
import com.bitmovin.player.PlayerView;
import com.bitmovin.player.api.Player;
import com.bitmovin.player.api.PlayerConfig;
import com.bitmovin.player.api.advertising.AdItem;
import com.bitmovin.player.api.advertising.AdSource;
import com.bitmovin.player.api.advertising.AdSourceType;
import com.bitmovin.player.api.advertising.AdvertisingConfig;
import com.bitmovin.player.api.advertising.CompanionAdContainer;
import com.bitmovin.player.api.analytics.PlayerFactory;
import com.bitmovin.player.api.source.SourceConfig;
import com.bitmovin.player.api.source.SourceType;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String AD_TAG = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dlinear&correlator=";

    private PlayerView bitmovinPlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewGroup companionAdContainerView = findViewById(R.id.companionAdContainer);
        bitmovinPlayerView = findViewById(R.id.bitmovinPlayerView);

        // Setup companion ad container
        List<CompanionAdContainer> companionAdContainerList = new ArrayList<>();
        CompanionAdContainer companionAdContainer = new CompanionAdContainer(companionAdContainerView, 300, 250);
        companionAdContainerList.add(companionAdContainer);

        AdItem adItem = new AdItem("pre", new AdSource(AdSourceType.Ima, AD_TAG));
        AdvertisingConfig advertisingConfig = new AdvertisingConfig(companionAdContainerList, adItem);

        // Setup the player
        PlayerConfig playerConfig = new PlayerConfig();
        playerConfig.setAdvertisingConfig(advertisingConfig);

        String key = "{ANALYTICS_LICENSE_KEY}";
        Player bitmovinPlayer = PlayerFactory.create(this, playerConfig, new AnalyticsConfig(key));
        bitmovinPlayerView.setPlayer(bitmovinPlayer);


        bitmovinPlayer.load(new SourceConfig("https://bitdash-a.akamaihd.net/content/sintel/sintel.mpd", SourceType.Dash));
    }

    @Override
    protected void onStart() {
        bitmovinPlayerView.onStart();
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bitmovinPlayerView.onResume();
    }

    @Override
    protected void onPause() {
        bitmovinPlayerView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        bitmovinPlayerView.onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        bitmovinPlayerView.onDestroy();
        super.onDestroy();
    }

}
