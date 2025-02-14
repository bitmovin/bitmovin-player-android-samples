package com.bitmovin.player.samples.lowLatency;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Window;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.bitmovin.analytics.api.AnalyticsConfig;
import com.bitmovin.player.PlayerView;
import com.bitmovin.player.api.PlaybackConfig;
import com.bitmovin.player.api.Player;
import com.bitmovin.player.api.PlayerBuilder;
import com.bitmovin.player.api.PlayerConfig;
import com.bitmovin.player.api.buffer.BufferType;
import com.bitmovin.player.api.live.LiveConfig;
import com.bitmovin.player.api.live.LiveSynchronizationMethod;
import com.bitmovin.player.api.live.SourceLiveConfig;
import com.bitmovin.player.api.live.TargetSynchronizationConfig;
import com.bitmovin.player.api.media.MediaType;
import com.bitmovin.player.api.source.SourceConfig;
import com.bitmovin.player.api.source.SourceType;
import com.bitmovin.player.samples.lowLatency.databinding.ActivityMainBinding;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private PlayerView playerView;
    private Player player;
    private final SimpleDateFormat clockFormat = new SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault());
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root), (view, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        Window window = getWindow();
        WindowInsetsControllerCompat insetsController = WindowCompat.getInsetsController(window,
                window.getDecorView());
        insetsController.setAppearanceLightStatusBars(true);
        insetsController.setAppearanceLightNavigationBars(true);

        initializePlayer();

        mainHandler.post(continuouslyUpdateCurrentTimeRunnable);
        mainHandler.post(continuouslyUpdateLatencyAndBufferLevelRunnable);
    }

    private void initializePlayer() {
        SourceLiveConfig sourceLiveConfig = new SourceLiveConfig();
        // Initial target latency that the player tries to achieve
        sourceLiveConfig.setTargetLatency(3.0);
        // Configure catchup and fallback behavior.
        // The values provided here are exaggerated for demonstration purposes,
        // using the default values is recommended.
        sourceLiveConfig.setCatchupConfig(
                new TargetSynchronizationConfig(
                        TargetSynchronizationConfig.DEFAULT_SEEK_THRESHOLD,
                        2.0f
                )
        );
        sourceLiveConfig.setFallbackConfig(
                new TargetSynchronizationConfig(
                        5.0,
                        0.5f
                )
        );
        LiveConfig.Builder playerLiveConfig = new LiveConfig.Builder();
        playerLiveConfig.addSynchronizationEntry(
                "time.akamai.com",
                LiveSynchronizationMethod.Ntp
        );

        SourceConfig sourceConfig = new SourceConfig(
                "https://akamaibroadcasteruseast.akamaized.net/cmaf/live/657078/akasource/out.mpd",
                SourceType.Dash
        );
        sourceConfig.setLiveConfig(sourceLiveConfig);

        String analyticsKey = "{ANALYTICS_LICENSE_KEY}";
        PlayerConfig.Builder playerConfigBuilder = new PlayerConfig.Builder();
        PlaybackConfig.Builder playbackConfigBuilder = new PlaybackConfig.Builder();
        playbackConfigBuilder.setIsAutoplayEnabled(true);
        playerConfigBuilder.setPlaybackConfig(playbackConfigBuilder.build());
        playerConfigBuilder.setLiveConfig(playerLiveConfig.build());

        player = new PlayerBuilder(this)
                .setPlayerConfig(playerConfigBuilder.build())
                .configureAnalytics(new AnalyticsConfig(analyticsKey))
                .build();

        playerView = binding.playerView;
        playerView.setPlayer(player);

        player.load(sourceConfig);
    }

    private final Runnable continuouslyUpdateCurrentTimeRunnable = new Runnable() {
        @Override
        public void run() {
            binding.currentTimeTextView.setText(clockFormat.format(System.currentTimeMillis()));

            mainHandler.postDelayed(this, 100);
        }
    };

    private final Runnable continuouslyUpdateLatencyAndBufferLevelRunnable = new Runnable() {
        @SuppressLint("SetTextI18n")
        @Override
        public void run() {
            double currentLatency = player.getLowLatency().getLatency();
            double targetLatency = player.getLowLatency().getTargetLatency();
            double videoBuffer = player.getBuffer().getLevel(
                    BufferType.ForwardDuration,
                    MediaType.Video
            ).getLevel();

            binding.currentLatencyTextView.setText("Current Latency: " + currentLatency);
            binding.targetLatencyTextView.setText("Target Latency: " + targetLatency);
            binding.bufferTextView.setText("Forward buffer: " + videoBuffer);

            mainHandler.postDelayed(this, 500);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        playerView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        playerView.onResume();
    }

    @Override
    protected void onPause() {
        playerView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        playerView.onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mainHandler.removeCallbacks(continuouslyUpdateCurrentTimeRunnable);
        mainHandler.removeCallbacks(continuouslyUpdateLatencyAndBufferLevelRunnable);

        playerView.onDestroy();
        super.onDestroy();
    }
}
