package com.bitmovin.player.samples.multiView

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.bitmovin.player.samples.multiView.multiView.MultiViewGridViewModel
import com.bitmovin.player.samples.multiView.multiView.MultiViewGridViewModelFactory
import com.bitmovin.player.samples.multiView.multiView.MultiViewPlayerGrid
import com.bitmovin.player.samples.multiView.multiView.MultiViewPlayerProvider
import com.bitmovin.player.samples.multiView.multiView.Video

@Composable
fun MultiViewScreen() {
    val multiViewGridViewModel = viewModel<MultiViewGridViewModel>(
        factory = MultiViewGridViewModelFactory(
            playerProvider = MultiViewPlayerProvider(
                context = LocalContext.current,
                lifecycle = LocalLifecycleOwner.current.lifecycle,
                maxPlayerInstances = 5
            )
        )
    )

    var showVideoSelection by remember { mutableStateOf(false) }
    val selectedVideos =
        multiViewGridViewModel.playerHolders.collectAsState().value.map { it.video }.toSet()

    Scaffold(
        bottomBar = {
            Column(Modifier.fillMaxWidth()) {
                Box(
                    Modifier
                        .align(Alignment.End)
                        .padding(end = gridSpacing)
                ) {
                    TextButton(
                        onClick = { showVideoSelection = !showVideoSelection },
                    ) {
                        Icon(Icons.Default.Menu, contentDescription = null)
                        Spacer(modifier = Modifier.size(8.dp))
                        Text("Select Videos")
                    }
                }
                AnimatedVisibility(showVideoSelection) {
                    VideoSelection(multiViewGridViewModel, selectedVideos)
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding())
        ) {
            if (selectedVideos.isNotEmpty()) {
                MultiViewPlayerGrid(multiViewGridViewModel)
            } else {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = "Select a video to play",
                    fontSize = 26.sp
                )
            }
        }
    }
}

@Composable
private fun VideoSelection(
    viewModel: MultiViewGridViewModel,
    selectedVideos: Set<Video>
) {
    LazyRow(
        contentPadding = PaddingValues(gridSpacing),
        horizontalArrangement = Arrangement.spacedBy(gridSpacing)
    ) {
        items(allVideos) { video ->
            Box(
                modifier = Modifier
                    .width(160.dp)
                    .height(80.dp)
                    .clip(cornerShape)
                    .background(Color.LightGray, shape = cornerShape)
                    .animateItem()
                    .background(MaterialTheme.colorScheme.primary, shape = cornerShape)
                    .clickable {
                        viewModel.addOrRemoveVideo(video)
                    },
                contentAlignment = Alignment.Center
            ) {
                Box {
                    AsyncImage(
                        model = video.posterUrl,
                        contentDescription = "Video poster",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    if (video in selectedVideos) {
                        Icon(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp),
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = Color.White,
                        )
                    }
                }
            }
        }
    }
}

private val cornerShape = RoundedCornerShape(8.dp)
private val gridSpacing = 12.dp
