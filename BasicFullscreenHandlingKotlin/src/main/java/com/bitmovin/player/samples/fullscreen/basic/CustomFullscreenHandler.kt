package com.bitmovin.player.samples.fullscreen.basic

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Handler
import android.os.Looper
import android.support.v7.widget.Toolbar
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import com.bitmovin.player.BitmovinPlayerView
import com.bitmovin.player.ui.FullscreenHandler
import com.bitmovin.player.ui.FullscreenUtil

class CustomFullscreenHandler(
        private val activity: Activity,
        private val playerView: BitmovinPlayerView,
        private val toolbar: Toolbar?
) : FullscreenHandler {

    private var isFullscreen: Boolean = false
    private var decorView: View? = activity.window.decorView

    private fun handleFullscreen(fullscreen: Boolean) {
        this.isFullscreen = fullscreen
        this.doRotation(fullscreen)
        this.doSystemUiVisibility(fullscreen)
        this.doLayoutChanges(fullscreen)
    }

    private fun doRotation(fullScreen: Boolean) {
        val rotation = this.activity.windowManager.defaultDisplay.rotation

        if (fullScreen) {
            if (rotation == Surface.ROTATION_270) {
                this.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
            } else {
                this.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
        } else {
            this.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    private fun doSystemUiVisibility(fullScreen: Boolean) {
        this.decorView?.post {
            val uiParams = FullscreenUtil.getSystemUiVisibilityFlags(fullScreen, true)
            decorView?.systemUiVisibility = uiParams
        }
    }

    private fun doLayoutChanges(fullscreen: Boolean) {
        val mainLooper = Looper.getMainLooper()
        val isMainLooperAlready = Looper.myLooper() == mainLooper

        val updateLayoutRunnable = UpdateLayoutRunnable(fullscreen)

        if (isMainLooperAlready) {
            updateLayoutRunnable.run()
        } else {
            val handler = Handler(mainLooper)
            handler.post(updateLayoutRunnable)
        }
    }

    override fun onFullscreenRequested() {
        this.handleFullscreen(true)
    }

    override fun onFullscreenExitRequested() {
        this.handleFullscreen(false)
    }

    override fun onResume() {
        if (isFullscreen) {
            doSystemUiVisibility(isFullscreen)
        }
    }

    override fun onPause() {}

    override fun onDestroy() {}

    override fun isFullScreen(): Boolean {
        return isFullscreen
    }

    private inner class UpdateLayoutRunnable(private val fullscreen: Boolean) : Runnable {

        override fun run() {
            this@CustomFullscreenHandler.toolbar?.visibility = (if (this.fullscreen) View.GONE else View.VISIBLE)

            if (this@CustomFullscreenHandler.playerView.parent is ViewGroup) {
                val parentView = this@CustomFullscreenHandler.playerView.parent as ViewGroup

                for (i in 0 until parentView.childCount) {
                    val child = parentView.getChildAt(i)
                    if (child !== playerView) {
                        child.visibility = (if (fullscreen) View.GONE else View.VISIBLE)
                    }
                }
            }
        }
    }
}
