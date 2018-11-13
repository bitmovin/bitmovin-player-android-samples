package com.bitmovin.player.samples.playback.background;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bitmovin.player.BitmovinPlayer;
import com.bitmovin.player.BitmovinPlayerView;
import com.bitmovin.player.config.media.SourceConfiguration;
import com.bitmovin.player.config.media.SourceItem;

public class MainActivity extends AppCompatActivity
{
    private BitmovinPlayerView bitmovinPlayerView;
    private BitmovinPlayer bitmovinPlayer;
    private boolean bound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create a BitmovinPlayerView without a BitmovinPlayer and add it to the View hierarchy
        RelativeLayout rootLayout = this.findViewById(R.id.root);
        this.bitmovinPlayerView = new BitmovinPlayerView(this, (BitmovinPlayer) null);
        this.bitmovinPlayerView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        rootLayout.addView(this.bitmovinPlayerView);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        this.bitmovinPlayerView.onStart();
        // Bind and start the BackgroundPlaybackService
        Intent intent = new Intent(this, BackgroundPlaybackService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        // If the Service is not started, it would get destroyed as soon as the Activity unbinds.
        startService(intent);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        // Attach the BitmovinPlayer to allow the BitmovinPlayerView to control the player
        this.bitmovinPlayerView.setPlayer(this.bitmovinPlayer);
        this.bitmovinPlayerView.onResume();
    }

    @Override
    protected void onPause()
    {
        // Detach the BitmovinPlayer to decouple it from the BitmovinPlayerView lifecycle
        this.bitmovinPlayerView.setPlayer(null);
        this.bitmovinPlayerView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        // Unbind the Service and reset the BitmovinPlayer reference
        unbindService(mConnection);
        this.bitmovinPlayer = null;
        this.bound = false;
        this.bitmovinPlayerView.onStop();
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
        SourceItem sourceItem = new SourceItem("https://bitmovin-a.akamaihd.net/content/MI201109210084_1/mpds/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.mpd");
        sourceItem.setPosterSource("https://bitmovin-a.akamaihd.net/content/poster/hd/RedBull.jpg");
        sourceConfiguration.addSourceItem(sourceItem);

        // load source using the created source configuration
        this.bitmovinPlayer.load(sourceConfiguration);
    }

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service)
        {
            // We've bound to the Service, cast the IBinder and get the BitmovinPlayer instance
            BackgroundPlaybackService.BackgroundBinder binder = (BackgroundPlaybackService.BackgroundBinder) service;
            bitmovinPlayer = binder.getPlayer();
            // Attach the BitmovinPlayer as soon as we have a reference
            bitmovinPlayerView.setPlayer(bitmovinPlayer);
            // If not already initialized, initialize the player with a source.
            if (bitmovinPlayer.getConfig().getSourceItem() == null)
            {
                initializePlayer();
            }
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0)
        {
            bound = false;
        }
    };
}
