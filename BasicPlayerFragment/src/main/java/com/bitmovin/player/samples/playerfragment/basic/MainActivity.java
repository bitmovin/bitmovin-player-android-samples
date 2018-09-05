package com.bitmovin.player.samples.playerfragment.basic;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.bitmovin.player.BitmovinPlayerFragment;
import com.bitmovin.player.config.media.SourceConfiguration;

public class MainActivity extends AppCompatActivity
{
    private BitmovinPlayerFragment playerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        // Create a new instance of BitmovinPlayerFragment
        this.playerFragment = BitmovinPlayerFragment.newInstance();

        // Begin a new Fragment transaction setting up the newly created fragment.
        // Alternatively the BitmovinPlayerFragment can also be setup via layout xml and then be fetched with FragmentManager.
        this.getFragmentManager().beginTransaction().add(R.id.content_frame, this.playerFragment).commit();

        // Execute pending transactions so that fragment gets added immediately and PlayerView is available
        this.getFragmentManager().executePendingTransactions();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);

        // After fragment got loaded, we have full access to BitmovinPlayer and can initialize it
        this.initializePlayer();
    }

    private void initializePlayer()
    {
        // Create a new source configuration
        SourceConfiguration sourceConfiguration = new SourceConfiguration();

        // Add a new source item
        sourceConfiguration.addSourceItem("https://bitdash-a.akamaihd.net/content/sintel/sintel.mpd");

        // load source using the created source configuration
        this.playerFragment.getPlayer().load(sourceConfiguration);
    }
}
