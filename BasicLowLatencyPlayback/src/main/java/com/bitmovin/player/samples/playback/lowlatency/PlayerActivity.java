/*
 * Bitmovin Player Android SDK
 * Copyright (C) 2019, Bitmovin GmbH, All Rights Reserved
 *
 * This source code and its use and distribution, is subject to the terms
 * and conditions of the applicable license agreement.
 */

package com.bitmovin.player.samples.playback.lowlatency;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.SeekBar;

import com.bitmovin.player.BitmovinPlayer;
import com.bitmovin.player.BitmovinPlayerView;
import com.bitmovin.player.api.event.data.ErrorEvent;
import com.bitmovin.player.api.event.listener.OnErrorListener;
import com.bitmovin.player.config.PlayerConfiguration;
import com.bitmovin.player.config.drm.WidevineConfiguration;
import com.bitmovin.player.config.live.LiveConfiguration;
import com.bitmovin.player.config.live.LiveSynchronizationMethod;
import com.bitmovin.player.config.live.LowLatencyConfiguration;
import com.bitmovin.player.config.live.LowLatencySynchronizationConfiguration;
import com.bitmovin.player.config.media.DASHSource;
import com.bitmovin.player.config.media.SourceItem;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class PlayerActivity extends AppCompatActivity
{
    private static final String TAG = PlayerActivity.class.getSimpleName();

    public static final String STREAM = "STREAM";
    public static final String DRM = "DRM";

    private BitmovinPlayerView bitmovinPlayerView;
    private BitmovinPlayer bitmovinPlayer;

    public static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
    public static final DecimalFormat decimalFormat = new DecimalFormat("0.0");
    public static final int MAX_LATENCY_CENTIES = 50;
    public static final int MIN_LATENCY_CENTIES = 5;

    public static final double START_LATENCY = 0.5;

    public TextView clock;
    public SeekBar seekLatency;
    public TextView textTargetLatency;
    public TextView textLatency;
    public TextView textBuffer;

    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        Intent intent = this.getIntent();

        String stream = intent.getStringExtra(STREAM);
        if (stream == null)
        {
            finish();
            return;
        }
        String drm = intent.getStringExtra(DRM);

        this.calendar = Calendar.getInstance();
        this.clock = findViewById(R.id.clock);
        this.seekLatency = findViewById(R.id.seek_latency);
        this.textTargetLatency = findViewById(R.id.text_target_latency);
        this.textLatency = findViewById(R.id.txt_latency);
        this.textBuffer = findViewById(R.id.txt_buffer);

        this.seekLatency.setMax(MAX_LATENCY_CENTIES - MIN_LATENCY_CENTIES);
        this.seekLatency.setProgress((int)(START_LATENCY * 10.0));
        this.textTargetLatency.setText(decimalFormat.format(START_LATENCY));
        this.seekLatency.setOnSeekBarChangeListener(this.onSeekBarChangeListener);

        LinearLayout playerContainer = findViewById(R.id.playerContainer);

        // Creating a new PlayerConfiguration
        PlayerConfiguration playerConfiguration = new PlayerConfiguration();

        SourceItem sourceItem = new SourceItem(new DASHSource(stream));
        if (drm != null)
        {
            sourceItem.addDRMConfiguration(new WidevineConfiguration(drm));
        }
        playerConfiguration.getSourceConfiguration().addSourceItem(sourceItem);

        playerConfiguration.getPlaybackConfiguration().setAutoplayEnabled(true);

        LiveConfiguration liveConfiguration = new LiveConfiguration();
        liveConfiguration.addSynchronizationEntry("time.akamai.com", LiveSynchronizationMethod.NTP);
        LowLatencyConfiguration lowLatencyConfiguration = new LowLatencyConfiguration(START_LATENCY);
        lowLatencyConfiguration.setCatchupConfiguration(new LowLatencySynchronizationConfiguration(0.075, 5, 1.2f));
        lowLatencyConfiguration.setFallbackConfiguration(new LowLatencySynchronizationConfiguration(0.075, 5, 0.95f));
        liveConfiguration.setLowLatencyConfiguration(lowLatencyConfiguration);

        playerConfiguration.setLiveConfiguration(liveConfiguration);

        this.bitmovinPlayerView = new BitmovinPlayerView(this, playerConfiguration);
        this.bitmovinPlayerView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        playerContainer.addView(bitmovinPlayerView);

        this.bitmovinPlayer = bitmovinPlayerView.getPlayer();
        this.bitmovinPlayer.addEventListener(this.onErrorListener);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                ticker.run();
                statsTicker.run();
            }
        }, 1000);
        this.seekLatency.requestFocus();
    }

    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener()
    {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
        {

            double latency = (progress + MIN_LATENCY_CENTIES) / 10.0;
            Log.e(TAG, "A new TargetLatency is set (" + progress + "): " + latency);
            bitmovinPlayer.setTargetLatency(latency);
            textTargetLatency.setText(decimalFormat.format(latency));
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

    private final Runnable statsTicker = new Runnable()
    {
        public void run()
        {
            updateStats();

            Handler handler = textBuffer.getHandler();
            if (handler == null)
            {
                return;
            }
            handler.postDelayed(statsTicker, 100);
        }
    };

    private void updateStats()
    {
        if (textLatency != null)
        {
            textLatency.setText(decimalFormat.format(bitmovinPlayer.getLatency()));
        }
        if (textBuffer != null)
        {
            double buffer = Math.min(bitmovinPlayer.getVideoBufferLength(), bitmovinPlayer.getAudioBufferLength());
            textBuffer.setText(decimalFormat.format(buffer));
        }
    }

    // region system time

    private final Runnable ticker = new Runnable()
    {
        public void run()
        {
            if (clock == null)
            {
                return;
            }
            onTimeChanged();

            long now = SystemClock.uptimeMillis();
            long next = now + (10 - now % 10);

            Handler handler = clock.getHandler();
            if (handler == null)
            {
                return;
            }
            handler.postAtTime(ticker, next);
        }
    };

    private void onTimeChanged()
    {
        this.calendar.setTimeInMillis(System.currentTimeMillis());
        this.clock.setText(simpleDateFormat.format(this.calendar.getTime()));
    }

    // endregion

    // region listeners

    private OnErrorListener onErrorListener = new OnErrorListener()
    {
        @Override
        public void onError(ErrorEvent event)
        {
            Log.e(TAG, "An error occurred (" + event.getCode() + "): " + event.getMessage());
        }
    };

    // endregion

    @Override
    protected void onStart()
    {
        super.onStart();
        this.bitmovinPlayerView.onStart();
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
}
