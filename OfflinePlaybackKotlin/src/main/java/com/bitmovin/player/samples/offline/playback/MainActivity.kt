package com.bitmovin.player.samples.offline.playback

import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.widget.AdapterView
import android.widget.Toast
import com.bitmovin.player.DrmLicenseKeyExpiredException
import com.bitmovin.player.IllegalOperationException
import com.bitmovin.player.NoConnectionException
import com.bitmovin.player.api.event.data.ErrorEvent
import com.bitmovin.player.config.drm.WidevineConfiguration
import com.bitmovin.player.config.media.SourceItem
import com.bitmovin.player.offline.OfflineContentManager
import com.bitmovin.player.offline.OfflineContentManagerListener
import com.bitmovin.player.offline.OfflineSourceItem
import com.bitmovin.player.offline.options.OfflineContentOptions
import com.bitmovin.player.offline.options.OfflineOptionEntry
import com.bitmovin.player.offline.options.OfflineOptionEntryAction
import com.bitmovin.player.offline.options.OfflineOptionEntryState
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity(), OfflineContentManagerListener, ListItemActionListener {

    private var rootFolder: File? = null
    private var listItems = ArrayList<ListItem>()
    private var listAdapter: ListAdapter? = null

    private var retryOfflinePlayback = true
    private var listItemForRetry: ListItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Get the folder into which the downloaded offline content will be stored.
        // There can be multiple of such root folders and every can contain several offline contents.
        this.rootFolder = this.getDir("offline", ContextWrapper.MODE_PRIVATE)

        // Creating the ListView containing 2 example streams, which can be downloaded using this app.
        this.listItems.addAll(getListItems())
        this.listAdapter = ListAdapter(this, 0, this.listItems, this)
        listView.adapter = this.listAdapter
        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            onListItemClicked(parent.getItemAtPosition(position) as ListItem)
        }
        requestOfflineContentOptions(this.listItems)
    }

    override fun onStop() {
        super.onStop()
        for (item in listItems) {
            item.offlineContentManager.release()
        }
    }

    private fun requestOfflineContentOptions(listItems: List<ListItem>) {
        // Request OfflineContentOptions from the OfflineContentManager.
        // Note that the getOptions call is asynchronous, and that the result will be delivered to the according listener method onOptionsAvailable
        for (item in listItems) {
            item.offlineContentManager.getOptions()
        }
    }

    private fun onListItemClicked(listItem: ListItem) {
        playSource(listItem)
    }

    private fun playSource(listItem: ListItem) {
        var sourceItem: SourceItem? = null
        try {
            // First we try to get an OfflineSourceItem from the OfflineContentManager, as we prefer offline content
            sourceItem = listItem.offlineContentManager.offlineSourceItem
        } catch (e: IOException) {
            // If it fails to load needed files
            Toast.makeText(this, "Unable to load DRM license files", Toast.LENGTH_LONG).show()
        } catch (e: DrmLicenseKeyExpiredException) {
            try {
                this.listItemForRetry = listItem
                this.retryOfflinePlayback = true
                listItem.offlineContentManager.renewOfflineLicense()
            } catch (e: NoConnectionException) {
                Toast.makeText(this, "The DRM license expired, but there is no network connection", Toast.LENGTH_LONG).show()
            }
        }

        // If no offline content is available, or it fails to get an OfflineSourceItem, we take the original SourceItem for online streaming
        if (sourceItem == null) {
            sourceItem = listItem.sourceItem
        }
        startPlayerActivity(sourceItem)
    }

    private fun startPlayerActivity(sourceItem: SourceItem?) {
        val playerActivityIntent = Intent(this, PlayerActivity::class.java)

        // Add the SourceItem to the Intent
        val extraName = if (sourceItem is OfflineSourceItem) {
            PlayerActivity.OFFLINE_SOURCE_ITEM
        } else {
            PlayerActivity.SOURCE_ITEM
        }
        val gson = Gson()
        playerActivityIntent.putExtra(extraName, gson.toJson(sourceItem))

        //Start the PlayerActivity
        startActivity(playerActivityIntent)
    }

    /*
     * OfflineContentManagerListener callbacks
     */

    override fun onCompleted(sourceItem: SourceItem, offlineContentOptions: OfflineContentOptions) {
        val listItem = getListItemWithSourceItem(sourceItem)
        // Update the OfflineContentOptions, reset progress and notify the ListAdapter to update the views
        listItem?.offlineContentOptions = offlineContentOptions
        listItem?.progress = 0f
        this.listAdapter?.notifyDataSetChanged()
    }

    override fun onError(sourceItem: SourceItem, errorEvent: ErrorEvent) {
        Toast.makeText(this, errorEvent.message, Toast.LENGTH_SHORT).show()
    }

    override fun onProgress(sourceItem: SourceItem, progress: Float) {
        val listItem = getListItemWithSourceItem(sourceItem)
        val oldProgress = listItem?.progress
        listItem?.progress = progress

        // Only show full progress changes
        if (oldProgress?.toInt() != progress.toInt()) {
            listAdapter?.notifyDataSetChanged()
        }
    }

    override fun onOptionsAvailable(sourceItem: SourceItem, offlineContentOptions: OfflineContentOptions) {
        val listItem = getListItemWithSourceItem(sourceItem)
        // Update the OfflineContentOptions and notify the ListAdapter to update the views
        listItem?.offlineContentOptions = offlineContentOptions
        this.listAdapter?.notifyDataSetChanged()
    }

    override fun onDrmLicenseUpdated(sourceItem: SourceItem) {
        if (this.retryOfflinePlayback) {
            if (this.listItemForRetry?.sourceItem === sourceItem) {
                // At the last try, the license was expired
                // so we try it now again
                val listItem = this.listItemForRetry
                this.retryOfflinePlayback = false
                this.listItemForRetry = null
                if (listItem != null) {
                    playSource(listItem)
                }
            }
        }
    }

    override fun onSuspended(sourceItem: SourceItem) {
        Toast.makeText(this, "Suspended: ${sourceItem.title}", Toast.LENGTH_SHORT).show()
    }

    override fun onResumed(sourceItem: SourceItem) {
        Toast.makeText(this, "Resumed: ${sourceItem.title}", Toast.LENGTH_SHORT).show()
    }

    /*
     * Listener methods for the two buttons every ListItem has
     */

    override fun showSelectionDialog(listItem: ListItem) {
        val offlineContentOptions = listItem.offlineContentOptions

        if (offlineContentOptions != null) {
            // Generating the needed lists, to create an AlertDialog, listing all options
            val entries = Util.getAsOneList(offlineContentOptions)
            val entriesAsText = Array(entries.size) { "" }
            val entriesCheckList = BooleanArray(entries.size)

            entries.forEachIndexed { i, entry ->
                try {
                    // Resetting the Action if set
                    entry.action = null
                } catch (e: IllegalOperationException) {
                    // Won't happen
                }

                entriesAsText[i] = entry.id + "-" + entry.mimeType
                entriesCheckList[i] = entry.state == OfflineOptionEntryState.DOWNLOADED || entry.action == OfflineOptionEntryAction.DOWNLOAD
            }

            // Building and showing the AlertDialog
            val dialogBuilder = generateAlertDialogBuilder(listItem, entries, entriesAsText, entriesCheckList)
            dialogBuilder.show()
        }
    }

    override fun delete(listItem: ListItem) {
        // To delete everything of a specific OfflineContentManager, we call deleteAll
        listItem.offlineContentManager.deleteAll()
        Toast.makeText(this, "Deleting " + listItem.sourceItem.title, Toast.LENGTH_SHORT).show()
    }

    override fun suspend(listItem: ListItem) {
        listItem.offlineContentManager.suspend()
    }

    override fun resume(listItem: ListItem) {
        listItem.offlineContentManager.resume()
    }

    private fun download(listItem: ListItem) {
        val offlineContentManager = listItem.offlineContentManager

        try {
            // Passing the OfflineContentOptions with set OfflineOptionEntryActions to the OfflineContentManager
            offlineContentManager.process(listItem.offlineContentOptions)
        } catch (e: NoConnectionException) {
            e.printStackTrace()
        }

    }

    private fun generateAlertDialogBuilder(listItem: ListItem, entries: List<OfflineOptionEntry>, entriesAsText: Array<String>, entryCheckList: BooleanArray): AlertDialog.Builder {
        val dialogBuilder = AlertDialog.Builder(this).setMultiChoiceItems(entriesAsText, entryCheckList) { _, which, isChecked ->
            try {
                // Set an Download/Delete action, if the user changes the checked state
                val offlineOptionEntry = entries[which]
                offlineOptionEntry.action = if (isChecked) OfflineOptionEntryAction.DOWNLOAD else OfflineOptionEntryAction.DELETE
            } catch (e: IllegalOperationException) {
            }
        }
        dialogBuilder.setPositiveButton(android.R.string.ok) { _, _ -> download(listItem) }
        dialogBuilder.setNegativeButton(android.R.string.cancel, null)
        return dialogBuilder
    }

    private fun getListItemWithSourceItem(sourceItem: SourceItem): ListItem? {
        // Find the matching SourceItem in the List, containing all our SourceItems
        return this.listItems.find { item -> item.sourceItem === sourceItem }
    }

    private fun getListItems(): List<ListItem> {
        val listItems = ArrayList<ListItem>()

        // Initialize a SourceItem
        val artOfMotion = SourceItem("https://bitmovin-a.akamaihd.net/content/MI201109210084_1/mpds/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.mpd")
        artOfMotion.setThumbnailTrack("https://bitmovin-a.akamaihd.net/content/MI201109210084_1/thumbnails/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.vtt")
        artOfMotion.title = "Art of Motion"

        // Initialize an OfflineContentManager in the rootFolder with the id "artOfMotion"
        val artOfMotionOfflineContentManager = OfflineContentManager.getOfflineContentManager(artOfMotion,
                this.rootFolder?.path, "artOfMotion", this, this)

        // Create a ListItem from the SourceItem and the OfflienContentManager
        val artOfMotionListItem = ListItem(artOfMotion, artOfMotionOfflineContentManager)

        // Add the ListItem to the List
        listItems.add(artOfMotionListItem)

        // Initialize a SourceItem with a DRM configuration
        val artOfMotionDrm = SourceItem("https://bitmovin-a.akamaihd.net/content/art-of-motion_drm/mpds/11331.mpd")
        artOfMotionDrm.addDRMConfiguration(WidevineConfiguration("https://widevine-proxy.appspot.com/proxy"))
        artOfMotionDrm.title = "Art of Motion with DRM"

        // Initialize an OfflineContentManager in the rootFolder with the id "artOfMotionDrm"
        val artOfMotionDrmOfflineContentManager = OfflineContentManager.getOfflineContentManager(artOfMotionDrm,
                this.rootFolder?.path, "artOfMotionDrm", this, this)

        // Create a ListItem from the SourceItem and the OfflineContentManager
        val artOfMotionDrmListItem = ListItem(artOfMotionDrm, artOfMotionDrmOfflineContentManager)

        // Add the ListItem to the List
        listItems.add(artOfMotionDrmListItem)

        return listItems
    }
}
