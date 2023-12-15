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
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bitmovin.analytics.api.AnalyticsConfig;
import com.bitmovin.player.PlayerView;
import com.bitmovin.player.api.Player;
import com.bitmovin.player.api.PlayerConfig;
import com.bitmovin.player.api.analytics.PlayerFactory;
import com.bitmovin.player.api.buffer.BufferType;
import com.bitmovin.player.api.drm.WidevineConfig;
import com.bitmovin.player.api.event.EventListener;
import com.bitmovin.player.api.event.PlayerEvent;
import com.bitmovin.player.api.live.LiveConfig;
import com.bitmovin.player.api.live.LiveSynchronizationMethod;
import com.bitmovin.player.api.live.LowLatencyConfig;
import com.bitmovin.player.api.live.LowLatencySynchronizationConfig;
import com.bitmovin.player.api.media.MediaType;
import com.bitmovin.player.api.source.SourceConfig;
import com.bitmovin.player.api.source.SourceType;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class PlayerActivity extends AppCompatActivity {
    private static final String TAG = PlayerActivity.class.getSimpleName();

    public static final String STREAM = "STREAM";
    public static final String DRM = "DRM";

    private PlayerView bitmovinPlayerView;
    private Player bitmovinPlayer;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        Intent intent = this.getIntent();

        String stream = intent.getStringExtra(STREAM);
        if (stream == null) {
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
        this.seekLatency.setProgress((int) (START_LATENCY * 10.0));
        this.textTargetLatency.setText(decimalFormat.format(START_LATENCY));
        this.seekLatency.setOnSeekBarChangeListener(this.onSeekBarChangeListener);

        LinearLayout playerContainer = findViewById(R.id.playerContainer);

        // Creating a new PlayerConfig
        PlayerConfig playerConfig = new PlayerConfig();

        SourceConfig sourceConfig = new SourceConfig(stream, SourceType.Dash);
        if (drm != null) {
            sourceConfig.setDrmConfig(new WidevineConfig(drm));
        }

        playerConfig.getPlaybackConfig().setAutoplayEnabled(true);

        LiveConfig liveConfig = new LiveConfig();
        liveConfig.addSynchronizationEntry("time.akamai.com", LiveSynchronizationMethod.Ntp);
        LowLatencyConfig lowLatencyConfig = new LowLatencyConfig(START_LATENCY);
        lowLatencyConfig.setCatchupConfig(new LowLatencySynchronizationConfig(0.075, 5, 1.2f));
        lowLatencyConfig.setFallbackConfig(new LowLatencySynchronizationConfig(0.075, 5, 0.95f));
        liveConfig.setLowLatencyConfig(lowLatencyConfig);

        playerConfig.setLiveConfig(liveConfig);

        String key = "{ANALYTICS_LICENSE_KEY}";
        Player player = PlayerFactory.create(this, playerConfig, new AnalyticsConfig(key));
        this.bitmovinPlayerView = new PlayerView(this, player);
        this.bitmovinPlayerView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        this.bitmovinPlayerView.setKeepScreenOn(true);

        playerContainer.addView(bitmovinPlayerView);

        this.bitmovinPlayer = bitmovinPlayerView.getPlayer();
        this.bitmovinPlayer.on(PlayerEvent.Error.class, this.onErrorListener);

        this.bitmovinPlayer.load(sourceConfig);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ticker.run();
                statsTicker.run();
            }
        }, 1000);
        this.seekLatency.requestFocus();
    }

    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            double latency = (progress + MIN_LATENCY_CENTIES) / 10.0;
            Log.e(TAG, "A new TargetLatency is set (" + progress + "): " + latency);
            bitmovinPlayer.getLowLatency().setTargetLatency(latency);
            textTargetLatency.setText(decimalFormat.format(latency));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };

    private final Runnable statsTicker = new Runnable() {
        public void run() {
            updateStats();

            Handler handler = textBuffer.getHandler();
            if (handler == null) {
                return;
            }
            handler.postDelayed(statsTicker, 100);
        }
    };

    private void updateStats() {
        if (textLatency != null) {
            textLatency.setText(decimalFormat.format(bitmovinPlayer.getLowLatency().getLatency()));
        }
        if (textBuffer != null) {
            double buffer = Math.min(
                    bitmovinPlayer.getBuffer().getLevel(BufferType.ForwardDuration, MediaType.Video).getLevel(),
                    bitmovinPlayer.getBuffer().getLevel(BufferType.ForwardDuration, MediaType.Audio).getLevel()
            );
            textBuffer.setText(decimalFormat.format(buffer));
        }
    }

    // region system time

    private final Runnable ticker = new Runnable() {
        public void run() {
            if (clock == null) {
                return;
            }
            onTimeChanged();

            long now = SystemClock.uptimeMillis();
            long next = now + (10 - now % 10);

            Handler handler = clock.getHandler();
            if (handler == null) {
                return;
            }
            handler.postAtTime(ticker, next);
        }
    };

    private void onTimeChanged() {
        this.calendar.setTimeInMillis(System.currentTimeMillis());
        this.clock.setText(simpleDateFormat.format(this.calendar.getTime()));
    }

    // endregion

    // region listeners

    private EventListener<PlayerEvent.Error> onErrorListener = new EventListener<PlayerEvent.Error>() {
        @Override
        public void onEvent(PlayerEvent.Error event) {
            Log.e(TAG, "An error occurred (" + event.getCode() + "): " + event.getMessage());
        }
    };

    // endregion

    @Override
    protected void onStart() {
        super.onStart();
        this.bitmovinPlayerView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.bitmovinPlayerView.onResume();
    }

    @Override
    protected void onPause() {
        this.bitmovinPlayerView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        this.bitmovinPlayerView.onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        this.bitmovinPlayerView.onDestroy();
        super.onDestroy();
    }
}
