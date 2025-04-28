package com.bitmovin.player.samples.multiView.multiView

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.bitmovin.player.api.ui.PlayerViewConfig
import com.bitmovin.player.api.ui.PlayerViewConfig.PlayerVideoQualityConstraint.RelaxedViewport
import com.bitmovin.player.api.ui.ScalingMode
import com.bitmovin.player.api.ui.SurfaceType
import com.bitmovin.player.samples.multiView.ui.components.PlayerView

@Composable
fun MultiViewPlayerGrid(
    viewModel: MultiViewGridViewModel,
) {
    val playerHolders = viewModel.playerHolders.collectAsState().value
    val gridConfig = remember(key1 = playerHolders) { viewModel.gridConfig }
    val orientation = LocalConfiguration.current.orientation.toOrientation()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .padding(gridSpacing)
    ) {
        MultiViewStaggeredGrid(
            gridConfig = gridConfig,
            orientation = orientation,
        ) {
            items(
                playerHolders,
                key = { item -> item.video.id },
                span = { item ->
                    if (item.isFocusedVideo) StaggeredGridItemSpan.FullLine else StaggeredGridItemSpan.SingleLane
                }
            ) { playerHolder ->
                Box(
                    modifier = Modifier
                        .gridConfig(
                            gridConfig,
                            playerHolder.isFocusedVideo,
                            constraintsScope = this@BoxWithConstraints,
                            orientation = orientation,
                        )
                        .clip(cornerShape)
                        .background(Color.LightGray, shape = cornerShape)
                        .animateItem()
                        .clickable(enabled = !playerHolder.shouldShowUi) {
                            viewModel.swapFocusedVideo(playerHolder.video)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    val player = playerHolder.player
                    PlayerView(
                        modifier = Modifier
                            .fillMaxSize()
                            .animateItem(),
                        player = player,
                        playerViewConfig = PlayerViewConfig(
                            scalingMode = ScalingMode.Zoom,
                            surfaceType = SurfaceType.TextureView,
                            playerMaxVideoQualityConstraint = RelaxedViewport,
                        ),
                        isUiVisible = playerHolder.shouldShowUi,
                    )
                    AnimatedVisibility(playerHolder.isLoading, enter = fadeIn(), exit = fadeOut()) {
                        Box(Modifier.fillMaxSize()) {
                            AsyncImage(
                                model = playerHolder.video.posterUrl,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(cornerShape),
                            )
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center),
                                color = Color.White,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MultiViewStaggeredGrid(
    gridConfig: GridConfig,
    orientation: ScreenOrientation,
    content: LazyStaggeredGridScope.() -> Unit,
) {
    when (orientation) {
        ScreenOrientation.Landscape -> LazyHorizontalStaggeredGrid(
            verticalArrangement = Arrangement.spacedBy(gridSpacing),
            horizontalItemSpacing = gridSpacing,
            userScrollEnabled = false,
            rows = StaggeredGridCells.Fixed(gridConfig.crossAxisItemCount),
            content = content
        )

        ScreenOrientation.Portrait -> LazyVerticalStaggeredGrid(
            horizontalArrangement = Arrangement.spacedBy(gridSpacing),
            verticalItemSpacing = gridSpacing,
            userScrollEnabled = false,
            columns = StaggeredGridCells.Fixed(gridConfig.crossAxisItemCount),
            content = content
        )
    }
}

private fun Modifier.gridConfig(
    gridConfig: GridConfig,
    isFocusedVideo: Boolean,
    constraintsScope: BoxWithConstraintsScope,
    orientation: ScreenOrientation,
): Modifier = run {
    when (orientation) {
        ScreenOrientation.Landscape -> width(
            calculateMainAxisItemSize(
                gridConfig,
                isFocusedVideo,
                constraintsScope,
                orientation,
            )
        )

        ScreenOrientation.Portrait -> height(
            calculateMainAxisItemSize(
                gridConfig,
                isFocusedVideo,
                constraintsScope,
                orientation,
            )
        )
    }
}

// As we are using a LazyStaggeredGrid which is a scrollable component, we have to calculate the
// main axis size, so that all the items fit the available space without the need for scrolling.
// The calculation accounts for spacing and assumes only 1 focused video with the provided weight.
private fun calculateMainAxisItemSize(
    gridConfig: GridConfig,
    isFocusedVideo: Boolean,
    constraintsScope: BoxWithConstraintsScope,
    orientation: ScreenOrientation,
): Dp {
    val maxSize = when (orientation) {
        ScreenOrientation.Landscape -> constraintsScope.maxWidth
        ScreenOrientation.Portrait -> constraintsScope.maxHeight
    }

    val evenSize = (maxSize / gridConfig.mainAxisItemCount)
    val focusedVideoConfig = gridConfig.focusedVideoConfig ?: return evenSize - gridSpacing

    val focusedVideoCount = 1
    val focusedVideoSize = evenSize * focusedVideoConfig.mainAxisWeight
    val videoSize =
        (maxSize - focusedVideoSize) / (gridConfig.mainAxisItemCount - focusedVideoCount)
    val size = if (isFocusedVideo) focusedVideoSize else videoSize

    return size - gridSpacing
}

private enum class ScreenOrientation {
    Landscape,
    Portrait
}

private fun Int.toOrientation(): ScreenOrientation = when (this) {
    Configuration.ORIENTATION_LANDSCAPE -> ScreenOrientation.Landscape
    else -> ScreenOrientation.Portrait
}

private val cornerShape = RoundedCornerShape(8.dp)
private val gridSpacing = 12.dp
