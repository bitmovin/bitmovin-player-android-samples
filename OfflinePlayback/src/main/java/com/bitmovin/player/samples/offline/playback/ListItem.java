/*
 * Bitmovin Player Android SDK
 * Copyright (C) 2017, Bitmovin GmbH, All Rights Reserved
 *
 * This source code and its use and distribution, is subject to the terms
 * and conditions of the applicable license agreement.
 */

package com.bitmovin.player.samples.offline.playback;

import com.bitmovin.player.api.offline.OfflineContentManager;
import com.bitmovin.player.api.offline.options.OfflineContentOptions;
import com.bitmovin.player.api.source.SourceConfig;

/**
 * A class representing a ListItem
 */
class ListItem {
    private SourceConfig sourceConfig;
    private OfflineContentManager offlineContentManager;
    private OfflineContentOptions offlineContentOptions;
    private float progress;

    public ListItem(SourceConfig sourceConfig, OfflineContentManager offlineContentManager) {
        this.sourceConfig = sourceConfig;
        this.offlineContentManager = offlineContentManager;
    }

    public SourceConfig getSourceConfig() {
        return this.sourceConfig;
    }

    public OfflineContentManager getOfflineContentManager()
    {
        return this.offlineContentManager;
    }

    public OfflineContentOptions getOfflineContentOptions()
    {
        return this.offlineContentOptions;
    }

    public float getProgress()
    {
        return this.progress;
    }

    public void setOfflineContentOptions(OfflineContentOptions offlineContentOptions) {
        this.offlineContentOptions = offlineContentOptions;
    }

    public void setProgress(float progress)
    {
        this.progress = progress;
    }
}
