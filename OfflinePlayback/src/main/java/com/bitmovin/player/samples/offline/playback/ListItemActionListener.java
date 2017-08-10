package com.bitmovin.player.samples.offline.playback;

public interface ListItemActionListener
{
    void showSelectionDialog(ListItem listItem);

    void delete(ListItem listItem);
}
