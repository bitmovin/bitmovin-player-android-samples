package com.bitmovin.player.samples.custom.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bitmovin.player.BitmovinPlayer;
import com.bitmovin.player.BitmovinPlayerView;
import com.bitmovin.player.api.event.data.PausedEvent;
import com.bitmovin.player.api.event.data.PlayEvent;
import com.bitmovin.player.api.event.data.PlaybackFinishedEvent;
import com.bitmovin.player.api.event.data.SeekedEvent;
import com.bitmovin.player.api.event.data.SourceLoadedEvent;
import com.bitmovin.player.api.event.data.StallEndedEvent;
import com.bitmovin.player.api.event.data.TimeChangedEvent;
import com.bitmovin.player.api.event.listener.OnPausedListener;
import com.bitmovin.player.api.event.listener.OnPlayListener;
import com.bitmovin.player.api.event.listener.OnPlaybackFinishedListener;
import com.bitmovin.player.api.event.listener.OnSeekedListener;
import com.bitmovin.player.api.event.listener.OnSourceLoadedListener;
import com.bitmovin.player.api.event.listener.OnStallEndedListener;
import com.bitmovin.player.api.event.listener.OnTimeChangedListener;
import com.bitmovin.player.config.PlayerConfiguration;
import com.bitmovin.player.ui.FullscreenHandler;

import java.util.Timer;
import java.util.TimerTask;

public class PlayerUI extends RelativeLayout
{
    private static final String LIVE = "LIVE";
    private static final int UI_HIDE_TIME = 5000;

    private BitmovinPlayerView bitmovinPlayerView;
    private BitmovinPlayer player;
    private ImageButton playButton;
    private ImageButton fullscreenButton;
    private SeekBar seekBar;
    private TextView positionView;
    private TextView durationView;

    private Drawable playDrawable;
    private Drawable pauseDrawable;

    private long lastUiInteraction;

    private Timer uiHideTimer;
    private TimerTask uiHideTask;

    private boolean live;
    private View controlView;

    public PlayerUI(Context context, PlayerConfiguration playerConfiguration)
    {
        super(context);
        // Create new BitmovinPlayerView with our PlayerConfiguration
        this.bitmovinPlayerView = new BitmovinPlayerView(context, playerConfiguration);
        this.bitmovinPlayerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        this.player = bitmovinPlayerView.getPlayer();

        setup();
    }

    private void setup()
    {
        LayoutInflater.from(this.getContext()).inflate(R.layout.player_ui, this);

        this.controlView = findViewById(R.id.controls);
        this.playButton = (ImageButton) this.controlView.findViewById(R.id.playback_button);
        this.fullscreenButton = (ImageButton) this.controlView.findViewById(R.id.fullscreen_button);
        this.playDrawable = ContextCompat.getDrawable(this.bitmovinPlayerView.getContext(), R.drawable.ic_play_arrow_black_24dp);
        this.pauseDrawable = ContextCompat.getDrawable(this.bitmovinPlayerView.getContext(), R.drawable.ic_pause_black_24dp);
        this.seekBar = (SeekBar) this.controlView.findViewById(R.id.seekbar);
        this.positionView = (TextView) this.controlView.findViewById(R.id.position);
        this.durationView = (TextView) this.controlView.findViewById(R.id.duration);

        this.seekBar.setOnSeekBarChangeListener(this.seekBarChangeListener);
        this.playButton.setOnClickListener(this.onClickListener);
        this.fullscreenButton.setOnClickListener(this.onClickListener);
        this.playButton.setOnTouchListener(this.onTouchListener);
        this.seekBar.setOnTouchListener(this.onTouchListener);
        setOnTouchListener(this.onTouchListener);

        // Add BitmovinPlayerView to the layout
        this.addView(this.bitmovinPlayerView, 0);

        uiHideTimer = new Timer();

        addPlayerListener();
        updateUi();
    }

    private void addPlayerListener()
    {
        this.player.addEventListener(onTimeChangedListener);
        this.player.addEventListener(onSourceLoadedListener);
        this.player.addEventListener(onPlayListener);
        this.player.addEventListener(onPausedListener);
        this.player.addEventListener(onStallEndedListener);
        this.player.addEventListener(onSeekedListener);
        this.player.addEventListener(onPlaybackFinishedListener);
    }

    private void removePlayerListener()
    {
        this.player.removeEventListener(onTimeChangedListener);
        this.player.removeEventListener(onSourceLoadedListener);
        this.player.removeEventListener(onPlayListener);
        this.player.removeEventListener(onPausedListener);
        this.player.removeEventListener(onStallEndedListener);
        this.player.removeEventListener(onSeekedListener);
        this.player.removeEventListener(onPlaybackFinishedListener);
    }

    private void startUiHiderTask()
    {
        stopUiHiderTask();

        // Create Task which hides the UI after a specified time (UI_HIDE_TIME)
        this.uiHideTask = new TimerTask()
        {
            @Override
            public void run()
            {
                long timeSincelastUiInteraction = System.currentTimeMillis() - lastUiInteraction;
                if (timeSincelastUiInteraction > UI_HIDE_TIME)
                {
                    setControlsVisible(false);
                }
            }
        };
        // Schedule the hider task, so it checks the state every 100ms
        this.uiHideTimer.scheduleAtFixedRate(this.uiHideTask, 0, 100);
    }

    private void stopUiHiderTask()
    {
        if (this.uiHideTask != null)
        {
            this.uiHideTask.cancel();
            this.uiHideTask = null;
        }
    }

    public void setVisible(boolean visible)
    {
        lastUiInteraction = System.currentTimeMillis();
        setControlsVisible(visible);
    }

