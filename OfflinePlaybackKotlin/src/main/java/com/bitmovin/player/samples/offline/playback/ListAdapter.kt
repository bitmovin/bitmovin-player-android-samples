package com.bitmovin.player.samples.offline.playback

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.LayoutRes
import com.bitmovin.player.api.offline.options.OfflineContentOptions
import com.bitmovin.player.api.offline.options.OfflineOptionEntryState
import kotlinx.android.synthetic.main.list_item.view.*

class ListAdapter(
        context: Context,
        @LayoutRes resource: Int,
        objects: List<ListItem>,
        private val listItemActionListener: ListItemActionListener
) : ArrayAdapter<ListItem>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var view = convertView

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
        }
        val listItem = getItem(position)

        val sourceConfig = listItem?.sourceConfig
        val offlineContentOptions = listItem?.offlineContentOptions
        view?.title?.text = sourceConfig?.title

        if (offlineContentOptions != null) {
            view?.btnDownload?.visibility = View.VISIBLE
            view?.btnDownload?.setOnClickListener { listItemActionListener.showSelectionDialog(listItem) }

            // If any option is downloading, show a progress in the list
            when {
                isDownloading(offlineContentOptions) -> {
                    view?.state?.text = String.format("Downloading - %.0f %%", listItem.progress)
                    view?.state?.visibility = View.VISIBLE
                    view?.btnPauseResume?.setImageResource(R.drawable.ic_pause_black_24dp)
                    view?.btnPauseResume?.visibility = View.VISIBLE
                    view?.btnPauseResume?.setOnClickListener { listItemActionListener.suspend(listItem) }
                }
                isSuspended(offlineContentOptions) -> {
                    view?.btnPauseResume?.setImageResource(R.drawable.ic_play_arrow_black_24dp)
                    view?.btnPauseResume?.visibility = View.VISIBLE
                    view?.btnPauseResume?.setOnClickListener { listItemActionListener.resume(listItem) }
                }
                hasFailed(offlineContentOptions) -> {
                    view?.state?.text = String.format("Failed - %.0f %%", listItem.progress)
                    view?.state?.visibility = View.VISIBLE
                    view?.btnPauseResume?.setImageResource(R.drawable.ic_play_arrow_black_24dp)
                    view?.btnPauseResume?.visibility = View.VISIBLE
                    view?.btnPauseResume?.setOnClickListener { listItemActionListener.resume(listItem) }
                }
                else -> {
                    view?.state?.visibility = View.INVISIBLE
                    view?.btnPauseResume?.visibility = View.INVISIBLE
                }
            }
            // If any option is downloaded, we show the delete button
            if (hasDownloaded(offlineContentOptions)) {
                view?.btnDelete?.visibility = View.VISIBLE
                view?.btnDelete?.setOnClickListener { listItemActionListener.delete(listItem) }
            } else {
                view?.btnDelete?.visibility = View.GONE
            }

        } else {
            // If no options are available, we hide the download and the delete button
            view?.btnDelete?.visibility = View.GONE
            view?.btnDownload?.visibility = View.GONE
        }

        return view
    }

    /**
     * Returns true, if one [OfflineOptionEntry]s state is [OfflineOptionEntryState.Downloading]
     *
     * @param offlineContentOptions
     * @return true, if one [OfflineOptionEntry]s state is [OfflineOptionEntryState.Downloading]
     */
    private fun isDownloading(offlineContentOptions: OfflineContentOptions): Boolean {
        val allOfflineOptionEntries = Util.getAsOneList(offlineContentOptions)
        return allOfflineOptionEntries.any { entry -> entry.state == OfflineOptionEntryState.Downloading }
    }

    /**
     * Returns true, if one [OfflineOptionEntry]s state is [OfflineOptionEntryState.Suspended]
     *
     * @param offlineContentOptions
     * @return true, if one [OfflineOptionEntry]s state is [OfflineOptionEntryState.Suspended]
     */
    private fun isSuspended(offlineContentOptions: OfflineContentOptions): Boolean {
        val allOfflineOptionEntries = Util.getAsOneList(offlineContentOptions)
        return allOfflineOptionEntries.any { entry -> entry.state == OfflineOptionEntryState.Suspended }
    }

    /**
     * Returns true, if one [OfflineOptionEntry]s state is [OfflineOptionEntryState.Failed]
     *
     * @param offlineContentOptions
     * @return true, if one [OfflineOptionEntry]s state is [OfflineOptionEntryState.Failed]
     */
    private fun hasFailed(offlineContentOptions: OfflineContentOptions): Boolean {
        val allOfflineOptionEntries = Util.getAsOneList(offlineContentOptions)
        return allOfflineOptionEntries.any { entry -> entry.state == OfflineOptionEntryState.Failed }
    }

    /**
     * Returns true, if one [OfflineOptionEntry]s state is [OfflineOptionEntryState.Downloaded]
     *
     * @param offlineContentOptions
     * @return true, if one [OfflineOptionEntry]s state is [OfflineOptionEntryState.Downloaded]
     */
    private fun hasDownloaded(offlineContentOptions: OfflineContentOptions): Boolean {
        val allOfflineOptionEntries = Util.getAsOneList(offlineContentOptions)
        return allOfflineOptionEntries.any { entry -> entry.state == OfflineOptionEntryState.Downloaded }
    }
}
