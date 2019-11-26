package com.bitmovin.player.samples.custom.ui

import android.app.Activity
import android.content.pm.ActivityInfo.*
import android.view.OrientationEventListener
import kotlin.math.abs

class PlayerOrientationListener(private val activity: Activity) : OrientationEventListener(activity) {

    companion object {
        const val ROTATION_THRESHOLD = 5
    }

    override fun onOrientationChanged(orientation: Int) {
        when {
            abs(orientation - 0) < ROTATION_THRESHOLD -> {
                this.activity.requestedOrientation = SCREEN_ORIENTATION_PORTRAIT
            }
            abs(orientation - 90) < ROTATION_THRESHOLD -> {
                this.activity.requestedOrientation = SCREEN_ORIENTATION_REVERSE_LANDSCAPE
            }
            abs(orientation - 180) < ROTATION_THRESHOLD -> {
                this.activity.requestedOrientation = SCREEN_ORIENTATION_REVERSE_PORTRAIT
            }
            abs(orientation - 270) < ROTATION_THRESHOLD -> {
                this.activity.requestedOrientation = SCREEN_ORIENTATION_LANDSCAPE
            }
        }
    }
}
