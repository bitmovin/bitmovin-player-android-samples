package com.bitmovin.player.samples.custom.ui.subtitleview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bitmovin.player.BitmovinPlayer;
import com.bitmovin.player.BitmovinPlayerView;
import com.bitmovin.player.BitmovinSubtitleView;
import com.bitmovin.player.config.PlayerConfiguration;
import com.bitmovin.player.config.StyleConfiguration;
import com.bitmovin.player.config.media.SourceConfiguration;

public class MainActivity extends AppCompatActivity
{
    private BitmovinPlayer bitmovinPlayer;
    private BitmovinPlayerView bitmovinPlayerView;
    private BitmovinSubtitleView bitmovinSubtitleView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(com.bitmovin.player.samples.custom.ui.view.subtitle.R.layout.activity_main);

        // Create new StyleConfiguration
        StyleConfiguration styleConfiguration = new StyleConfiguration();
        // Disable default Bitmovin UI
        styleConfiguration.setUiEnabled(false);

        // Create a new source configuration
        SourceConfiguration sourceConfiguration = new SourceConfiguration();
        // Add a new source item
        sourceConfiguration.addSourceItem("https://bitmovin-a.akamaihd.net/content/sintel/sintel.mpd");

        // Creating a new PlayerConfiguration
        PlayerConfiguration playerConfiguration = new PlayerConfiguration();
        // Assign created StyleConfiguration to the PlayerConfiguration
        playerConfiguration.setStyleConfiguration(styleConfiguration);
        // Assign created SourceConfiguration to the PlayerConfiguration
        playerConfiguration.setSourceConfiguration(sourceConfiguration);

        RelativeLayout playerContainer = this.findViewById(com.bitmovin.player.samples.custom.ui.view.subtitle.R.id.player_container);

        // Creating a BitmovinPlayerView and get it's BitmovinPlayer instance.
        this.bitmovinPlayerView = new BitmovinPlayerView(this, playerConfiguration);
        this.bitmovinPlayerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        this.bitmovinPlayer = this.bitmovinPlayerView.getPlayer();

        // Creating a BitmovinSubtitleView and assign the current player instance.
        this.bitmovinSubtitleView = new BitmovinSubtitleView(this);
        this.bitmovinSubtitleView.setPlayer(this.bitmovinPlayer);

        // Setup minimalistic controls for the player
        PlayerControls playerControls = this.findViewById(com.bitmovin.player.samples.custom.ui.view.subtitle.R.id.player_controls);
        playerControls.setPlayer(this.bitmovinPlayer);

        // Add the BitmovinSubtitleView to the layout
        playerContainer.addView(this.bitmovinSubtitleView);

        // Add the BitmovinPlayerView to the layout as first position (so it is the behind the SubtitleView)
        playerContainer.addView(this.bitmovinPlayerView, 0);
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
