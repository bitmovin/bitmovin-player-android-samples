package com.bitmovin.player.samples.custom.ui

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.bitmovin.player.api.ui.FullscreenHandler
import com.bitmovin.player.ui.getSystemUiVisibilityFlags

class CustomFullscreenHandler(private val activity: Activity, private val playerUI: PlayerUI) : FullscreenHandler {
    private val decorView: View = activity.window.decorView
    override var isFullscreen: Boolean = false
    private val playerOrientationListener = PlayerOrientationListener(activity)

    init {
        playerOrientationListener.enable()
    }

    private fun handleFullscreen(fullscreen: Boolean) {
        isFullscreen = fullscreen

        doSystemUiVisibility(fullscreen)
        doLayoutChanges(fullscreen)
    }

    private fun doSystemUiVisibility(fullScreen: Boolean) {
        decorView.post {
            val uiParams = getSystemUiVisibilityFlags(fullScreen, true)
            decorView.systemUiVisibility = uiParams
        }
    }

    private fun doLayoutChanges(fullscreen: Boolean) {
        val actionBar = (activity as? AppCompatActivity)?.supportActionBar
        actionBar?.setShowHideAnimationEnabled(false)
        if (fullscreen) actionBar?.hide() else actionBar?.show()

        if (playerUI.parent is ViewGroup) {
            val parentView = playerUI.parent as ViewGroup

            (0 until parentView.childCount)
                    .map { parentView.getChildAt(it) }
                    .filter { it !== playerUI }
                    .forEach { it.visibility = if (fullscreen) View.GONE else View.VISIBLE }

            val params = playerUI.layoutParams.apply {
                width = ViewGroup.LayoutParams.MATCH_PARENT
                height = ViewGroup.LayoutParams.MATCH_PARENT
            }

            playerUI.layoutParams = params
            playerUI.setPadding(0, 0, 0, 0)
        }
    }

    override fun onFullscreenRequested() = handleFullscreen(true)

    override fun onFullscreenExitRequested() = handleFullscreen(false)

    override fun onResume() {
        if (isFullscreen) doSystemUiVisibility(isFullscreen)
    }

    override fun onPause() {}

    override fun onDestroy() = playerOrientationListener.disable()
}
