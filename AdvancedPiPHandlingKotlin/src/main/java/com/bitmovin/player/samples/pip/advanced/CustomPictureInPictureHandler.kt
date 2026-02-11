package com.bitmovin.player.samples.pip.advanced

import android.app.Activity
import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Rect
import android.graphics.drawable.Icon
import android.os.Build
import android.util.Rational
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.bitmovin.player.PlayerView
import com.bitmovin.player.api.Player
import com.bitmovin.player.api.event.PlayerEvent
import com.bitmovin.player.api.ui.PictureInPictureHandler

/**
 * A custom [PictureInPictureHandler] that demonstrates:
 * - Play/pause remote actions shown as overlay controls in the PiP window
 * - Automatic PiP entry on API 31+ when the user navigates away while playing
 * - Smooth source rect hint transitions on API 31+
 * - Dynamic aspect ratio based on actual video dimensions
 */
class CustomPictureInPictureHandler(
    private val activity: Activity,
    private val player: Player,
    private val playerView: PlayerView,
) : PictureInPictureHandler {
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == ACTION_TOGGLE_PLAYBACK) {
                if (player.isPlaying) player.pause() else player.play()
            }
        }
    }
    private val onPlay: (PlayerEvent.Play) -> Unit = { updatePictureInPictureParams() }
    private val onPlaying: (PlayerEvent.Playing) -> Unit = { updatePictureInPictureParams() }
    private val onPaused: (PlayerEvent.Paused) -> Unit = { updatePictureInPictureParams() }
    private val onPlaybackFinished: (PlayerEvent.PlaybackFinished) -> Unit = { updatePictureInPictureParams() }

    init {
        player.on(PlayerEvent.Play::class, onPlay)
        player.on(PlayerEvent.Playing::class, onPlaying)
        player.on(PlayerEvent.Paused::class, onPaused)
        player.on(PlayerEvent.PlaybackFinished::class, onPlaybackFinished)
        registerReceiver()
    }

    override val isPictureInPicture: Boolean
        get() = isPictureInPictureAvailable && activity.isInPictureInPictureMode

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O)
    override val isPictureInPictureAvailable: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

    override fun enterPictureInPicture() {
        if (!isPictureInPictureAvailable) {
            return
        }

        if (isPictureInPicture) {
            return
        }

        activity.enterPictureInPictureMode(buildPictureInPictureParams())
    }

    override fun exitPictureInPicture() {
        if (!isPictureInPictureAvailable) {
            return
        }

        if (!isPictureInPicture) {
            return
        }

        val restoreIntent = Intent(this.activity, this.activity.javaClass)
        restoreIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        activity.startActivity(restoreIntent)
    }

    private fun updatePictureInPictureParams() {
        if (!isPictureInPictureAvailable) return
        activity.setPictureInPictureParams(buildPictureInPictureParams())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun buildPictureInPictureParams(): PictureInPictureParams {
        val builder = PictureInPictureParams.Builder()
            .setAspectRatio(getAspectRatio())
            .setActions(listOf(buildToggleAction()))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            builder.setAutoEnterEnabled(player.isPlaying)
        }
        builder.setSourceRectHint(getSourceRectHint())

        return builder.build()
    }

    private fun getAspectRatio(): Rational {
        val videoData = player.playbackVideoData
        return if (videoData != null && videoData.width > 0 && videoData.height > 0) {
            Rational(videoData.width, videoData.height)
        } else {
            Rational(16, 9)
        }
    }

    private fun getSourceRectHint(): Rect {
        val rect = Rect()
        playerView.getGlobalVisibleRect(rect)
        return rect
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun buildToggleAction(): RemoteAction {
        val iconRes = if (player.isPlaying) R.drawable.ic_pip_pause else R.drawable.ic_pip_play
        val titleRes = if (player.isPlaying) R.string.pip_action_pause else R.string.pip_action_play

        val icon = Icon.createWithResource(activity, iconRes)
        val title = activity.getString(titleRes)

        val intent = Intent(ACTION_TOGGLE_PLAYBACK).apply {
            setPackage(activity.packageName)
        }
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val pendingIntent = PendingIntent.getBroadcast(activity, 0, intent, flags)

        return RemoteAction(icon, title, title, pendingIntent)
    }

    private fun registerReceiver() {
        val filter = IntentFilter(ACTION_TOGGLE_PLAYBACK)
        ContextCompat.registerReceiver(
            activity,
            broadcastReceiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED,
        )
    }

    private fun unregisterReceiver() {
        activity.unregisterReceiver(broadcastReceiver)
    }

    fun dispose() {
        player.off(onPlay)
        player.off(onPlaying)
        player.off(onPaused)
        player.off(onPlaybackFinished)
        unregisterReceiver()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            activity.setPictureInPictureParams(
                PictureInPictureParams.Builder()
                    .setAutoEnterEnabled(false)
                    .build(),
            )
        }
    }
}

private const val ACTION_TOGGLE_PLAYBACK =
    "com.bitmovin.player.samples.pip.advanced.TOGGLE_PLAYBACK"
