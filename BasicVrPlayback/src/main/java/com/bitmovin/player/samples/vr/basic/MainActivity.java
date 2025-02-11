package com.bitmovin.player.samples.vr.basic;

import android.os.Bundle;
import android.view.Window;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.bitmovin.player.PlayerView;
import com.bitmovin.player.api.Player;
import com.bitmovin.player.api.source.SourceConfig;
import com.bitmovin.player.api.source.SourceType;
import com.bitmovin.player.api.vr.VrConfig;
import com.bitmovin.player.api.vr.VrContentType;

public class MainActivity extends AppCompatActivity {
    private PlayerView bitmovinPlayerView;
    private Player player;

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

        bitmovinPlayerView = findViewById(R.id.bitmovinPlayerView);
        player = bitmovinPlayerView.getPlayer();

        // Enabling the gyroscopic controlling for the 360° video
        player.getVr().setGyroscopeEnabled(true);

        initializePlayer();
    }

    @Override
    protected void onStart() {
        bitmovinPlayerView.onStart();
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bitmovinPlayerView.onResume();
    }

    @Override
    protected void onPause() {
        bitmovinPlayerView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        bitmovinPlayerView.onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        bitmovinPlayerView.onDestroy();
        super.onDestroy();
    }

    protected void initializePlayer() {
        // Create a new SourceItem
        SourceConfig vrSourceItem = new SourceConfig("https://cdn.bitmovin.com/content/assets/playhouse-vr/mpds/105560.mpd", SourceType.Dash);

        // Get the current VRConfiguration of the SourceItem
        VrConfig vrConfiguration = vrSourceItem.getVrConfig();
        // Set the VrContentType on the VRConfiguration
        vrConfiguration.setVrContentType(VrContentType.Single);
        // Set the start position to 180 degrees
        vrConfiguration.setStartPosition(180);

        // load source using the created source item
        player.load(vrSourceItem);
    }
}
