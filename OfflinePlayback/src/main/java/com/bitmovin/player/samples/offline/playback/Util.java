/*
 * Bitmovin Player Android SDK
 * Copyright (C) 2017, Bitmovin GmbH, All Rights Reserved
 *
 * This source code and its use and distribution, is subject to the terms
 * and conditions of the applicable license agreement.
 */

package com.bitmovin.player.samples.offline.playback;

import com.bitmovin.player.api.offline.options.OfflineContentOptions;
import com.bitmovin.player.api.offline.options.OfflineOptionEntry;
import com.bitmovin.player.api.offline.options.ThumbnailOfflineOptionEntry;

import java.util.ArrayList;
import java.util.List;

public class Util {
    private Util() { }

    /**
     * Returns the video, audio and text options of the {@link OfflineContentOptions} in one list
     *
     * @param offlineContentOptions
     * @return
     */
    public static List<OfflineOptionEntry> getAsOneList(OfflineContentOptions offlineContentOptions) {
        List<OfflineOptionEntry> offlineOptionEntries = new ArrayList<OfflineOptionEntry>(offlineContentOptions.getVideoOptions());
        offlineOptionEntries.addAll(offlineContentOptions.getAudioOptions());
        offlineOptionEntries.addAll(offlineContentOptions.getTextOptions());
        ThumbnailOfflineOptionEntry thumbnailOfflineOptionEntry = offlineContentOptions.getThumbnailOption();
        if (thumbnailOfflineOptionEntry != null) {
            offlineOptionEntries.add(thumbnailOfflineOptionEntry);
        }
        return offlineOptionEntries;
    }
}
