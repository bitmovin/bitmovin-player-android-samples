package com.bitmovin.player.samples.custom.adaptation;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.bitmovin.player.BitmovinPlayer;
import com.bitmovin.player.BitmovinPlayerView;
import com.bitmovin.player.config.AdaptationConfiguration;
import com.bitmovin.player.config.PlayerConfiguration;
import com.bitmovin.player.config.adaptation.VideoAdaptation;
import com.bitmovin.player.config.adaptation.data.VideoAdaptationData;
import com.bitmovin.player.config.media.SourceItem;
import com.bitmovin.player.config.quality.VideoQuality;

public class MainActivity extends AppCompatActivity {

    private BitmovinPlayer bitmovinPlayer;
    private BitmovinPlayerView bitmovinPlayerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PlayerConfiguration playerConfiguration = this.createPlayerConfiguration();
        this.bitmovinPlayerView = new BitmovinPlayerView(this, playerConfiguration);
        this.bitmovinPlayer = this.bitmovinPlayerView.getPlayer();
        this.bitmovinPlayer.load(new SourceItem("https://bitdash-a.akamaihd.net/content/sintel/sintel.mpd"));

        LinearLayout rootView = this.findViewById(R.id.root);
        this.bitmovinPlayerView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        rootView.addView(this.bitmovinPlayerView, 0);
    }

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

    private PlayerConfiguration createPlayerConfiguration() {
        // Setup adaptation configuration
        AdaptationConfiguration adaptationConfiguration = new AdaptationConfiguration();
        adaptationConfiguration.setAllowRebuffering(true);
        adaptationConfiguration.setMaxSelectableVideoBitrate(800_000);
        adaptationConfiguration.setStartupBitrate(1_200_000);
        adaptationConfiguration.setVideoAdaptation(videoAdaptationListener);

        // Assign adaptation to player configuration
        PlayerConfiguration playerConfiguration = new PlayerConfiguration();
        playerConfiguration.setAdaptationConfiguration(adaptationConfiguration);
        return playerConfiguration;
    }

    private VideoAdaptation videoAdaptationListener = new VideoAdaptation() {
        /*
         *  Customize this method to return a different video quality id than what is suggested
         */
        @Override
        public String onVideoAdaptation(VideoAdaptationData videoAdaptationData) {
            // Get the suggested video quality id
            String suggestedVideoQualityId = videoAdaptationData.getSuggested();

            // Add your own logic to choose a different video quality
            VideoQuality[] videoQualities = bitmovinPlayer.getAvailableVideoQualities();

            return suggestedVideoQualityId;
        }
    };
}
