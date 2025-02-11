package com.bitmovin.player.samples.ads.bitmovin;

import android.os.Bundle;
import android.view.Window;
import android.widget.LinearLayout;

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
import com.bitmovin.player.api.PlayerConfig;
import com.bitmovin.player.api.advertising.AdItem;
import com.bitmovin.player.api.advertising.AdSource;
import com.bitmovin.player.api.advertising.AdSourceType;
import com.bitmovin.player.api.advertising.AdvertisingConfig;
import com.bitmovin.player.api.source.SourceConfig;

public class MainActivity extends AppCompatActivity {
    private static final String BITMOVIN_AD = "https://cdn.bitmovin.com/content/player/advertising/bitmovin-ad.xml";
    private static final String NO_RESPONSE_AD = "https://this-url-doesnt-exist/ad.xml";
    // These are Sample Tags from https://developers.google.com/interactive-media-ads/docs/sdks/android/tags
    private static final String SINGLE_REDIRECT_LINEAR_AD = "https://pubads.g.doubleclick.net/gampad/ads?iu=/21775744923/external/single_ad_samples&sz=640x480&cust_params=sample_ct%3Dredirectlinear&ciu_szs=300x250%2C728x90&gdfp_req=1&output=vast&unviewed_position_start=1&env=vp&impl=s&correlator=";
    private static final String SINGLE_SKIPPABLE_INLINE_AD = "https://pubads.g.doubleclick.net/gampad/ads?iu=/21775744923/external/single_preroll_skippable&sz=640x480&ciu_szs=300x250%2C728x90&gdfp_req=1&output=vast&unviewed_position_start=1&env=vp&impl=s&correlator=";

    private PlayerView playerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_main), (view, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        Window window = getWindow();
        WindowInsetsControllerCompat insetsController = WindowCompat.getInsetsController(window,
                window.getDecorView());
        insetsController.setAppearanceLightStatusBars(true);
        insetsController.setAppearanceLightNavigationBars(true);

        // Create AdSources
        AdSource bitmovinAd = new AdSource(AdSourceType.Bitmovin, BITMOVIN_AD);
        AdSource noResponseAd = new AdSource(AdSourceType.Bitmovin, NO_RESPONSE_AD);
        AdSource redirectLinearAd = new AdSource(AdSourceType.Bitmovin, SINGLE_REDIRECT_LINEAR_AD);
        AdSource skippableInlineAd = new AdSource(AdSourceType.Bitmovin, SINGLE_SKIPPABLE_INLINE_AD);

        // Set up a pre-roll ad
        AdItem preRoll = new AdItem("pre", skippableInlineAd);

        // Set up a mid-roll waterfalling ad at 10% of the content duration
        // NOTE: AdItems containing more than one AdSource will be executed as waterfalling ad
        AdItem midRoll = new AdItem("10%", noResponseAd, bitmovinAd);

        // Set up a post-roll ad
        AdItem postRoll = new AdItem("post", redirectLinearAd);

        // Add the AdItems to the AdvertisingConfig
        AdvertisingConfig advertisingConfig = new AdvertisingConfig(preRoll, midRoll, postRoll);

        // Create a new PlayerConfiguration
        PlayerConfig playerConfig = new PlayerConfig();

        // Add the AdvertisingConfig to the PlayerConfig. Ads in the AdvertisingConfig will be scheduled automatically.
        playerConfig.setAdvertisingConfig(advertisingConfig);

        // Create new BitmovinPlayerView with our PlayerConfiguration and AnalyticsConfiguration
        String key = "{ANALYTICS_LICENSE_KEY}";
        Player player = new PlayerBuilder(this)
                .setPlayerConfig(playerConfig)
                .configureAnalytics(new AnalyticsConfig(key))
                .build();
        playerView = new PlayerView(this, player);
        playerView.setLayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                )
        );
        playerView.setKeepScreenOn(true);
        playerView.getPlayer().load(SourceConfig.fromUrl("https://cdn.bitmovin.com/content/assets/sintel/sintel.mpd"));

        LinearLayout rootView = findViewById(R.id.activity_main);

        // Add PlayerView to the layout
        rootView.addView(playerView, 0);
    }

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
        playerView.onDestroy();
        super.onDestroy();
    }
}
