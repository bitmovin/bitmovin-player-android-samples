package com.bitmovin.player.samples.custom.ui.subtitleview;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bitmovin.player.api.Player;
import com.bitmovin.player.api.event.Event;
import com.bitmovin.player.api.event.EventListener;
import com.bitmovin.player.api.event.PlayerEvent;
import com.bitmovin.player.api.event.SourceEvent;
import com.bitmovin.player.api.media.subtitle.SubtitleTrack;

import java.util.List;

public class PlayerControls extends LinearLayout {
    private static final String LIVE = "LIVE";

    private Player player;
    private ImageButton playButton;
    private ImageButton subtitleButton;
    private SeekBar seekBar;
    private TextView positionView;
    private TextView durationView;

    private Drawable playDrawable;
    private Drawable pauseDrawable;

    private boolean live;
    private View controlView;

    public PlayerControls(Context context) {
        super(context, null);
    }

    public PlayerControls(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(getContext()).inflate(R.layout.player_controls, this);

        controlView = findViewById(R.id.controls);
        playButton = findViewById(R.id.playback_button);
        subtitleButton = findViewById(R.id.subtitle_button);
        playDrawable = ContextCompat.getDrawable(context, R.drawable.ic_play_arrow_black_24dp);
        pauseDrawable = ContextCompat.getDrawable(context, R.drawable.ic_pause_black_24dp);
        seekBar = findViewById(R.id.seekbar);
        positionView = findViewById(R.id.position);
        durationView = findViewById(R.id.duration);

        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        playButton.setOnClickListener(onClickListener);
        subtitleButton.setOnClickListener(onClickListener);
    }

    public void setPlayer(Player bitmovinPlayer) {
        if (player != null) {
            removePlayerListener();
        }
        player = bitmovinPlayer;
        if (player != null) {
            addPlayerListener();
        }
    }

    private void addPlayerListener() {
        player.on(PlayerEvent.TimeChanged.class, this::updateUi);
        player.on(SourceEvent.Loaded.class, this::updateUi);
        player.on(PlayerEvent.Play.class, this::updateUi);
        player.on(PlayerEvent.Paused.class, this::updateUi);
        player.on(PlayerEvent.StallEnded.class, this::updateUi);
        player.on(PlayerEvent.Seeked.class, this::updateUi);
        player.on(PlayerEvent.PlaybackFinished.class, this::updateUi);
        player.on(PlayerEvent.Ready.class, this::updateUi);
    }

    private void removePlayerListener() {
        player.off(this::updateUi);
    }

    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // Only seek/timeShift when the user changes the progress (and not the TimeChangedEvent)
            if (fromUser) {
                // If the current stream is a live stream, we have to use the timeShift method
                if (!player.isLive()) {
                    player.seek(progress / 1000d);
                } else {
                    player.timeShift((progress - seekBar.getMax()) / 1000d);
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == playButton) {
                if (player.isPlaying()) {
                    player.pause();
                } else {
                    player.play();
                }
            } else if (v == subtitleButton) {
                onSubtitleDialogButton();
            }
        }
    };

    private void onSubtitleDialogButton() {
        final List<SubtitleTrack> subtitleTracks = player.getAvailableSubtitles();
        final String[] subtitleNames = new String[subtitleTracks.size()];
        for (int i = 0; i < subtitleNames.length; i++) {
            subtitleNames[i] = subtitleTracks.get(i).getLabel();
        }
        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, subtitleNames);

        new AlertDialog.Builder(getContext()).setAdapter(listAdapter, (dialog, which) -> {
            PlayerControls.this.player.setSubtitle(subtitleTracks.get(which).getId());
            dialog.dismiss();
        }).show();
    }

    /**
     * Methods for UI update
     */

    private void updateUi(Event event) {
        seekBar.post(() -> {
            int positionMs;
            int durationMs;

            // if the live state of the player changed, the UI should change it's mode
            if (live != player.isLive()) {
                live = player.isLive();
                if (live) {
                    positionView.setVisibility(GONE);
                    durationView.setText(LIVE);
                } else {
                    positionView.setVisibility(VISIBLE);
                }
            }

            if (live) {
                // The Seekbar does not support negative values
                // so the seekable range is shifted to the positive
                durationMs = (int) (-player.getMaxTimeShift() * 1000);
                positionMs = (int) (durationMs + player.getTimeShift() * 1000);
            } else {
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
            if (player.isPlaying()) {
                playButton.setImageDrawable(pauseDrawable);
            } else {
                playButton.setImageDrawable(playDrawable);
            }
        });
    }

    private String millisecondsToTimeString(int milliseconds) {
        int second = (milliseconds / 1000) % 60;
        int minute = (milliseconds / (1000 * 60)) % 60;
        int hour = (milliseconds / (1000 * 60 * 60)) % 24;

        if (hour > 0) {
            return String.format("%02d:%02d:%02d", hour, minute, second);
        } else {
            return String.format("%02d:%02d", minute, second);
        }
    }
}
