package com.bitmovin.player.samples.playlist.basic;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bitmovin.player.PlayerView;
import com.bitmovin.player.api.Player;
import com.bitmovin.player.api.event.EventListener;
import com.bitmovin.player.api.event.PlayerEvent;
import com.bitmovin.player.api.playlist.PlaylistConfig;
import com.bitmovin.player.api.playlist.PlaylistOptions;
import com.bitmovin.player.api.source.Source;
import com.bitmovin.player.api.source.SourceBuilder;
import com.bitmovin.player.api.source.SourceConfig;

import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private PlayerView playerView;
    private Player player;

    private final EventListener<PlayerEvent.PlaylistTransition> onReadyListener = event -> {
        String text = "Transitioned from " + event.getFrom().getConfig().getTitle() + " to " + event.getTo().getConfig().getTitle();
        Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.playerView = this.findViewById(R.id.playerView);
        this.player = this.playerView.getPlayer();

        SourceConfig sourceConfig1 = SourceConfig.fromUrl("https://bitmovin-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8");
        sourceConfig1.setTitle("Art of Motions");
        SourceConfig sourceConfig2 = SourceConfig.fromUrl("https://bitmovin-a.akamaihd.net/content/sintel/hls/playlist.m3u8");
        sourceConfig2.setTitle("Sintel");

        List<Source> sources = Arrays.asList(
                new SourceBuilder(sourceConfig1).build(),
                new SourceBuilder(sourceConfig2).build()
        );

        this.player.on(PlayerEvent.PlaylistTransition.class, this.onReadyListener);

        this.player.load(new PlaylistConfig(sources, new PlaylistOptions()));
    }


    @Override
    protected void onStart() {
        this.playerView.onStart();
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.playerView.onResume();
    }

    @Override
    protected void onPause() {
        this.playerView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        this.playerView.onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        this.playerView.onDestroy();
        super.onDestroy();
    }
}
