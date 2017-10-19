package com.bitmovin.player.samples.vr.basic;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.bitmovin.player.BitmovinPlayer;
import com.bitmovin.player.BitmovinPlayerView;
import com.bitmovin.player.config.media.SourceConfiguration;
import com.bitmovin.player.config.media.SourceItem;
import com.bitmovin.player.config.vr.VRConfiguration;
import com.bitmovin.player.config.vr.VRContentType;

public class MainActivity extends AppCompatActivity
{
    private BitmovinPlayerView bitmovinPlayerView;
    private BitmovinPlayer bitmovinPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.bitmovinPlayerView = (BitmovinPlayerView) this.findViewById(R.id.bitmovinPlayerView);
        this.bitmovinPlayer = this.bitmovinPlayerView.getPlayer();

        this.initializePlayer();
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

    protected void initializePlayer()
    {
        // Create a new source configuration
        SourceConfiguration sourceConfiguration = new SourceConfiguration();

        // Create a new SourceItem
        SourceItem vrSourceItem = new SourceItem("https://bitmovin-a.akamaihd.net/content/playhouse-vr/mpds/105560.mpd");

        // Get the current VRConfiguration of the SourceItem
        VRConfiguration vrConfiguration = vrSourceItem.getVrConfiguration();
        // Set the VrContentType on the VRConfiguration
        vrConfiguration.setVrContentType(VRContentType.SINGLE);
        // Set the start position to 180 degrees
        vrConfiguration.setStartPosition(180);

        // Add a the SourceItem to the SourceConfiguration
        sourceConfiguration.addSourceItem(vrSourceItem);

        // load source using the created source configuration
        this.bitmovinPlayer.load(sourceConfiguration);
    }
}
