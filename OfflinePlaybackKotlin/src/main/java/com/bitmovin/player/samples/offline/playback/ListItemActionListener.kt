package com.bitmovin.player.samples.offline.playback

interface ListItemActionListener {
    fun showSelectionDialog(listItem: ListItem)

    fun delete(listItem: ListItem)

    fun suspend(listItem: ListItem)

    fun resume(listItem: ListItem)
}
