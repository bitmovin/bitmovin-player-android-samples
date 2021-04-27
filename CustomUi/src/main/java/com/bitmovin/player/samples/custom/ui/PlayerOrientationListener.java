package com.bitmovin.player.samples.custom.ui;

import android.app.Activity;
import android.view.OrientationEventListener;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;

public class PlayerOrientationListener extends OrientationEventListener {
    private static final int ROTATION_THRESHOLD = 5;
    private final Activity activity;

    public PlayerOrientationListener(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    public void onOrientationChanged(int orientation) {
        if (Math.abs(orientation - 0) < ROTATION_THRESHOLD) {
            this.activity.setRequestedOrientation(SCREEN_ORIENTATION_PORTRAIT);
        } else if (Math.abs(orientation - 90) < ROTATION_THRESHOLD) {
            this.activity.setRequestedOrientation(SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        } else if (Math.abs(orientation - 180) < ROTATION_THRESHOLD) {
            this.activity.setRequestedOrientation(SCREEN_ORIENTATION_REVERSE_PORTRAIT);
        } else if (Math.abs(orientation - 270) < ROTATION_THRESHOLD) {
            this.activity.setRequestedOrientation(SCREEN_ORIENTATION_LANDSCAPE);
        }
    }
}
