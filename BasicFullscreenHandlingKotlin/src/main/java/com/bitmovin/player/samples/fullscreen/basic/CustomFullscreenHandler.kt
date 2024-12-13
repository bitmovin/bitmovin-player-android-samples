package com.bitmovin.player.samples.fullscreen.basic

import android.app.Activity
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.KeyCharacterMap
import androidx.appcompat.widget.Toolbar
import android.view.View
import android.view.ViewGroup
import com.bitmovin.player.PlayerView
import com.bitmovin.player.api.ui.FullscreenHandler


class CustomFullscreenHandler(
    activity: Activity,
    private val playerView: PlayerView,
    private val toolbar: Toolbar?
) : FullscreenHandler {
    override var isFullscreen = false
    private var decorView: View? = activity.window.decorView
    private val playerOrientationListener = PlayerOrientationListener(activity).apply { enable() }

    private fun handleFullscreen(fullscreen: Boolean) {
        isFullscreen = fullscreen
        doSystemUiVisibility(fullscreen)
        doLayoutChanges()
    }

    private fun doSystemUiVisibility(fullScreen: Boolean) {
        decorView?.post {
            val uiParams = getSystemUiVisibilityFlags(fullScreen)
            decorView?.systemUiVisibility = uiParams
        }
    }

    private fun doLayoutChanges() {
        val mainLooper = Looper.getMainLooper()
        val isAlreadyMainLooper = Looper.myLooper() == mainLooper

        if (isAlreadyMainLooper) {
            updateLayout()
        } else {
            val handler = Handler(mainLooper)
            handler.post(::updateLayout)
        }
    }

    private fun updateLayout() {
        val parentView = playerView.parent
        toolbar?.visibility = if (isFullscreen) View.GONE else View.VISIBLE

        if (parentView !is ViewGroup) return

        for (i in 0 until parentView.childCount) {
            parentView
                .getChildAt(i)
                .takeIf { it !== playerView }
                ?.visibility = if (this.isFullscreen) View.GONE else View.VISIBLE
        }
    }

    override fun onFullscreenRequested() = handleFullscreen(true)

    override fun onFullscreenExitRequested() = handleFullscreen(false)

    override fun onResume() {
        if (isFullscreen) {
            doSystemUiVisibility(isFullscreen)
        }
    }

    override fun onPause() {}

    override fun onDestroy() = playerOrientationListener.disable()
}

fun getSystemUiVisibilityFlags(fullScreen: Boolean): Int = if (!fullScreen) {
    View.SYSTEM_UI_FLAG_VISIBLE
} else {
    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
}