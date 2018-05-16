package com.bitmovin.player.samples.custom.ui.subtitleview;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bitmovin.player.BitmovinPlayer;
import com.bitmovin.player.api.event.data.PausedEvent;
import com.bitmovin.player.api.event.data.PlayEvent;
import com.bitmovin.player.api.event.data.PlaybackFinishedEvent;
import com.bitmovin.player.api.event.data.ReadyEvent;
import com.bitmovin.player.api.event.data.SeekedEvent;
import com.bitmovin.player.api.event.data.SourceLoadedEvent;
import com.bitmovin.player.api.event.data.StallEndedEvent;
import com.bitmovin.player.api.event.data.TimeChangedEvent;
import com.bitmovin.player.api.event.listener.OnPausedListener;
import com.bitmovin.player.api.event.listener.OnPlayListener;
import com.bitmovin.player.api.event.listener.OnPlaybackFinishedListener;
import com.bitmovin.player.api.event.listener.OnReadyListener;
import com.bitmovin.player.api.event.listener.OnSeekedListener;
import com.bitmovin.player.api.event.listener.OnSourceLoadedListener;
import com.bitmovin.player.api.event.listener.OnStallEndedListener;
import com.bitmovin.player.api.event.listener.OnTimeChangedListener;
import com.bitmovin.player.config.track.SubtitleTrack;


public class PlayerControls extends LinearLayout
{
    private static final String LIVE = "LIVE";

    private BitmovinPlayer player;
    private ImageButton playButton;
    private ImageButton subtitleButton;
    private SeekBar seekBar;
    private TextView positionView;
    private TextView durationView;

    private Drawable playDrawable;
    private Drawable pauseDrawable;

    private boolean live;
    private View controlView;

    public PlayerControls(Context context)
    {
        super(context, null);
    }

    public PlayerControls(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);

        LayoutInflater.from(this.getContext()).inflate(com.bitmovin.player.samples.custom.ui.view.subtitle.R.layout.player_controls, this);

        this.controlView = findViewById(com.bitmovin.player.samples.custom.ui.view.subtitle.R.id.controls);
        this.playButton = (ImageButton) this.findViewById(com.bitmovin.player.samples.custom.ui.view.subtitle.R.id.playback_button);
        this.subtitleButton = (ImageButton) this.findViewById(com.bitmovin.player.samples.custom.ui.view.subtitle.R.id.subtitle_button);
        this.playDrawable = ContextCompat.getDrawable(context, com.bitmovin.player.samples.custom.ui.view.subtitle.R.drawable.ic_play_arrow_black_24dp);
        this.pauseDrawable = ContextCompat.getDrawable(context, com.bitmovin.player.samples.custom.ui.view.subtitle.R.drawable.ic_pause_black_24dp);
        this.seekBar = (SeekBar) this.findViewById(com.bitmovin.player.samples.custom.ui.view.subtitle.R.id.seekbar);
        this.positionView = (TextView) this.findViewById(com.bitmovin.player.samples.custom.ui.view.subtitle.R.id.position);
        this.durationView = (TextView) this.findViewById(com.bitmovin.player.samples.custom.ui.view.subtitle.R.id.duration);

        this.seekBar.setOnSeekBarChangeListener(this.seekBarChangeListener);
        this.playButton.setOnClickListener(this.onClickListener);
        this.subtitleButton.setOnClickListener(this.onClickListener);
    }

    public void setPlayer(BitmovinPlayer bitmovinPlayer)
    {
        if (this.player != null)
        {
            removePlayerListener();
        }
        this.player = bitmovinPlayer;
        if (this.player != null)
        {
            addPlayerListener();
        }
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
        this.player.addEventListener(onReadyListener);
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
        this.player.removeEventListener(onReadyListener);
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

    private View.OnClickListener onClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (v == playButton)
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
            else if (v == subtitleButton)
            {
                onSubtitleDialogButton();
            }
        }
    };

    private void onSubtitleDialogButton()
    {
        final SubtitleTrack[] subtitleTracks = this.player.getAvailableSubtitles();
        final String[] subtitleNames = new String[subtitleTracks.length];
        for (int i = 0; i < subtitleNames.length; i++)
        {
            subtitleNames[i] = subtitleTracks[i].getLabel();
        }
        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_list_item_1, subtitleNames);

        new AlertDialog.Builder(this.getContext()).setAdapter(listAdapter, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                PlayerControls.this.player.setSubtitle(subtitleTracks[which].getId());
                dialog.dismiss();
            }
        }).show();
    }

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

    private OnReadyListener onReadyListener = new OnReadyListener()
    {
        @Override
        public void onReady(ReadyEvent readyEvent)
        {
            updateUi();
            ;
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
