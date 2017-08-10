package com.bitmovin.player.samples.offline.playback;

import com.bitmovin.player.offline.options.OfflineContentOptions;
import com.bitmovin.player.offline.options.OfflineOptionEntry;

import java.util.ArrayList;
import java.util.List;

public class Util
{
    private Util()
    {
    }

    /**
     * Returns the video, audio and text options of the {@link OfflineContentOptions} in one list
     *
     * @param offlineContentOptions
     * @return
     */
    public static List<OfflineOptionEntry> getAsOneList(OfflineContentOptions offlineContentOptions)
    {
        List<OfflineOptionEntry> offlineOptionEntries = new ArrayList<OfflineOptionEntry>(offlineContentOptions.getVideoOptions());
        offlineOptionEntries.addAll(offlineContentOptions.getAudioOptions());
        offlineOptionEntries.addAll(offlineContentOptions.getTextOptions());
        return offlineOptionEntries;
    }
}
