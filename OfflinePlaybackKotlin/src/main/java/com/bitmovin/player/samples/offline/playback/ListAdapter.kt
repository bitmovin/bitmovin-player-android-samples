package com.bitmovin.player.samples.offline.playback

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.LayoutRes
import com.bitmovin.player.api.offline.options.OfflineContentOptions
import com.bitmovin.player.api.offline.options.OfflineOptionEntryState
import com.bitmovin.player.samples.offline.playback.databinding.ListItemBinding

class ListAdapter(
        context: Context,
        @LayoutRes resource: Int,
        objects: List<ListItem>,
        private val listItemActionListener: ListItemActionListener
) : ArrayAdapter<ListItem>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = convertView?.let { ListItemBinding.bind(convertView) }
            ?: ListItemBinding.inflate(LayoutInflater.from(context), parent, false)

        val listItem = getItem(position)

        val sourceConfig = listItem?.sourceConfig
        val offlineContentOptions = listItem?.offlineContentOptions
        binding.title.text = sourceConfig?.title

        if (offlineContentOptions != null) {
            binding.btnDownload.visibility = View.VISIBLE
            binding.btnDownload.setOnClickListener { listItemActionListener.showSelectionDialog(listItem) }

            // If any option is downloading, show a progress in the list
            when {
                isDownloading(offlineContentOptions) -> {
                    binding.apply {
                        state.text = String.format("Downloading - %.0f %%", listItem.progress)
                        state.visibility = View.VISIBLE
                        btnPauseResume.setImageResource(R.drawable.ic_pause_black_24dp)
                        btnPauseResume.visibility = View.VISIBLE
                        btnPauseResume.setOnClickListener { listItemActionListener.suspend(listItem) }
                    }
                }
                isSuspended(offlineContentOptions) -> {
                    binding.btnPauseResume.apply {
                        setImageResource(R.drawable.ic_play_arrow_black_24dp)
                        visibility = View.VISIBLE
                        setOnClickListener { listItemActionListener.resume(listItem) }
                    }
                }
                hasFailed(offlineContentOptions) -> {
                    binding.apply {
                        state.text = String.format("Failed - %.0f %%", listItem.progress)
                        state.visibility = View.VISIBLE
                        btnPauseResume.setImageResource(R.drawable.ic_play_arrow_black_24dp)
                        btnPauseResume.visibility = View.VISIBLE
                        btnPauseResume.setOnClickListener { listItemActionListener.resume(listItem) }
                    }
                }
                else -> {
                    binding.state.visibility = View.INVISIBLE
                    binding.btnPauseResume.visibility = View.INVISIBLE
                }
            }
            // If any option is downloaded, we show the delete button
            if (hasDownloaded(offlineContentOptions)) {
                binding.btnDelete.visibility = View.VISIBLE
                binding.btnDelete.setOnClickListener { listItemActionListener.delete(listItem) }
            } else {
                binding.btnDelete.visibility = View.GONE
            }

        } else {
            // If no options are available, we hide the download and the delete button
            binding.btnDelete.visibility = View.GONE
            binding.btnDownload.visibility = View.GONE
        }

        return binding.root
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
