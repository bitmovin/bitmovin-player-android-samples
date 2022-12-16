package com.bitmovin.player.samples.logging;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bitmovin.player.PlayerView;
import com.bitmovin.player.api.Player;
import com.bitmovin.player.api.event.Event;
import com.bitmovin.player.api.event.SourceEvent;
import com.bitmovin.player.api.source.Source;
import com.bitmovin.player.api.source.SourceConfig;
import com.google.android.exoplayer2.util.Log;


public class MainActivity extends AppCompatActivity {
    private PlayerView playerView;
    private Player player;

    private EventLogger<Event> playerLogger;
    private EventLogger<SourceEvent> sourceLogger;
    private EventLogger<Event> viewLogger;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        player = Player.create(this);
        Source source = Source.create(
                SourceConfig.fromUrl("https://bitdash-a.akamaihd.net/content/sintel/sintel.mpd")
        );

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
