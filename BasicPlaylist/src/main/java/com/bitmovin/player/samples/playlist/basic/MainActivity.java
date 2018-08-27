package com.bitmovin.player.samples.playlist.basic;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.bitmovin.player.BitmovinPlayer;
import com.bitmovin.player.BitmovinPlayerView;
import com.bitmovin.player.api.event.data.PlayEvent;
import com.bitmovin.player.api.event.data.PlaybackFinishedEvent;
import com.bitmovin.player.api.event.data.ReadyEvent;
import com.bitmovin.player.api.event.listener.OnPlayListener;
import com.bitmovin.player.api.event.listener.OnPlaybackFinishedListener;
import com.bitmovin.player.api.event.listener.OnReadyListener;
import com.bitmovin.player.config.media.SourceConfiguration;
import com.bitmovin.player.config.media.SourceItem;

public class MainActivity extends AppCompatActivity
{
    private BitmovinPlayerView bitmovinPlayerView;
    private BitmovinPlayer bitmovinPlayer;

    // Holds all items of the playlist
    private PlaylistItem[] playlistItems;
    // Stores the index of the next playlist item to be played
    private int nextPlaylistItem = 0;
    private boolean lastItemFinished = false;
    private boolean playlistStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.bitmovinPlayerView = (BitmovinPlayerView) this.findViewById(R.id.bitmovinPlayerView);
        this.bitmovinPlayer = this.bitmovinPlayerView.getPlayer();
        this.playlistItems = new PlaylistItem[]{
                new PlaylistItem("Art of Motion", "https://bitmovin-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8"),
                new PlaylistItem("Sintel", "https://bitmovin-a.akamaihd.net/content/sintel/hls/playlist.m3u8")
        };

        this.addListenersToPlayer();

        // Start the playlist
        this.playNextItem();
    }

    /**
     * Plays the next playlist item in the playlist.
     */
    private void playNextItem()
    {
        // check if there are unplayed items in the playlist
        if (nextPlaylistItem >= this.playlistItems.length)
        {
            return;
        }
        // fetch the next item to play from the playlist
        PlaylistItem itemToPlay = this.playlistItems[nextPlaylistItem];
        nextPlaylistItem += 1;

        // Create a source item based on the playlist item and load it
        SourceItem sourceItem = new SourceItem(itemToPlay.getUrl());
        sourceItem.setTitle(itemToPlay.getTitle());

        // Create a source configuration and add the sourceItem
        SourceConfiguration sourceConfiguration = new SourceConfiguration();
        sourceConfiguration.addSourceItem(sourceItem);

        // load the new source configuration
        this.bitmovinPlayer.load(sourceConfiguration);
    }

    private OnPlayListener onPlayListener = new OnPlayListener()
    {
        @Override
        public void onPlay(PlayEvent playEvent)
        {
            // Remember that the playlist was started by the user
            playlistStarted = true;

            // When the replay button in the UI was tapped or a player.play() API call was issued after the last
            // playlist item has finished, we repeat the whole playlist instead of just repeating the last item
            if (lastItemFinished)
            {
                // Unload the last played item and reset the playlist state
                bitmovinPlayer.unload();
                lastItemFinished = false;
                nextPlaylistItem = 0;
                // Restart playlist with first item in list
                playNextItem();
            }
        }
    };

    private OnReadyListener onReadyListener = new OnReadyListener()
    {
        @Override
        public void onReady(ReadyEvent readyEvent)
        {
            // Autoplay all playlist items after the initial playlist item was started by either tapping the
            // play button or by issuing the player.play() API call.
            if (playlistStarted)
            {
                bitmovinPlayer.play();
            }
        }
    };

    private OnPlaybackFinishedListener onPlaybackFinishedListener = new OnPlaybackFinishedListener()
    {
        @Override
        public void onPlaybackFinished(PlaybackFinishedEvent playbackFinishedEvent)
        {
            // Automatically play next item in the playlist if there are still unplayed items left
            lastItemFinished = nextPlaylistItem >= playlistItems.length;
            if (!lastItemFinished)
            {
                playNextItem();
            }
        }
    };

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

    protected void addListenersToPlayer()
    {
        this.bitmovinPlayer.addEventListener(this.onReadyListener);
        this.bitmovinPlayer.addEventListener(this.onPlayListener);
        this.bitmovinPlayer.addEventListener(this.onPlaybackFinishedListener);
    }

    // A simple calls defining a playlist item
    private class PlaylistItem
    {
        private String title;
        private String url;

        public PlaylistItem(String title, String url)
        {
            this.title = title;
            this.url = url;
        }

        public String getTitle()
        {
            return title;
        }

        public String getUrl()
        {
            return url;
        }
    }
}
