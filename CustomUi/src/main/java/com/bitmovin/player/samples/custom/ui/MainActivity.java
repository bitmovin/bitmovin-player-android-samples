package com.bitmovin.player.samples.custom.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bitmovin.player.config.PlayerConfiguration;
import com.bitmovin.player.config.StyleConfiguration;
import com.bitmovin.player.config.media.SourceConfiguration;
import com.bitmovin.player.ui.FullscreenHandler;

public class MainActivity extends AppCompatActivity
{
    private PlayerUI playerUi;
    private FullscreenHandler fullscreenHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create new StyleConfiguration
        StyleConfiguration styleConfiguration = new StyleConfiguration();
        // Disable UI
        styleConfiguration.setUiEnabled(false);

        // Create a new source configuration
        SourceConfiguration sourceConfiguration = new SourceConfiguration();
        // Add a new source item
        sourceConfiguration.addSourceItem("http://bitdash-a.akamaihd.net/content/sintel/sintel.mpd");

        // Creating a new PlayerConfiguration
        PlayerConfiguration playerConfiguration = new PlayerConfiguration();
        // Assign created StyleConfiguration to the PlayerConfiguration
        playerConfiguration.setStyleConfiguration(styleConfiguration);
        // Assign created SourceConfiguration to the PlayerConfiguration
        playerConfiguration.setSourceConfiguration(sourceConfiguration);

        this.playerUi = new PlayerUI(this, playerConfiguration);
        this.fullscreenHandler = new CustomFullscreenHandler(this, playerUi);

        // Set the FullscreenHandler of the PlayerUI
        this.playerUi.setFullscreenHandler(fullscreenHandler);

        LinearLayout rootView = (LinearLayout) this.findViewById(R.id.activity_main);

        this.playerUi.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        rootView.addView(this.playerUi);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        this.playerUi.onResume();
    }

    @Override
    protected void onPause()
    {
        this.playerUi.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy()
    {
        this.playerUi.onDestroy();
        super.onDestroy();
    }
}
