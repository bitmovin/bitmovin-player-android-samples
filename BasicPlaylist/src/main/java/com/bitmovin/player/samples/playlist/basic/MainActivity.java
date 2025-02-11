package com.bitmovin.player.samples.playlist.basic;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.bitmovin.player.PlayerView;
import com.bitmovin.player.api.Player;
import com.bitmovin.player.api.event.EventListener;
import com.bitmovin.player.api.event.PlayerEvent;
import com.bitmovin.player.api.playlist.PlaylistApi;
import com.bitmovin.player.api.playlist.PlaylistConfig;
import com.bitmovin.player.api.playlist.PlaylistOptions;
import com.bitmovin.player.api.source.Source;
import com.bitmovin.player.api.source.SourceBuilder;
import com.bitmovin.player.api.source.SourceConfig;
import com.bitmovin.player.api.source.SourceType;

import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private PlayerView playerView;
    private Player player;

    private static final String SintelHls = "https://cdn.bitmovin.com/content/assets/sintel/hls/playlist.m3u8";
    private static final String ArtOfMotionProgressive = "https://cdn.bitmovin.com/content/assets/MI201109210084/MI201109210084_mpeg-4_hd_high_1080p25_10mbits.mp4";
    private static final String SintelDash = "https://cdn.bitmovin.com/content/assets/sintel/sintel.mpd";
    private static final String KronehitLiveHls = "https://bitcdn-kronehit.bitmovin.com/v2/hls/playlist.m3u8";

    private final EventListener<PlayerEvent.PlaylistTransition> onReadyListener = event -> {
        String text = "Transitioned from " + event.getFrom().getConfig().getTitle() + " to " + event.getTo().getConfig().getTitle();
        Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
    };

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

        this.playerView = this.findViewById(R.id.playerView);
        this.player = this.playerView.getPlayer();

        SourceConfig sourceConfig1 = new SourceConfig(SintelHls, SourceType.Hls);
        sourceConfig1.setTitle("(1/4) Sintel HLS");
        SourceConfig sourceConfig2 = new SourceConfig(ArtOfMotionProgressive, SourceType.Progressive);
        sourceConfig2.setTitle("(2/4) Art of Motion Progressive");
        SourceConfig sourceConfig3 = new SourceConfig(SintelDash, SourceType.Dash);
        sourceConfig3.setTitle("(3/4) Sintel DASH");
        SourceConfig sourceConfig4 = new SourceConfig(KronehitLiveHls, SourceType.Hls);
        sourceConfig4.setTitle("(4/4) Kronehit Live HLS");

        List<Source> sources = Arrays.asList(
                new SourceBuilder(sourceConfig1).build(),
                new SourceBuilder(sourceConfig2).build(),
                new SourceBuilder(sourceConfig3).build(),
                new SourceBuilder(sourceConfig4).build()
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

    /**
     * @return the index of the current active source or -1 if none is active
     */
    private int getActiveSourceIndex(PlaylistApi playlist) {
        List<Source> sources = playlist.getSources();
        for (int index = 0; index < sources.size(); index++) {
            if (sources.get(index).isActive()) {
                return index;
            }
        }
        return -1;
    }

    public void next(View view) {
        playlistIncrement(1);
    }

    public void previous(View view) {
        playlistIncrement(-1);
    }

    private void playlistIncrement(int increment) {
        final PlaylistApi playlist = this.player.getPlaylist();
        final int activeSourceIndex = getActiveSourceIndex(playlist);
        if (activeSourceIndex == -1) {
            return;
        }
        final int newSourceIndex = activeSourceIndex + increment;
        if (newSourceIndex < 0 || newSourceIndex >= playlist.getSources().size()) {
            return;
        }

        playlist.seek(playlist.getSources().get(newSourceIndex), 0.0);
    }
}
