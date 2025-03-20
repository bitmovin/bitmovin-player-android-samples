package com.bitmovin.player.samples.multiView.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.DisposableEffectScope
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.bitmovin.player.PlayerView
import com.bitmovin.player.api.Player
import com.bitmovin.player.api.ui.PlayerViewConfig
import com.bitmovin.player.api.ui.UiConfig
import com.bitmovin.player.ui.CustomMessageHandler

/** The [player] is not owned by the view. [Player.destroy] will _not_ be called when this view is destroyed. */
@Composable
fun PlayerView(
    modifier: Modifier = Modifier,
    player: Player?,
    customMessageHandler: CustomMessageHandler? = null,
    playerViewLifecycleHandler: PlayerViewLifecycleHandler = PlayerViewLifecycleHandler(),
    playerViewConfig: PlayerViewConfig = PlayerViewConfig(),
    isUiVisible: Boolean = playerViewConfig.uiConfig is UiConfig.WebUi,
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    /**
     * A list of lifecycle observers handling the `PlayerView` lifecycle.
     * This should always contain a single observer based on the assumption that [DisposableEffectScope.onDispose]
     * executes before [AndroidView]'s `factory`. A list data structure was chosen to avoid not removing the observers
     * in case this does not hold.
     */
    val observers = remember { mutableListOf<DefaultLifecycleObserver>() }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            val playerView = PlayerView(context, player, playerViewConfig)
            playerView.setCustomMessageHandler(customMessageHandler)

            val observer = object : DefaultLifecycleObserver {
                override fun onStart(owner: LifecycleOwner) = playerViewLifecycleHandler.onStart(playerView)
                override fun onResume(owner: LifecycleOwner) = playerViewLifecycleHandler.onResume(playerView)
                override fun onPause(owner: LifecycleOwner) = playerViewLifecycleHandler.onPause(playerView)
                override fun onStop(owner: LifecycleOwner) = playerViewLifecycleHandler.onStop(playerView)
                override fun onDestroy(owner: LifecycleOwner) {
                    // Do not destroy the player in `onDestroy` as the player lifecycle is handled outside
                    // of the composable. This is achieved by setting the player to `null` before destroying.
                    playerView.player = null
                    playerViewLifecycleHandler.onDestroy(playerView)
                }
            }
            lifecycle.addObserver(observer)
            observers.add(observer)

            return@AndroidView playerView
        },
        update = { view ->
            // Since player holds all the state, we need to make sure that it's set on the view when it gets updated.
            // Otherwise e.g replacing the player instance on PlayerView won't have an effect.
            view.player = player
            view.isUiVisible = isUiVisible
        }
    )

    DisposableEffect(lifecycle) {
        onDispose {
            observers.forEach { lifecycle.removeObserver(it) }
            observers.clear()
        }
    }
}
