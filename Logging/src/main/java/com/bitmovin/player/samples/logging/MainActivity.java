package com.bitmovin.player.samples.logging;

import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.bitmovin.analytics.api.AnalyticsConfig;
import com.bitmovin.player.PlayerView;
import com.bitmovin.player.api.Player;
import com.bitmovin.player.api.PlayerBuilder;
import com.bitmovin.player.api.event.Event;
import com.bitmovin.player.api.event.SourceEvent;
import com.bitmovin.player.api.source.Source;
import com.bitmovin.player.api.source.SourceBuilder;
import com.bitmovin.player.api.source.SourceConfig;


public class MainActivity extends AppCompatActivity {
    private PlayerView playerView;
    private Player player;

    private EventLogger<Event> playerLogger;
    private EventLogger<SourceEvent> sourceLogger;
    private EventLogger<Event> viewLogger;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        playerView = findViewById(R.id.playerView);

        String tag = "BitmovinPlayer";
        playerLogger = new EventLogger<>(
                LoggerConfig.createDefaultPlayerLoggerConfig(),
                event -> Log.e(tag, event.toString()),
                event -> Log.w(tag, event.toString()),
                event -> Log.i(tag, event.toString()),
                event -> Log.d(tag, event.toString())
        );

        sourceLogger = new EventLogger<>(
                LoggerConfig.createDefaultSourceLoggerConfig(),
                event -> Log.e(tag, event.toString()),
                event -> Log.w(tag, event.toString()),
                event -> Log.i(tag, event.toString()),
                event -> Log.d(tag, event.toString())
        );

        viewLogger = new EventLogger<>(
                LoggerConfig.createDefaultViewLoggerConfig(),
                event -> Log.e(tag, event.toString()),
                event -> Log.w(tag, event.toString()),
                event -> Log.i(tag, event.toString()),
                event -> Log.d(tag, event.toString())
        );


        initializePlayer();
    }

    private void initializePlayer() {
        String key = "{ANALYTICS_LICENSE_KEY}";
        player = new PlayerBuilder(this)
                .configureAnalytics(new AnalyticsConfig(key))
                .build();
        Source source = new SourceBuilder(
                SourceConfig.fromUrl("https://cdn.bitmovin.com/content/assets/sintel/sintel.mpd")
        ).build();

        viewLogger.attach(playerView);
        playerLogger.attach(player);
        sourceLogger.attach(source);

        playerView.setPlayer(player);

        // load source
        player.load(source);
    }

    @Override
    protected void onStart() {
        playerView.onStart();
        super.onStart();
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
        playerView.onDestroy();

        viewLogger.detach();
        playerLogger.detach();
        sourceLogger.detach();

        super.onDestroy();
    }
}