    private void setControlsVisible(final boolean visible)
    {
        post(new Runnable()
        {
            @Override
            public void run()
            {
                if (visible)
                {
                    startUiHiderTask();
                }
                else
                {
                    stopUiHiderTask();
                }

                int visibility = visible ? View.VISIBLE : View.INVISIBLE;
                controlView.setVisibility(visibility);
            }
        });
    }

    public void setFullscreenHandler(FullscreenHandler fullscreenHandler)
    {
        this.bitmovinPlayerView.setFullscreenHandler(fullscreenHandler);
    }

    public void destroy()
    {
        removePlayerListener();
        uiHideTimer.cancel();
    }

    public void onResume()
    {
        this.bitmovinPlayerView.onResume();
    }

    public void onPause()
    {
        this.bitmovinPlayerView.onPause();
    }

    public void onDestroy()
    {
        this.bitmovinPlayerView.onDestroy();
        destroy();
    }

    /**
     * UI Listeners
     */

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return true;
    }

    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener()
    {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
        {
            // Only seek/timeShift when the user changes the progress (and not the TimeChangedEvent)
            if (fromUser)
            {
                // If the current stream is a live stream, we have to use the timeShift method
                if (!player.isLive())
                {
                    player.seek(progress / 1000d);
                }
                else
                {
                    player.timeShift((progress - seekBar.getMax()) / 1000d);
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar)
        {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar)
        {
        }
    };

    private OnClickListener onClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (v == playButton || v == PlayerUI.this)
            {
                if (player.isPlaying())
                {
                    player.pause();
                }
                else
                {
                    player.play();
                }
            }
            if (v == fullscreenButton)
            {
                if (bitmovinPlayerView.isFullscreen())
                {
                    bitmovinPlayerView.exitFullscreen();
                }
                else
                {
                    bitmovinPlayerView.enterFullscreen();
                }
            }
        }
    };

    private OnTouchListener onTouchListener = new OnTouchListener()
    {
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            lastUiInteraction = System.currentTimeMillis();

            if (event.getAction() == MotionEvent.ACTION_UP)
            {
                // Start the hider task, when the UI is not touched
                startUiHiderTask();
            }
            else
            {
                // When the view is touched, the UI should be visible
                setControlsVisible(true);
            }
            return false;
        }
    };

    /**
     * Player Listeners
     */

    private OnTimeChangedListener onTimeChangedListener = new OnTimeChangedListener()
    {
        @Override
        public void onTimeChanged(TimeChangedEvent timeChangedEvent)
        {
            updateUi();
        }
    };

    private OnSourceLoadedListener onSourceLoadedListener = new OnSourceLoadedListener()
    {
        @Override
        public void onSourceLoaded(SourceLoadedEvent sourceLoadedEvent)
        {
            updateUi();
        }
    };

    private OnPlaybackFinishedListener onPlaybackFinishedListener = new OnPlaybackFinishedListener()
    {
        @Override
        public void onPlaybackFinished(PlaybackFinishedEvent playbackFinishedEvent)
        {
            updateUi();
        }
    };

    private OnPausedListener onPausedListener = new OnPausedListener()
    {
        @Override
        public void onPaused(PausedEvent pausedEvent)
        {
            updateUi();
        }
    };

    private OnPlayListener onPlayListener = new OnPlayListener()
    {
        @Override
        public void onPlay(PlayEvent playEvent)
        {
            updateUi();
        }
    };

    private OnSeekedListener onSeekedListener = new OnSeekedListener()
    {
        @Override
        public void onSeeked(SeekedEvent seekedEvent)
        {
            updateUi();
        }
    };

    private OnStallEndedListener onStallEndedListener = new OnStallEndedListener()
    {
        @Override
        public void onStallEnded(StallEndedEvent stallEndedEvent)
        {
            updateUi();
        }
    };

    /**
     * Methods for UI update
     */

    private void updateUi()
    {
        this.seekBar.post(new Runnable()
        {
            @Override
            public void run()
            {
                int positionMs;
                int durationMs;

                // if the live state of the player changed, the UI should change it's mode
                if (live != player.isLive())
                {
                    live = player.isLive();
                    if (live)
                    {
                        positionView.setVisibility(GONE);
                        durationView.setText(LIVE);
                    }
                    else
                    {
                        positionView.setVisibility(VISIBLE);
                    }
                }

                if (live)
                {
                    // The Seekbar does not support negative values
                    // so the seekable range is shifted to the positive
                    durationMs = (int) (-player.getMaxTimeShift() * 1000);
                    positionMs = (int) (durationMs + player.getTimeShift() * 1000);
                }
                else
                {
                    // Converting to milliseconds
                    positionMs = (int) (player.getCurrentTime() * 1000);
                    durationMs = (int) (player.getDuration() * 1000);

                    // Update the TextViews displaying the current position and duration
                    positionView.setText(millisecondsToTimeString(positionMs));
                    durationView.setText(millisecondsToTimeString(durationMs));
                }

                // Update the values of the Seekbar
                seekBar.setProgress(positionMs);
                seekBar.setMax(durationMs);

                // Update the image of the playback button
                if (player.isPlaying())
                {
                    playButton.setImageDrawable(pauseDrawable);
                }
                else
                {
                    playButton.setImageDrawable(playDrawable);
                }
            }
        });
    }

    private String millisecondsToTimeString(int milliseconds)
    {
        int second = (milliseconds / 1000) % 60;
        int minute = (milliseconds / (1000 * 60)) % 60;
        int hour = (milliseconds / (1000 * 60 * 60)) % 24;

        if (hour > 0)
        {
            return String.format("%02d:%02d:%02d", hour, minute, second);
        }
        else
        {
            return String.format("%02d:%02d", minute, second);
        }
    }
}
