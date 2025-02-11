package com.bitmovin.player.samples.custom.ui

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.bitmovin.player.api.ui.FullscreenHandler

class CustomFullscreenHandler(
    private val activity: AppCompatActivity,
    root: View,
    private val playerUI: PlayerUI
) : FullscreenHandler {
    private val decorView: View = activity.window.decorView
    override var isFullscreen: Boolean = false
    private val playerOrientationListener = PlayerOrientationListener(activity)

    init {
        playerOrientationListener.enable()
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, windowInsets ->
            if (isFullscreen) {
                view.updatePadding(0, 0, 0, 0)
            } else {
                val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
                view.updatePadding(insets.left, insets.top, insets.right, insets.bottom)
            }
            WindowInsetsCompat.CONSUMED
        }
    }

    private fun handleFullscreen(fullscreen: Boolean) {
        isFullscreen = fullscreen

        doSystemUiVisibility(fullscreen)
        doLayoutChanges(fullscreen)
    }

    private fun doSystemUiVisibility(fullScreen: Boolean) {
        decorView.post {
            val uiParams = getSystemUiVisibilityFlags(fullScreen)
            decorView.systemUiVisibility = uiParams
        }
    }

    private fun doLayoutChanges(fullscreen: Boolean) {
        val actionBar = (activity as? AppCompatActivity)?.supportActionBar
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

internal fun getSystemUiVisibilityFlags(fullScreen: Boolean): Int = if (fullScreen) {
    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
} else {
    View.SYSTEM_UI_FLAG_VISIBLE
}