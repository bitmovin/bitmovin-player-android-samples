package com.bitmovin.player.samples.ads.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bitmovin.analytics.api.AnalyticsConfig
import com.bitmovin.player.PlayerView
import com.bitmovin.player.api.PlaybackConfig
import com.bitmovin.player.api.Player
import com.bitmovin.player.api.PlayerConfig
import com.bitmovin.player.api.advertising.AdItem
import com.bitmovin.player.api.advertising.AdSource
import com.bitmovin.player.api.advertising.AdSourceType
import com.bitmovin.player.api.advertising.AdvertisingConfig
import com.bitmovin.player.api.advertising.LinearAd
import com.bitmovin.player.api.analytics.AnalyticsPlayerConfig
import com.bitmovin.player.api.event.PlayerEvent
import com.bitmovin.player.api.event.on
import com.bitmovin.player.api.source.SourceConfig
import com.bitmovin.player.api.source.SourceType
import com.bitmovin.player.api.ui.PlayerViewConfig
import com.bitmovin.player.api.ui.UiConfig
import com.bitmovin.player.samples.ads.ui.databinding.ActivityMainBinding
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

private const val BITMOVIN_AD = "https://cdn.bitmovin.com/content/player/advertising/bitmovin-ad.xml"

@SuppressLint("SetTextI18n")
class MainActivity : AppCompatActivity() {
    private lateinit var playerView: PlayerView
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Create a new PlayerConfig containing the advertising config.
        // Ads in the AdvertisingConfig will be scheduled automatically.
        val playerConfig = PlayerConfig(
            advertisingConfig = AdvertisingConfig(
                AdItem(
                    position = "pre",
                    AdSource(AdSourceType.Bitmovin, BITMOVIN_AD),
                ),
            ),
            playbackConfig = PlaybackConfig(
                isAutoplayEnabled = true,
            ),
        )

        // Create new Player with our PlayerConfig
        val analyticsKey = "{ANALYTICS_LICENSE_KEY}"
        val player = Player(
            this,
            playerConfig,
            AnalyticsPlayerConfig.Enabled(AnalyticsConfig(analyticsKey)),
        )

        // Disable the default UI so a custom one can be used
        playerView = PlayerView(
            this,
            player,
            PlayerViewConfig(uiConfig = UiConfig.Disabled),
        ).apply {
            layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT,
            )
            // set up basic play/pause functionality as click listener on the PlayerView
            setOnClickListener {
                if (player.isPlaying) {
                    player.pause()
                } else {
                    player.play()
                }
            }
        }

        player.on<PlayerEvent.AdStarted> {
            // This ui only supports linear ads
            val linearAd = it.ad as? LinearAd ?: return@on
            if (linearAd.uiConfig?.requestsUi != true) return@on
            binding.adsUiContainer.visibility = View.VISIBLE

            setUpClickThroughControl(
                clicked = {
                    player.pause()
                    startActivity(
                        Intent(Intent.ACTION_VIEW, Uri.parse(linearAd.clickThroughUrl)),
                    )
                },
                clickThroughUrl = linearAd.clickThroughUrl,
                visitAdvertiser = binding.visitAdvertiser,
            )
            setUpSkipControl({ player.skipAd() }, binding.skipAd, linearAd.skippableAfter?.seconds)
            setUpAdInfoControl(binding.adInfo, linearAd.duration.seconds)
        }

        player.on<PlayerEvent.AdBreakFinished> {
            binding.adsUiContainer.visibility = View.GONE
        }

        playerView.keepScreenOn = true
        player.load(
            SourceConfig(
                "https://bitmovin-a.akamaihd.net/content/MI201109210084_1/mpds/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.mpd",
                SourceType.Dash,
            ),
        )

        binding.root.addView(playerView, 0)
    }

    override fun onStart() {
        super.onStart()
        playerView.onStart()
    }

    override fun onResume() {
        super.onResume()
        playerView.onResume()
    }

    override fun onPause() {
        playerView.onPause()
        super.onPause()
    }

    override fun onStop() {
        playerView.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        playerView.onDestroy()
        super.onDestroy()
    }
}

@SuppressLint("SetTextI18n")
private fun setUpAdInfoControl(adInfo: TextView, adDuration: Duration) = countDown(
    onTick = { millisUntilFinished ->
        val paddedDuration = millisUntilFinished.inWholeSeconds.toString().padStart(2, '0')
        adInfo.text = "Ad playing • 0:$paddedDuration"
    },
    duration = adDuration,
)

@SuppressLint("SetTextI18n")
private fun setUpSkipControl(skip: () -> Unit, skipAd: TextView, skippableAfter: Duration?) =
    if (skippableAfter != null) {
        skipAd.visibility = View.VISIBLE
        skipAd.isEnabled = false
        skipAd.setOnClickListener { skip() }

        countDown(
            onTick = { millisUntilFinished ->
                skipAd.text = (millisUntilFinished.inWholeSeconds + 1).toString()
            },
            onFinished = {
                skipAd.text = "Skip >|"
                skipAd.isEnabled = true
            },
            duration = skippableAfter,
        )
    } else {
        skipAd.visibility = View.GONE
    }

private fun setUpClickThroughControl(
    clicked: () -> Unit,
    clickThroughUrl: String?,
    visitAdvertiser: TextView
) = if (clickThroughUrl != null) {
    visitAdvertiser.visibility = View.VISIBLE
    visitAdvertiser.setOnClickListener { clicked() }
} else {
    visitAdvertiser.visibility = View.GONE
}

/**
 * Wrapper to create and start a [CountDownTimer] with a more functional API.
 * Has a default countdown interval of 1 second.
 */
private fun countDown(
    onTick: (Duration) -> Unit,
    onFinished: () -> Unit = {},
    duration: Duration,
    interval: Duration = 1.seconds
) {
    object : CountDownTimer(duration.inWholeMilliseconds, interval.inWholeMilliseconds) {
        override fun onTick(millisUntilFinished: Long) = onTick(millisUntilFinished.milliseconds)
        override fun onFinish() = onFinished()
    }.start()
}
