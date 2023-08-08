package com.bitmovin.player.samples.custom.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import com.bitmovin.analytics.api.AnalyticsConfig
import com.bitmovin.player.PlayerView
import com.bitmovin.player.api.Player
import com.bitmovin.player.api.PlayerConfig
import com.bitmovin.player.api.analytics.create
import com.bitmovin.player.api.event.*
import com.bitmovin.player.api.source.Source
import com.bitmovin.player.api.ui.FullscreenHandler
import com.bitmovin.player.samples.custom.ui.databinding.PlayerUiBinding
import java.util.*

private const val LIVE = "Live"
private const val UI_HIDE_TIMER = 5000

class PlayerUI(
    context: Context,
    playerConfig: PlayerConfig
) : RelativeLayout(context) {
    // Create new Player with our PlayerConfig
    private val analyticsKey = "{ANALYTICS_LICENSE_KEY}"
    private val player = Player.create(context, playerConfig, AnalyticsConfig(analyticsKey))
    private var binding: PlayerUiBinding = PlayerUiBinding.inflate(
        LayoutInflater.from(context), this, true
    )

    // Create new PlayerView with our Player
    private val playerView: PlayerView = PlayerView(context, player).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    private var playDrawable: Drawable? = null
    private var pauseDrawable: Drawable? = null
    private var lastUiInteraction: Long = 0
    private var uiHideTimer: Timer = Timer()
    private var uiHideTask: TimerTask? = null
    private var live: Boolean = false

    private val seekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            // Only seek/timeShift when the user changes the progress (and not the PlayerEvent.TimeChanged )
            if (fromUser) player.seekOrTimeShift(progress, seekBar)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {}

        override fun onStopTrackingTouch(seekBar: SeekBar) {}
    }

    private val onClickListener = OnClickListener { view ->
        when {
            view === binding.playButton || view === this@PlayerUI -> player.togglePlayback()
            (view === binding.fullscreenButton) -> playerView.toggleFullscreen()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private val onTouchListener = OnTouchListener { _, event ->
        lastUiInteraction = System.currentTimeMillis()

        if (event.action == MotionEvent.ACTION_UP) {
            // Start the hider task, when the UI is not touched
            startUiHiderTask()
        } else {
            // When the view is touched, the UI should be visible
            setControlsVisible(true)
        }
        false
    }

    init {

        playDrawable = ContextCompat.getDrawable(context, R.drawable.ic_play_arrow_black_24dp)
        pauseDrawable = ContextCompat.getDrawable(context, R.drawable.ic_pause_black_24dp)

        binding.apply {
            seekbar.setOnSeekBarChangeListener(seekBarChangeListener)
            playButton.setOnClickListener(onClickListener)
            fullscreenButton.setOnClickListener(onClickListener)
            playButton.setOnTouchListener(onTouchListener)
            seekbar.setOnTouchListener(onTouchListener)
        }
        setOnTouchListener(onTouchListener)

        // Add PlayerView to the layout
        addView(playerView, 0)

        addPlayerListener()
        updateUi()
    }

    fun load(source: Source) {
        source.next<SourceEvent.Loaded>(::updateUi)
        player.load(source)
    }

    private fun addPlayerListener() {
        player.on<PlayerEvent.TimeChanged>(::updateUi)
        player.on<PlayerEvent.Play>(::updateUi)
        player.on<PlayerEvent.Paused>(::updateUi)
        player.on<PlayerEvent.StallEnded>(::updateUi)
        player.on<PlayerEvent.Seeked>(::updateUi)
        player.on<PlayerEvent.PlaybackFinished>(::updateUi)

    }

    private fun removePlayerListener() {
        player.off(::updateUi)
    }

    private fun startUiHiderTask() {
        stopUiHiderTask()

        // Create Task which hides the UI after a specified time (UiHideTimer)
        uiHideTask = object : TimerTask() {
            override fun run() {
                val timeSincelastUiInteraction = System.currentTimeMillis() - lastUiInteraction
                if (timeSincelastUiInteraction > UI_HIDE_TIMER) {
                    setControlsVisible(false)
                }
            }
        }
        // Schedule the hider task, so it checks the state every 100ms
        uiHideTimer.scheduleAtFixedRate(uiHideTask, 0, 100)
    }

    private fun stopUiHiderTask() {
        uiHideTask?.cancel()
        uiHideTask = null
    }

    fun setVisible(visible: Boolean) {
        lastUiInteraction = System.currentTimeMillis()
        setControlsVisible(visible)
    }

    private fun setControlsVisible(visible: Boolean) {
        post {
            if (visible) {
                startUiHiderTask()
            } else {
                stopUiHiderTask()
            }

            val visibility = if (visible) View.VISIBLE else View.INVISIBLE
            binding.controlView.visibility = visibility
        }
    }

    fun setFullscreenHandler(fullscreenHandler: FullscreenHandler) {
        playerView.setFullscreenHandler(fullscreenHandler)
    }

    fun onStart() = playerView.onStart()

    fun onResume() = playerView.onResume()

    fun onPause() = playerView.onPause()

    fun onStop() = playerView.onStop()

    fun onDestroy() {
        playerView.onDestroy()
        destroy()
    }

    private fun destroy() {
        player.source?.off(::updateUi)

        removePlayerListener()
        uiHideTimer.cancel()
    }

    /**
     * UI Listeners
     */
    override fun onTouchEvent(event: MotionEvent): Boolean = true

    /**
     * Methods for UI update
     */
    private fun updateUi(event: Event? = null) {
        binding.seekbar.post {
            // if the live state of the player changed, the UI should change it's mode
            val positionMs: Int
            val durationMs: Int

            if (live != player.isLive) {
                live = player.isLive
                if (live) {
                    binding.positionView.visibility = View.GONE
                    binding.durationView.text = LIVE
                } else {
                    binding.positionView.visibility = View.VISIBLE
                }
            }

            if (live) {
                // The Seekbar does not support negative values
                // so the seekable range is shifted to the positive
                durationMs = (-player.maxTimeShift * 1000).toInt()
                positionMs = (durationMs + player.timeShift * 1000).toInt()
            } else {
                // Converting to milliseconds
                positionMs = (player.currentTime * 1000).toInt()
                durationMs = (player.duration * 1000).toInt()

                // Update the TextViews displaying the current position and duration
                binding.positionView.text = millisecondsToTimeString(positionMs)
                binding.durationView.text = millisecondsToTimeString(durationMs)
            }

            // Update the values of the Seekbar
            binding.seekbar.progress = positionMs
            binding.seekbar.max = durationMs

            // Update the image of the playback button
            if (player.isPlaying) {
                binding.playButton.setImageDrawable(pauseDrawable)
            } else {
                binding.playButton.setImageDrawable(playDrawable)
            }
        }
    }

    private fun millisecondsToTimeString(milliseconds: Int): String {
        val second = milliseconds / 1000 % 60
        val minute = milliseconds / (1000 * 60) % 60
        val hour = milliseconds / (1000 * 60 * 60) % 24

        return if (hour > 0) {
            String.format("%02d:%02d:%02d", hour, minute, second)
        } else {
            String.format("%02d:%02d", minute, second)
        }
    }
}


private fun Player.togglePlayback() = if (isPlaying) pause() else play()

private fun PlayerView.toggleFullscreen() = if (isFullscreen) {
    exitFullscreen()
} else {
    enterFullscreen()
}

// If the current stream is a live stream, we have to use the timeShift method
private fun Player.seekOrTimeShift(progress: Int, seekBar: SeekBar) {
    if (!isLive) {
        seek(progress / 1000.0)
    } else {
        timeShift((progress - seekBar.max) / 1000.0)
    }
}
