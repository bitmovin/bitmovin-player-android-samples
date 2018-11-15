package com.bitmovin.samples.tv.playback.basic;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;

import com.bitmovin.player.BitmovinPlayer;
import com.bitmovin.player.BitmovinPlayerView;
import com.bitmovin.player.api.event.listener.OnErrorListener;
import com.bitmovin.player.config.PlaybackConfiguration;
import com.bitmovin.player.config.PlayerConfiguration;
import com.bitmovin.player.config.StyleConfiguration;
import com.bitmovin.player.config.media.DASHSource;
import com.bitmovin.player.config.media.SourceConfiguration;
import com.bitmovin.player.config.media.SourceItem;
import com.bitmovin.player.samples.tv.playback.basic.R;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int SEEKING_OFFSET = 10;

    private BitmovinPlayerView bitmovinPlayerView;
    private BitmovinPlayer bitmovinPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // Switch from splash screen to main theme when we are done loading
        this.setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_main);

        this.initializePlayer();
    }

    private void initializePlayer()
    {
        // Initialize BitmovinPlayerView from layout
        this.bitmovinPlayerView = this.findViewById(R.id.bitmovin_player_view);
        // Fetch BitmovinPlayer from BitmovinPlayerView
        this.bitmovinPlayer = this.bitmovinPlayerView.getPlayer();

        this.bitmovinPlayer.setup(this.createPlayerConfiguration());
    }

    private PlayerConfiguration createPlayerConfiguration()
    {
        // Create a new SourceItem. In this case we are loading a DASH source.
        String sourceURL = "https://bitmovin-a.akamaihd.net/content/MI201109210084_1/mpds/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.mpd";
        SourceItem sourceItem = new SourceItem(new DASHSource(sourceURL));

        // Creating a new PlayerConfiguration
        PlayerConfiguration playerConfiguration = new PlayerConfiguration();

        // Assign created SourceConfiguration to the PlayerConfiguration
        SourceConfiguration sourceConfiguration = new SourceConfiguration();
        sourceConfiguration.addSourceItem(sourceItem);
        playerConfiguration.setSourceConfiguration(sourceConfiguration);

        // Here a custom bitmovinplayer-ui.js is loaded which utilizes the Cast-UI as this matches our needs here perfectly.
        // I.e. UI controls get shown / hidden whenever the Player API is called. This is needed due to the fact that on Android TV no touch events are received
        StyleConfiguration styleConfiguration = new StyleConfiguration();
        styleConfiguration.setPlayerUiJs("file:///android_asset/bitmovinplayer-ui.js");
        playerConfiguration.setStyleConfiguration(styleConfiguration);

        PlaybackConfiguration playbackConfiguration = new PlaybackConfiguration();
        playbackConfiguration.setAutoplayEnabled(true);
        playerConfiguration.setPlaybackConfiguration(playbackConfiguration);

        return playerConfiguration;
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        this.bitmovinPlayerView.onResume();
        this.addEventListener();
        this.bitmovinPlayer.play();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        this.bitmovinPlayerView.onStart();
    }

    @Override
    protected void onPause()
    {
        this.removeEventListener();
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

    @Override
    public boolean dispatchKeyEvent(KeyEvent event)
    {
        // This method is called on key down and key up, so avoid being called twice
        if (this.bitmovinPlayerView != null && event.getAction() == KeyEvent.ACTION_DOWN)
        {
            if (this.handleUserInput(event.getKeyCode()))
            {
                return true;
            }
        }

        // Make sure to return super.dispatchKeyEvent(event) so that any key not handled yet will work as expected
        return super.dispatchKeyEvent(event);
    }

    // region Private

    private boolean handleUserInput(int keycode)
    {
        Log.d(TAG, "Keycode " + keycode);
        switch (keycode)
        {
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_NUMPAD_ENTER:
            case KeyEvent.KEYCODE_SPACE:
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                this.togglePlay();
                return true;
            case KeyEvent.KEYCODE_MEDIA_PLAY:
                this.bitmovinPlayer.play();
                return true;
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
                this.bitmovinPlayer.pause();
                return true;
            case KeyEvent.KEYCODE_MEDIA_STOP:
                this.stopPlayback();
                return true;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
                this.seekForward();
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_MEDIA_REWIND:
                this.seekBackward();
                break;
            default:
        }

        return false;
    }

    private void togglePlay()
    {
        if (this.bitmovinPlayer.isPlaying())
        {
            this.bitmovinPlayer.pause();
        }
        else
        {
            this.bitmovinPlayer.play();
        }
    }

    private void stopPlayback()
    {
        this.bitmovinPlayer.pause();
        this.bitmovinPlayer.seek(0);
    }

    private void seekForward()
    {
        double currentTime = this.bitmovinPlayer.getCurrentTime();
        this.bitmovinPlayer.seek(currentTime + SEEKING_OFFSET);
    }

    private void seekBackward()
    {
        double currentTime = this.bitmovinPlayer.getCurrentTime();
        this.bitmovinPlayer.seek(currentTime - SEEKING_OFFSET);
    }

    // endregion

    // region Listener

    private void addEventListener()
    {
        if (this.bitmovinPlayer == null)
        {
            return;
        }

        this.bitmovinPlayer.addEventListener(this.onErrorListener);
    }

    private void removeEventListener()
    {
        if (this.bitmovinPlayer == null)
        {
            return;
        }

        this.bitmovinPlayer.removeEventListener(this.onErrorListener);
    }

    private OnErrorListener onErrorListener = errorEvent -> Log.e(TAG, "An Error occurred (" + errorEvent.getCode() + "): " + errorEvent.getMessage());

    // endregion
}
