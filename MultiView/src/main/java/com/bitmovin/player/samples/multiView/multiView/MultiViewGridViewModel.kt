package com.bitmovin.player.samples.multiView.multiView

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bitmovin.player.api.Player
import kotlin.reflect.cast
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MultiViewGridViewModel(
    private val playerProvider: MultiViewPlayerProvider,
) : ViewModel() {
    private val _playerHolders = MutableStateFlow(listOf<PlayerHolder>())
    val playerHolders = _playerHolders.map {
        it.mapIndexed { index, playerHolder ->
            playerHolder.copy(
                isFocusedVideo = isFocusedVideo(index),
                shouldShowUi = shouldShowPlayerUi(index),
            )
        }
    }.stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptyList()
    )

    val gridConfig: GridConfig
        get() = createGridConfig(_playerHolders.value.size)

    init {
        viewModelScope.launch {
            // Player provider automatically removes and releases the oldest player if the max player count is reached.
            // Make sure to also remove it then from the player selection.
            playerProvider.playerCache.collect { playerMap ->
                _playerHolders.update {
                    it.filter { playerHolder -> playerHolder.isLoading || playerHolder.video in playerMap }
                }
            }
        }
    }

    fun swapFocusedVideo(video: Video) {
        val videos = _playerHolders.value.map { it.video }
        val index = videos
            .indexOf(video)
            .takeUnless { it == -1 }
            ?: return
        val focusedPlayerIndex = gridConfig.focusedVideoConfig?.index ?: return

        _playerHolders.value = _playerHolders.value.toMutableList().apply {
            val temp = this[index]
            this[index] = this[focusedPlayerIndex]
            this[focusedPlayerIndex] = temp
        }
    }

    fun addVideo(video: Video) {
        val existingPlayer = playerProvider.getPlayer(video)
        if (existingPlayer != null) {
            _playerHolders.update { it + PlayerHolder(video = video, player = existingPlayer) }
            return
        }
        // Player null signalizes that the player is being created
        _playerHolders.update { it + PlayerHolder(video = video, player = null) }

        viewModelScope.launch {
            val playerResult = playerProvider.createPlayer(video)
            if (playerResult.isSuccess) {
                _playerHolders.update {
                    it.map { playerHolder ->
                        if (playerHolder.video == video) {
                            playerHolder.copy(player = playerResult.getOrThrow())
                        } else {
                            playerHolder
                        }
                    }
                }
            } else {
                _playerHolders.update { it.filter { playerHolder -> playerHolder.video != video } }
                Log.e("MultiView", "Failed to create player")
            }
        }
    }

    fun removeVideo(video: Video) {
        if (containsVideo(video)) {
            _playerHolders.update { it.filter { playerHolder -> playerHolder.video != video } }
            playerProvider.removePlayer(video)
        }
    }

    fun containsVideo(video: Video) = _playerHolders.value.any { it.video == video }

    fun addOrRemoveVideo(video: Video) {
        if (containsVideo(video)) {
            removeVideo(video)
        } else {
            addVideo(video)
        }
    }

    private fun isFocusedVideo(index: Int) = index == gridConfig.focusedVideoConfig?.index

    private fun shouldShowPlayerUi(index: Int): Boolean {
        return !gridConfig.disableAllPlayersUi && (isFocusedVideo(index) || gridConfig.focusedVideoConfig == null)
    }

    private fun createGridConfig(playerCount: Int): GridConfig = when (playerCount) {
        1 -> GridConfig(
            mainAxisItemCount = 1,
            crossAxisItemCount = 1,
        )

        2 -> GridConfig(
            mainAxisItemCount = 2,
            crossAxisItemCount = 1,
        )

        3 -> GridConfig(
            mainAxisItemCount = 2,
            crossAxisItemCount = 2,
            focusedVideoConfig = FocusedVideoConfig(index = 0, mainAxisWeight = 1.4f),
        )

        4 -> GridConfig(
            mainAxisItemCount = 2,
            crossAxisItemCount = 3,
            focusedVideoConfig = FocusedVideoConfig(index = 0, mainAxisWeight = 1.4f),
        )

        5 -> GridConfig(
            mainAxisItemCount = 3,
            crossAxisItemCount = 2,
            focusedVideoConfig = FocusedVideoConfig(index = 2, mainAxisWeight = 1.4f),
        )

        else -> GridConfig(
            mainAxisItemCount = 3,
            crossAxisItemCount = 2,
            disableAllPlayersUi = true,
        )
    }

    override fun onCleared() = playerProvider.releaseAndClearPlayers()
}

data class PlayerHolder(
    val video: Video,
    val player: Player?,
    val isFocusedVideo: Boolean = false,
    val shouldShowUi: Boolean = false,
)

val PlayerHolder.isLoading: Boolean
    get() = player == null

/**
 * Main axis:
 * - For vertical grid top to bottom
 * - For horizontal grid start to end
 *
 * Cross axis:
 * - For vertical grid start to end
 * - For horizontal grid top to bottom
 */
data class GridConfig(
    val mainAxisItemCount: Int,
    val crossAxisItemCount: Int,
    val focusedVideoConfig: FocusedVideoConfig? = null,
    val maxMainAxisItemCount: Int = 3,
    val disableAllPlayersUi: Boolean = false,
) {
    init {
        require(mainAxisItemCount <= maxMainAxisItemCount)
        require(mainAxisItemCount > 0)
        require(crossAxisItemCount > 0)
    }
}

/**
 * A focused video represents a video that is rendered bigger than the others in the grid.
 * The current UI implementation only expects 1 focused video.
 */
data class FocusedVideoConfig(
    /**
     * The index of the focused video in the grid.
     */
    val index: Int,
    /**
     * The weight of the main axis of the focused video in the grid.
     * If this is set to 1.0f, the main axis will have the default even size,
     * but the cross axis will still span across the full line.
     */
    val mainAxisWeight: Float,
)

class MultiViewGridViewModelFactory(
    private val playerProvider: MultiViewPlayerProvider,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MultiViewGridViewModel(
            playerProvider,
        ).let(modelClass.kotlin::cast)
    }
}
