package com.bitmovin.player.samples.offline.playback

import com.bitmovin.player.config.media.SourceItem
import com.bitmovin.player.offline.OfflineContentManager
import com.bitmovin.player.offline.options.OfflineContentOptions

/**
 * A class representing a ListItem
 */
data class ListItem(val sourceItem: SourceItem, val offlineContentManager: OfflineContentManager) {
    var offlineContentOptions: OfflineContentOptions? = null
    var progress: Float = 0f
}
