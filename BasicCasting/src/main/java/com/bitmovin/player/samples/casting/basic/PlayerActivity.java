package com.bitmovin.player.samples.casting.basic;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.bitmovin.player.BitmovinPlayer;
import com.bitmovin.player.BitmovinPlayerView;
import com.bitmovin.player.cast.CastManager;
import com.bitmovin.player.config.PlayerConfiguration;
import com.bitmovin.player.config.media.SourceConfiguration;
import com.bitmovin.player.config.media.SourceItem;

public class PlayerActivity extends AppCompatActivity
{
    public static final String SOURCE_URL = "SOURCE_URL";
    public static final String SOURCE_TITLE = "SOURCE_TITLE";

    private BitmovinPlayerView bitmovinPlayerView;
    private BitmovinPlayer bitmovinPlayer;

    private CastManager castManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        String sourceUrl = getIntent().getStringExtra(SOURCE_URL);
        String sourceTitle = getIntent().getStringExtra(SOURCE_TITLE);
        if (sourceUrl == null || sourceTitle == null)
        {
            finish();
        }

        this.castManager = CastManager.getInstance();
        this.bitmovinPlayerView = (BitmovinPlayerView) this.findViewById(R.id.bitmovinPlayerView);
        this.bitmovinPlayer = this.bitmovinPlayerView.getPlayer();

        this.initializePlayer(sourceUrl, sourceTitle);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        this.bitmovinPlayerView.onResume();
        //The cast manager must know about the ui state, in order to start the notification service
        //Call incrementUiCounter on the cast manager in every onResume of your activities
        this.castManager.incrementUiCounter();
    }

    @Override
    protected void onPause()
    {
        //The cast manager must know about the ui state, in order to start the notification service
        //Call decrementUiCounter on the cast manager in every onPause of your activities
        this.castManager.decrementUiCounter();
        this.bitmovinPlayerView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy()
    {
        this.bitmovinPlayerView.onDestroy();
        super.onDestroy();
    }

    protected void initializePlayer(String sourceUrl, String sourceTitle)
    {
        // Create a new source configuration
        SourceConfiguration sourceConfiguration = new SourceConfiguration();

        // Create a new source item
        SourceItem sourceItem = new SourceItem(sourceUrl);
        sourceItem.setTitle(sourceTitle);

        // Add source item to source configuration
        sourceConfiguration.addSourceItem(sourceItem);

        // load source using the created source configuration
        this.bitmovinPlayer.load(sourceConfiguration);
    }
}
