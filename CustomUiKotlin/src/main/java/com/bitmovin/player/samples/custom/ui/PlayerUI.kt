package com.bitmovin.player.samples.custom.ui

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.SeekBar
import com.bitmovin.player.BitmovinPlayer
import com.bitmovin.player.BitmovinPlayerView
import com.bitmovin.player.api.event.listener.*
import com.bitmovin.player.config.PlayerConfiguration
import com.bitmovin.player.ui.FullscreenHandler
import kotlinx.android.synthetic.main.player_ui.view.*
import java.util.*

class PlayerUI: RelativeLayout {

    private val LIVE = "LIVE"
    private val UI_HIDE_TIME = 5000

    private var bitmovinPlayerView: BitmovinPlayerView? = null
    private var bitmovinPlayer: BitmovinPlayer? = null

    private var playDrawable: Drawable? = null
    private var pauseDrawable: Drawable? = null

    private var lastUiInteraction: Long = 0

    private var uiHideTimer: Timer? = null
    private var uiHideTask: TimerTask? = null

    private var live: Boolean = false

    constructor(context: Context, playerConfiguration: PlayerConfiguration) : super(context) {
        // Create new BitmovinPlayerView with our PlayerConfiguration
        this.bitmovinPlayerView = BitmovinPlayerView(context, playerConfiguration)
        this.bitmovinPlayerView?.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        this.bitmovinPlayer = bitmovinPlayerView?.player
        setup()
    }

    private fun setup() {
        LayoutInflater.from(this.context).inflate(R.layout.player_ui, this)

        this.playDrawable = ContextCompat.getDrawable(this.context, R.drawable.ic_play_arrow_black_24dp)
        this.pauseDrawable = ContextCompat.getDrawable(this.context, R.drawable.ic_pause_black_24dp)

        seekbar.setOnSeekBarChangeListener(this.seekBarChangeListener)
        playButton.setOnClickListener(this.onClickListener)
        fullscreenButton.setOnClickListener(this.onClickListener)
        playButton.setOnTouchListener(this.onTouchListener)
        seekbar.setOnTouchListener(this.onTouchListener)
        setOnTouchListener(this.onTouchListener)

        // Add BitmovinPlayerView to the layout
        this.addView(this.bitmovinPlayerView, 0)

        uiHideTimer = Timer()

        addPlayerListener()
        updateUi()
    }

    private fun addPlayerListener() {
        this.bitmovinPlayer?.addEventListener(onTimeChangedListener)
        this.bitmovinPlayer?.addEventListener(onSourceLoadedListener)
        this.bitmovinPlayer?.addEventListener(onPlayListener)
        this.bitmovinPlayer?.addEventListener(onPausedListener)
        this.bitmovinPlayer?.addEventListener(onStallEndedListener)
        this.bitmovinPlayer?.addEventListener(onSeekedListener)
        this.bitmovinPlayer?.addEventListener(onPlaybackFinishedListener)
    }

    private fun removePlayerListener() {
        this.bitmovinPlayer?.removeEventListener(onTimeChangedListener)
        this.bitmovinPlayer?.removeEventListener(onSourceLoadedListener)
        this.bitmovinPlayer?.removeEventListener(onPlayListener)
        this.bitmovinPlayer?.removeEventListener(onPausedListener)
        this.bitmovinPlayer?.removeEventListener(onStallEndedListener)
        this.bitmovinPlayer?.removeEventListener(onSeekedListener)
        this.bitmovinPlayer?.removeEventListener(onPlaybackFinishedListener)
    }

    private fun startUiHiderTask() {
        stopUiHiderTask()

        // Create Task which hides the UI after a specified time (UI_HIDE_TIME)
        this.uiHideTask = object : TimerTask() {
            override fun run() {
                val timeSincelastUiInteraction = System.currentTimeMillis() - lastUiInteraction
                if (timeSincelastUiInteraction > UI_HIDE_TIME) {
                    setControlsVisible(false)
                }
            }
        }
        // Schedule the hider task, so it checks the state every 100ms
        this.uiHideTimer?.scheduleAtFixedRate(this.uiHideTask, 0, 100)
    }

    private fun stopUiHiderTask() {
        this.uiHideTask?.cancel()
        this.uiHideTask = null
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
            controlView.visibility = visibility
        }
    }

    fun setFullscreenHandler(fullscreenHandler: FullscreenHandler) {
        this.bitmovinPlayerView?.setFullscreenHandler(fullscreenHandler)
    }

    fun destroy() {
        removePlayerListener()
        uiHideTimer?.cancel()
    }

    fun onStart() {
        this.bitmovinPlayerView?.onStart()
    }

    fun onResume() {
        this.bitmovinPlayerView?.onResume()
    }

    fun onPause() {
        this.bitmovinPlayerView?.onPause()
    }

    fun onStop() {
        this.bitmovinPlayerView?.onStop()
    }

    fun onDestroy() {
        this.bitmovinPlayerView?.onDestroy()
        destroy()
    }

    /**
     * UI Listeners
     */

    override fun onTouchEvent(event: MotionEvent): Boolean = true

    private val seekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            // Only seek/timeShift when the user changes the progress (and not the TimeChangedEvent)
            if (fromUser) {
                // If the current stream is a live stream, we have to use the timeShift method
                bitmovinPlayer?.let { player ->
                    if (!player.isLive) {
                        player.seek(progress / 1000.0)
                    } else {
                        player.timeShift((progress - seekBar.max) / 1000.0)
                    }
                }
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {}

        override fun onStopTrackingTouch(seekBar: SeekBar) {}
    }

    private val onClickListener = OnClickListener { view ->
        if (view === playButton || view === this@PlayerUI) {
            bitmovinPlayer?.let { player ->
                if (player.isPlaying) {
                    player.pause()
                } else {
                    player.play()
                }
            }
        }
        if (view === fullscreenButton) {
            bitmovinPlayerView?.let { playerView ->
                if (playerView.isFullscreen) {
                    playerView.exitFullscreen()
                } else {
                    playerView.enterFullscreen()
                }
            }
        }
    }

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

    /**
     * Player Listeners
     */

    private val onTimeChangedListener = OnTimeChangedListener { updateUi() }

    private val onSourceLoadedListener = OnSourceLoadedListener { updateUi() }

    private val onPlaybackFinishedListener = OnPlaybackFinishedListener { updateUi() }

    private val onPausedListener = OnPausedListener { updateUi() }

    private val onPlayListener = OnPlayListener { updateUi() }

    private val onSeekedListener = OnSeekedListener { updateUi() }

    private val onStallEndedListener = OnStallEndedListener { updateUi() }

    /**
     * Methods for UI update
     */

    private fun updateUi() {
        seekbar.post {
            // if the live state of the player changed, the UI should change it's mode
            bitmovinPlayer?.let { player ->
                val positionMs: Int
                val durationMs: Int

                if (live != player.isLive) {
                    live = player.isLive
                    if (live) {
                        positionView.visibility = View.GONE
                        durationView.text = LIVE
                    } else {
                        positionView.visibility = View.VISIBLE
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
                    positionView.text = millisecondsToTimeString(positionMs)
                    durationView.text = millisecondsToTimeString(durationMs)
                }

                // Update the values of the Seekbar
                seekbar.progress = positionMs
                seekbar.max = durationMs

                // Update the image of the playback button
                if (player.isPlaying) {
                    playButton.setImageDrawable(pauseDrawable)
                } else {
                    playButton.setImageDrawable(playDrawable)
                }
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
