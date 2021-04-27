package com.bitmovin.player.samples.custom.ui.subtitleview

import android.app.AlertDialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import com.bitmovin.player.api.Player
import com.bitmovin.player.api.event.Event
import com.bitmovin.player.api.event.PlayerEvent
import com.bitmovin.player.api.event.SourceEvent
import kotlinx.android.synthetic.main.player_controls.view.*

private const val LIVE = "LIVE"

class PlayerControls : LinearLayout {
    private lateinit var player: Player

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
        LayoutInflater.from(context).inflate(R.layout.player_controls, this)

        playDrawable = ContextCompat.getDrawable(context, R.drawable.ic_play_arrow_black_24dp)
        pauseDrawable = ContextCompat.getDrawable(context, R.drawable.ic_pause_black_24dp)

        seekbar.setOnSeekBarChangeListener(seekBarChangeListener)
        playButton.setOnClickListener(onClickListener)
        subtitleButton.setOnClickListener(onClickListener)
    }

    fun setPlayer(player: Player) {
        this.player = player
        removePlayerListener()
        addPlayerListener()
    }

    private fun addPlayerListener() {
        player.on(PlayerEvent.TimeChanged::class, ::updateUi)
        player.on(SourceEvent.Loaded::class, ::updateUi)
        player.on(PlayerEvent.Play::class, ::updateUi)
        player.on(PlayerEvent.Paused::class, ::updateUi)
        player.on(PlayerEvent.StallEnded::class, ::updateUi)
        player.on(PlayerEvent.Seeked::class, ::updateUi)
        player.on(PlayerEvent.PlaybackFinished::class, ::updateUi)
        player.on(PlayerEvent.Ready::class, ::updateUi)
    }

    private fun removePlayerListener() {
        player.off(::updateUi)
    }

    private fun onSubtitleDialogButton() {
        val subtitleTracks = player.availableSubtitles
        val subtitleNames = subtitleTracks.map { track -> track.label }
        val listAdapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, subtitleNames)
        AlertDialog.Builder(context).setAdapter(listAdapter) { dialog, which ->
            player.setSubtitle(subtitleTracks[which].id)
            dialog.dismiss()
        }.show()
    }

    /**
     * UI Listeners
     */

    private val seekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            // Only seek/timeShift when the user changes the progress (and not the TimeChangedEvent)
            if (fromUser) {
                // If the current stream is a live stream, we have to use the timeShift method
                if (!player.isLive) {
                    player.seek(progress / 1000.0)
                } else {
                    player.timeShift((progress - seekBar.max) / 1000.0)
                }
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {}

        override fun onStopTrackingTouch(seekBar: SeekBar) {}
    }

    private val onClickListener = OnClickListener { view ->
        if (view === playButton) {
            if (player.isPlaying) {
                player.pause()
            } else {
                player.play()
            }
        } else if (view === subtitleButton) {
            onSubtitleDialogButton()
        }
    }

    /**
     * Methods for UI update
     */

    private fun updateUi(event: Event? = null) {
        seekbar.post {
            val positionMs: Int
            val durationMs: Int

            // if the live state of the player changed, the UI should change it's mode
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
