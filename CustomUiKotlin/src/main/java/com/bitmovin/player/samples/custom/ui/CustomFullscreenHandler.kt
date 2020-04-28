package com.bitmovin.player.samples.custom.ui

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.bitmovin.player.ui.FullscreenHandler
import com.bitmovin.player.ui.getSystemUiVisibilityFlags

class CustomFullscreenHandler(private val activity: Activity, private val playerUI: PlayerUI) : FullscreenHandler {

    private val decorView: View = activity.window.decorView
    private var isFullscreen: Boolean = false
    private val playerOrientationListener = PlayerOrientationListener(activity)

    init {
        this.playerOrientationListener.enable()
    }

    private fun handleFullscreen(fullscreen: Boolean) {
        this.isFullscreen = fullscreen

        this.doSystemUiVisibility(fullscreen)
        this.doLayoutChanges(fullscreen)
    }

    private fun doSystemUiVisibility(fullScreen: Boolean) {
        this.decorView.post {
            val uiParams = getSystemUiVisibilityFlags(fullScreen, true)
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

        if (this.playerUI.parent is ViewGroup) {
            val parentView = this.playerUI.parent as ViewGroup

            for (i in 0 until parentView.childCount) {
                val child = parentView.getChildAt(i)
                if (child !== this.playerUI) {
                    child.visibility = if (fullscreen) View.GONE else View.VISIBLE
                }
            }

            val params = this.playerUI.layoutParams
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            params.height = ViewGroup.LayoutParams.MATCH_PARENT
            this.playerUI.layoutParams = params
            this.playerUI.setPadding(0, 0, 0, 0)
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
            this.doSystemUiVisibility(this.isFullscreen)
        }
    }

    override fun onPause() {}

    override fun onDestroy() {
        this.playerOrientationListener.disable()
    }

    override fun isFullScreen(): Boolean = this.isFullscreen
}
