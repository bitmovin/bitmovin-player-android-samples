/*
 * Bitmovin Player Android SDK
 * Copyright (C) 2017, Bitmovin GmbH, All Rights Reserved
 *
 * This source code and its use and distribution, is subject to the terms
 * and conditions of the applicable license agreement.
 */

package com.bitmovin.player.samples.offline.playback;

public interface ListItemActionListener
{
    void showSelectionDialog(ListItem listItem);

    void delete(ListItem listItem);

    void suspend(ListItem listItem);

    void resume(ListItem listItem);
}
