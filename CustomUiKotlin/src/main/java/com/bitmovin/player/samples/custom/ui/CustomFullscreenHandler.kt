package com.bitmovin.player.samples.custom.ui

import android.app.Activity
import android.content.pm.ActivityInfo
import android.support.v7.app.AppCompatActivity
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import com.bitmovin.player.ui.FullscreenHandler
import com.bitmovin.player.ui.FullscreenUtil

class CustomFullscreenHandler(private val activity: Activity, private val playerUI: PlayerUI?) : FullscreenHandler {

    private val decorView: View = activity.window.decorView
    private var isFullscreen: Boolean = false

    private fun handleFullscreen(fullscreen: Boolean) {
        this.isFullscreen = fullscreen

        this.doRotation(fullscreen)
        this.doSystemUiVisibility(fullscreen)
        this.doLayoutChanges(fullscreen)
    }

    private fun doRotation(fullScreen: Boolean) {
        val rotation = this.activity.windowManager.defaultDisplay.rotation

        playerUI?.setVisible(false)

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
        this.decorView.post {
            val uiParams = FullscreenUtil.getSystemUiVisibilityFlags(fullScreen, true)
            decorView.systemUiVisibility = uiParams
        }
    }

    private fun doLayoutChanges(fullscreen: Boolean) {
        val actionBar = (this.activity as? AppCompatActivity)?.supportActionBar
        actionBar?.setShowHideAnimationEnabled(false)
        if (fullscreen) {
            actionBar?.hide()
        } else {
            actionBar?.show()
        }

        if (this.playerUI?.parent is ViewGroup) {
            val parentView = this.playerUI.parent as ViewGroup

            for (i in 0 until parentView.childCount) {
                val child = parentView.getChildAt(i)
                if (child !== playerUI) {
                    child.visibility = if (fullscreen) View.GONE else View.VISIBLE
                }
            }

            val params = playerUI.layoutParams
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            params.height = ViewGroup.LayoutParams.MATCH_PARENT
            playerUI.layoutParams = params
            playerUI.setPadding(0, 0, 0, 0)
        }

        playerUI?.postDelayed({ playerUI.setVisible(true) }, 600L)
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
}
