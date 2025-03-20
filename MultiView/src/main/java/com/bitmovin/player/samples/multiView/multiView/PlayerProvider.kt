package com.bitmovin.player.samples.multiView.multiView

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.bitmovin.player.api.PlaybackConfig
import com.bitmovin.player.api.Player
import com.bitmovin.player.api.PlayerConfig
import com.bitmovin.player.api.buffer.BufferConfig
import com.bitmovin.player.api.buffer.BufferMediaTypeConfig
import com.bitmovin.player.api.buffer.BufferType
import com.bitmovin.player.api.deficiency.PlayerErrorCode
import com.bitmovin.player.api.deficiency.exception.LicenseKeyMissingException
import com.bitmovin.player.api.event.PlayerEvent
import com.bitmovin.player.api.event.next
import com.bitmovin.player.api.event.on
import com.bitmovin.player.api.media.AdaptationConfig
import kotlin.coroutines.resume
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext

class MultiViewPlayerProvider(
    private val context: Context,
    lifecycle: Lifecycle,
    maxPlayerInstances: Int = 5,
) {
    private val _playerCache = MutableStateFlow(PlayerCache(maxPlayerInstances))
    val playerCache = _playerCache.asStateFlow()

    private val players get() = playerCache.value.values

    init {
        addPlayerLifecycleHandling(lifecycle)
    }

    suspend fun createPlayer(video: Video): Result<Player> {
        if (_playerCache.value.containsKey(video)) {
            return Result.success(_playerCache.value[video]!!)
        }
        val player = createPlayerAndConfigurePlayer(video)

        return if (player != null) {
            _playerCache.update { it.update { put(video, player) } }
            Result.success(player)
        } else {
            Result.failure(Exception("Failed to create player"))
        }
    }

    fun removePlayer(video: Video) {
        val player = _playerCache.value[video] ?: return
        player.destroy()
        _playerCache.update { it.update { remove(video) } }
    }

    fun getPlayer(video: Video): Player? = _playerCache.value[video]

    fun releaseAndClearPlayers() = _playerCache
        .getAndUpdate { it.update { clear() } }
        .forEach { (_, player) ->
            player.onStop()
            player.destroy()
        }

    /**
     * Creates a player and suspends until the first frame is rendered.
     * In case any other errors occur, the player is destroyed and null is returned.
     */
    private suspend fun createPlayerAndConfigurePlayer(video: Video): Player? {
        return withContext(Dispatchers.Main) {
            val player = Player(
                context,
                playerConfig = PlayerConfig(
                    playbackConfig = PlaybackConfig(
                        isMuted = true,
                        isAutoplayEnabled = true,
                    ),
                    adaptationConfig = AdaptationConfig(initialBandwidthEstimateOverride = 100),
                    bufferConfig = BufferConfig(
                        audioAndVideo = BufferMediaTypeConfig(forwardDuration = 5.0),
                        startupThreshold = 0.5,
                        restartThreshold = 2.0,
                    )
                )
            )
            player.on<PlayerEvent.PlaybackFinished> {
                uiHandler.post { player.play() }
            }
            val loadedSuccessfully = player.loadWaitingForPlayback(video)
            if (loadedSuccessfully) {
                player.buffer.setTargetLevel(BufferType.ForwardDuration, 10.0)
                player
            } else {
                player.destroy()
                null
            }
        }
    }

    private suspend fun Player.loadWaitingForPlayback(
        video: Video
    ) = suspendCancellableCoroutine { continuation ->
        val player = this
        val continueWithSuccess: (PlayerEvent.Playing) -> Unit = {
            continuation.resume(true)
        }
        val continueWithFailure: (PlayerEvent.Error) -> Unit = {
            it.toastErrorIfLicenceRelated(context)
            continuation.resume(false)
        }

        player.next<PlayerEvent.Playing>(continueWithSuccess)
        player.next<PlayerEvent.Error>(continueWithFailure)
        player.load(video.source)

        continuation.invokeOnCancellation {
            player.destroy()
        }
    }

    private fun addPlayerLifecycleHandling(lifecycle: Lifecycle) {
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onPause(owner: LifecycleOwner) {
                players.forEach { player ->
                    player.onPause()
                    player.pause()
                }
            }

            override fun onResume(owner: LifecycleOwner) {
                players.forEach { player ->
                    player.onResume()
                    player.play()
                }
            }

            override fun onStart(owner: LifecycleOwner) {
                players.forEach { player ->
                    player.onStart()
                }
            }

            override fun onStop(owner: LifecycleOwner) {
                players.forEach { player ->
                    player.onStop()
                }
            }

            override fun onDestroy(owner: LifecycleOwner) {
                releaseAndClearPlayers()
            }
        })
    }

    companion object {
        private val uiHandler = Handler(Looper.getMainLooper())
    }
}

class PlayerCache(
    private val maxPlayerCount: Int
) : LinkedHashMap<Video, Player>(), MutableMap<Video, Player> {
    override fun removeEldestEntry(eldest: MutableMap.MutableEntry<Video, Player>?): Boolean {
        val shouldRemoveEntry = size > maxPlayerCount
        if (shouldRemoveEntry) {
            eldest?.value?.destroy()
        }
        return shouldRemoveEntry
    }

    fun update(action: PlayerCache.() -> Unit): PlayerCache {
        val newCache = PlayerCache(maxPlayerCount)
        newCache.putAll(this)
        newCache.action()
        return newCache
    }
}

private val licenseErrorCodes = setOf(
    PlayerErrorCode.LicenseKeyNotFound,
    PlayerErrorCode.LicenseAuthenticationFailed,
)

private fun PlayerEvent.Error.toastErrorIfLicenceRelated(context: Context) {
    if (code in licenseErrorCodes) {
        val message = "A license key must be provided in the PlayerConfig or app manifest."
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        Log.e("PlayerProvider", message)
    }
}
