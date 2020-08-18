package com.bitmovin.player.samples.ads.companion;

import android.os.Bundle;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.bitmovin.player.BitmovinPlayer;
import com.bitmovin.player.BitmovinPlayerView;
import com.bitmovin.player.config.PlayerConfiguration;
import com.bitmovin.player.config.advertising.AdItem;
import com.bitmovin.player.config.advertising.AdSource;
import com.bitmovin.player.config.advertising.AdSourceType;
import com.bitmovin.player.config.advertising.AdvertisingConfiguration;
import com.bitmovin.player.config.advertising.CompanionAdContainer;
import com.bitmovin.player.config.media.SourceConfiguration;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String AD_TAG = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dlinear&correlator=";

    private BitmovinPlayerView bitmovinPlayerView;
    private BitmovinPlayer bitmovinPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewGroup companionAdContainerView = findViewById(R.id.companionAdContainer);
        this.bitmovinPlayerView = findViewById(R.id.bitmovinPlayerView);
        this.bitmovinPlayer = bitmovinPlayerView.getPlayer();

        // Setup companion ad container
        List<CompanionAdContainer> companionAdContainerList = new ArrayList<>();
        CompanionAdContainer companionAdContainer = new CompanionAdContainer(companionAdContainerView, 300, 250);
        companionAdContainerList.add(companionAdContainer);

        AdItem adItem = new AdItem("pre", new AdSource(AdSourceType.IMA, AD_TAG));

        AdvertisingConfiguration advertisingConfiguration = new AdvertisingConfiguration(companionAdContainerList, adItem);

        // Finish setup of the player
        PlayerConfiguration playerConfiguration = new PlayerConfiguration();
        playerConfiguration.setAdvertisingConfiguration(advertisingConfiguration);
        SourceConfiguration sourceConfiguration = new SourceConfiguration();
        sourceConfiguration.addSourceItem("https://bitdash-a.akamaihd.net/content/sintel/sintel.mpd");
        playerConfiguration.setSourceConfiguration(sourceConfiguration);
        this.bitmovinPlayer.setup(playerConfiguration);
    }

    @Override
    protected void onStart()
    {
        this.bitmovinPlayerView.onStart();
        super.onStart();
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
        this.bitmovinPlayerView.onDestroy();
        super.onDestroy();
    }

}
