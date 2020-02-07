package com.bitmovin.player.samples.pip.basic;

import android.content.res.Configuration;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.bitmovin.player.BitmovinPlayer;
import com.bitmovin.player.BitmovinPlayerView;
import com.bitmovin.player.api.event.data.PictureInPictureEnterEvent;
import com.bitmovin.player.api.event.listener.OnPictureInPictureEnterListener;
import com.bitmovin.player.config.media.SourceConfiguration;
import com.bitmovin.player.ui.DefaultPictureInPictureHandler;

public class MainActivity extends AppCompatActivity
{
    private BitmovinPlayerView bitmovinPlayerView;
    private BitmovinPlayer bitmovinPlayer;
    private boolean playerShouldPause = true;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.bitmovinPlayerView = (BitmovinPlayerView) this.findViewById(R.id.bitmovinPlayerView);
        this.bitmovinPlayer = this.bitmovinPlayerView.getPlayer();

        // Create a PictureInPictureHandler and set it on the BitmovinPlayerView
        DefaultPictureInPictureHandler pictureInPictureHandler = new DefaultPictureInPictureHandler(this, this.bitmovinPlayer);
        this.bitmovinPlayerView.setPictureInPictureHandler(pictureInPictureHandler);

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

        // Add the PictureInPictureEnterListener to the BitmovinPlayerView
        this.bitmovinPlayerView.addEventListener(this.pipEnterListener);

        this.bitmovinPlayerView.onResume();
    }

    @Override
    protected void onPause()
    {
        if (this.playerShouldPause)
        {
            this.bitmovinPlayerView.onPause();
        }
        this.playerShouldPause = true;

        this.bitmovinPlayerView.removeEventListener(this.pipEnterListener);

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

        // Add a new source item
        sourceConfiguration.addSourceItem("https://bitdash-a.akamaihd.net/content/sintel/sintel.mpd");

        // load source using the created source configuration
        this.bitmovinPlayer.load(sourceConfiguration);
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig)
    {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);

        // Hiding the ActionBar
        if (isInPictureInPictureMode)
        {
            this.getSupportActionBar().hide();
        }
        else
        {
            this.getSupportActionBar().show();
        }
        this.bitmovinPlayerView.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
    }

    private OnPictureInPictureEnterListener pipEnterListener = new OnPictureInPictureEnterListener()
    {
        @Override
        public void onPictureInPictureEnter(PictureInPictureEnterEvent pictureInPictureEnterEvent)
        {
            // Android fires an onPause on the Activity when entering PiP mode.
            // However, we do not want the BitmovinPlayerView to act on this.
            MainActivity.this.playerShouldPause = false;
        }
    };
}
