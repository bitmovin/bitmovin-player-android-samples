package com.bitmovin.player.samples.offline.playback

import com.bitmovin.player.api.offline.OfflineContentManager
import com.bitmovin.player.api.offline.options.OfflineContentOptions
import com.bitmovin.player.api.source.SourceConfig

/**
 * A class representing a ListItem
 */
data class ListItem(val sourceConfig: SourceConfig, val offlineContentManager: OfflineContentManager) {
    var offlineContentOptions: OfflineContentOptions? = null
    var progress: Float = 0f
}
