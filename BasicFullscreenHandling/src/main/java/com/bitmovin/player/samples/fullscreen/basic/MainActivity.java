package com.bitmovin.player.samples.fullscreen.basic;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bitmovin.player.BitmovinPlayer;
import com.bitmovin.player.BitmovinPlayerView;
import com.bitmovin.player.config.media.SourceItem;

public class MainActivity extends AppCompatActivity
{
    private BitmovinPlayerView bitmovinPlayerView;
    private BitmovinPlayer bitmovinPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.bitmovinPlayerView = (BitmovinPlayerView) this.findViewById(R.id.bitmovinPlayerView);
        this.bitmovinPlayer = this.bitmovinPlayerView.getPlayer();

        // Instantiate a custom FullscreenHandler
        CustomFullscreenHandler customFullscreenHandler = new CustomFullscreenHandler(this, this.bitmovinPlayerView, toolbar);
        // Set the FullscreenHandler to the BitmovinPlayerView
        this.bitmovinPlayerView.setFullscreenHandler(customFullscreenHandler);

        this.initializePlayer();
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

    protected void initializePlayer()
    {

        // load source using a created source item
        this.bitmovinPlayer.load(new SourceItem("https://bitdash-a.akamaihd.net/content/sintel/sintel.mpd"));
    }
}
