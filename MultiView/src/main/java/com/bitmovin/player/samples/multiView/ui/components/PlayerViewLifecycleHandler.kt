package com.bitmovin.player.samples.multiView.ui.components

import com.bitmovin.player.PlayerView

open class PlayerViewLifecycleHandler {
    open fun onStart(playerView: PlayerView) = playerView.onStart()
    open fun onResume(playerView: PlayerView) = playerView.onResume()
    open fun onPause(playerView: PlayerView) = playerView.onPause()
    open fun onStop(playerView: PlayerView) = playerView.onStop()
    open fun onDestroy(playerView: PlayerView) = playerView.onDestroy()
}
