package com.bitmovin.player.samples.offline.playback

import com.bitmovin.player.api.offline.options.OfflineContentOptions
import com.bitmovin.player.api.offline.options.OfflineOptionEntry
import java.util.*

object Util {

    /**
     * Returns the video, audio and text options of the [OfflineContentOptions] in one list
     *
     * @param offlineContentOptions
     * @return
     */
    fun getAsOneList(offlineContentOptions: OfflineContentOptions): List<OfflineOptionEntry> {
        val offlineOptionEntries = ArrayList<OfflineOptionEntry>(offlineContentOptions.videoOptions)
        offlineOptionEntries.addAll(offlineContentOptions.audioOptions)
        offlineOptionEntries.addAll(offlineContentOptions.textOptions)
        val thumbnailOfflineOptionEntry = offlineContentOptions.thumbnailOption
        if (thumbnailOfflineOptionEntry != null) {
            offlineOptionEntries.add(thumbnailOfflineOptionEntry)
        }
        return offlineOptionEntries
    }
}
