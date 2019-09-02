package com.bitmovin.player.samples.custom.ui.subtitleview

import android.app.AlertDialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.SeekBar
import com.bitmovin.player.BitmovinPlayer
import com.bitmovin.player.api.event.listener.*
import kotlinx.android.synthetic.main.player_controls.view.*

class PlayerControls : LinearLayout {

    private val LIVE = "LIVE"

    private var bitmovinPlayer: BitmovinPlayer? = null

    private var playDrawable: Drawable? = null
    private var pauseDrawable: Drawable? = null

    private var live: Boolean = false

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        setup()
    }

    constructor(context: Context) : super(context, null) {
        setup()
    }

    private fun setup() {
        LayoutInflater.from(this.context).inflate(R.layout.player_controls, this)

        this.playDrawable = ContextCompat.getDrawable(this.context, R.drawable.ic_play_arrow_black_24dp)
        this.pauseDrawable = ContextCompat.getDrawable(this.context, R.drawable.ic_pause_black_24dp)

        seekbar.setOnSeekBarChangeListener(this.seekBarChangeListener)
        this.playButton.setOnClickListener(this.onClickListener)
        this.subtitleButton.setOnClickListener(this.onClickListener)
    }

    fun setPlayer(bitmovinPlayer: BitmovinPlayer) {
        removePlayerListener()
        this.bitmovinPlayer = bitmovinPlayer
        addPlayerListener()
    }

    private fun addPlayerListener() {
        this.bitmovinPlayer?.addEventListener(onTimeChangedListener)
        this.bitmovinPlayer?.addEventListener(onSourceLoadedListener)
        this.bitmovinPlayer?.addEventListener(onPlayListener)
        this.bitmovinPlayer?.addEventListener(onPausedListener)
        this.bitmovinPlayer?.addEventListener(onStallEndedListener)
        this.bitmovinPlayer?.addEventListener(onSeekedListener)
        this.bitmovinPlayer?.addEventListener(onPlaybackFinishedListener)
        this.bitmovinPlayer?.addEventListener(onReadyListener)
    }

    private fun removePlayerListener() {
        this.bitmovinPlayer?.removeEventListener(onTimeChangedListener)
        this.bitmovinPlayer?.removeEventListener(onSourceLoadedListener)
        this.bitmovinPlayer?.removeEventListener(onPlayListener)
        this.bitmovinPlayer?.removeEventListener(onPausedListener)
        this.bitmovinPlayer?.removeEventListener(onStallEndedListener)
        this.bitmovinPlayer?.removeEventListener(onSeekedListener)
        this.bitmovinPlayer?.removeEventListener(onPlaybackFinishedListener)
        this.bitmovinPlayer?.removeEventListener(onReadyListener)
    }

    private fun onSubtitleDialogButton() {
        bitmovinPlayer?.let { player ->
            val subtitleTracks = player.availableSubtitles
            val subtitleNames = subtitleTracks.map { track -> track.label }
            val listAdapter = ArrayAdapter<String>(this.context, android.R.layout.simple_list_item_1, subtitleNames)
            AlertDialog.Builder(this.context).setAdapter(listAdapter) { dialog, which ->
                player.setSubtitle(subtitleTracks[which].id)
                dialog.dismiss()
            }.show()
        }
    }

    /**
     * UI Listeners
     */

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
        if (view === playButton) {
            bitmovinPlayer?.let { player ->
                if (player.isPlaying) {
                    player.pause()
                } else {
                    player.play()
                }
            }
        } else if (view === subtitleButton) {
            onSubtitleDialogButton()
        }
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

    private val onReadyListener = OnReadyListener { updateUi() }

    /**
     * Methods for UI update
     */

    private fun updateUi() {
        seekbar.post {
            bitmovinPlayer?.let { player ->
                val positionMs: Int
                val durationMs: Int

                // if the live state of the bitmovinPlayer changed, the UI should change it's mode
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
