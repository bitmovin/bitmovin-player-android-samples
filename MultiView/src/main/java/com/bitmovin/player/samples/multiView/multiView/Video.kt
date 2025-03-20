package com.bitmovin.player.samples.multiView.multiView

import com.bitmovin.player.api.source.SourceConfig

data class Video(
    val id: String,
    val source: SourceConfig,
    val posterUrl: String,
)
