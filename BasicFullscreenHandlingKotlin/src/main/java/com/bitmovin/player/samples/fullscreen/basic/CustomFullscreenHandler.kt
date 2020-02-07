package com.bitmovin.player.samples.fullscreen.basic

import android.app.Activity
import android.os.Handler
import android.os.Looper
import androidx.appcompat.widget.Toolbar
import android.view.View
import android.view.ViewGroup
import com.bitmovin.player.BitmovinPlayerView
import com.bitmovin.player.ui.FullscreenHandler
import com.bitmovin.player.ui.getSystemUiVisibilityFlags


class CustomFullscreenHandler(
    activity: Activity,
    private val playerView: BitmovinPlayerView,
    private val toolbar: Toolbar?
) : FullscreenHandler {

    private var isFullscreen: Boolean = false
    private var decorView: View? = activity.window.decorView
    private val playerOrientationListener = PlayerOrientationListener(activity)

    private val updateLayoutRunnable = Runnable {

        this.toolbar?.visibility = if (this.isFullscreen) {
            View.GONE
        } else {
            View.VISIBLE
        }

        if (this.playerView.parent is ViewGroup) {
            val parentView = this.playerView.parent as ViewGroup

            for (i in 0 until parentView.childCount) {
                val child = parentView.getChildAt(i)
                if (child !== playerView) {
                    child.visibility = if (this.isFullscreen) {
                        View.GONE
                    } else {
                        View.VISIBLE
                    }
                }
            }
        }
    }

    init {
        this.playerOrientationListener.enable()
    }

    private fun handleFullscreen(fullscreen: Boolean) {
        this.isFullscreen = fullscreen
        this.doSystemUiVisibility(fullscreen)
        this.doLayoutChanges()
    }

    private fun doSystemUiVisibility(fullScreen: Boolean) {
        this.decorView?.post {
            val uiParams = getSystemUiVisibilityFlags(fullScreen, true)
            this.decorView?.systemUiVisibility = uiParams
        }
    }

    private fun doLayoutChanges() {
        val mainLooper = Looper.getMainLooper()
        val isMainLooperAlready = Looper.myLooper() == mainLooper

        if (isMainLooperAlready) {
            this.updateLayoutRunnable.run()
        } else {
            val handler = Handler(mainLooper)
            handler.post(this.updateLayoutRunnable)
        }
    }

    override fun onFullscreenRequested() {
        this.handleFullscreen(true)
    }

    override fun onFullscreenExitRequested() {
        this.handleFullscreen(false)
    }

    override fun onResume() {
        if (this.isFullscreen) {
            doSystemUiVisibility(this.isFullscreen)
        }
    }

    override fun onPause() {}

    override fun onDestroy() {
        this.playerOrientationListener.disable()
    }

    override fun isFullScreen(): Boolean = this.isFullscreen

}
