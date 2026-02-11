package com.bitmovin.player.samples.pip.advanced

import com.bitmovin.player.api.source.SourceType

data class VideoItem(
    val title: String,
    val description: String?,
    val url: String,
    val sourceType: SourceType,
)

internal object VideoCatalog {
    val items: List<VideoItem> = listOf(
        VideoItem(
            title = "Art of Motion",
            description = "DASH",
            url = "https://cdn.bitmovin.com/content/assets/MI201109210084/mpds/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.mpd",
            sourceType = SourceType.Dash,
        ),
        VideoItem(
            title = "Sintel",
            description = "DASH",
            url = "https://cdn.bitmovin.com/content/assets/sintel/sintel.mpd",
            sourceType = SourceType.Dash,
        ),
        VideoItem(
            title = "Big Buck Bunny",
            description = "DASH",
            url = "https://cdn.bitmovin.com/content/assets/bbb/stream.mpd",
            sourceType = SourceType.Dash,
        ),
        VideoItem(
            title = "Art of Motion",
            description = "HLS",
            url = "https://cdn.bitmovin.com/content/assets/art-of-motion-dash-hls-progressive/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8",
            sourceType = SourceType.Hls,
        ),
        VideoItem(
            title = "Sintel",
            description = "HLS",
            url = "https://cdn.bitmovin.com/content/assets/sintel/hls/playlist.m3u8",
            sourceType = SourceType.Hls,
        ),
        VideoItem(
            title = "Big Buck Bunny",
            description = "HLS",
            url = "https://cdn.bitmovin.com/content/assets/bbb/stream.m3u8",
            sourceType = SourceType.Hls,
        ),
    )
}
