package com.bitmovin.player.samples.offline.playback

import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bitmovin.player.api.deficiency.ErrorEvent
import com.bitmovin.player.api.deficiency.exception.DrmLicenseKeyExpiredException
import com.bitmovin.player.api.deficiency.exception.IllegalOperationException
import com.bitmovin.player.api.deficiency.exception.NoConnectionException
import com.bitmovin.player.api.drm.WidevineConfig
import com.bitmovin.player.api.media.thumbnail.ThumbnailTrack
import com.bitmovin.player.api.offline.OfflineContentManager
import com.bitmovin.player.api.offline.OfflineContentManagerListener
import com.bitmovin.player.api.offline.OfflineSourceConfig
import com.bitmovin.player.api.offline.options.OfflineContentOptions
import com.bitmovin.player.api.offline.options.OfflineOptionEntry
import com.bitmovin.player.api.offline.options.OfflineOptionEntryAction
import com.bitmovin.player.api.offline.options.OfflineOptionEntryState
import com.bitmovin.player.api.source.SourceConfig
import com.bitmovin.player.api.source.SourceType
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity(), OfflineContentManagerListener, ListItemActionListener {
    private lateinit var rootFolder: File
    private var listItems = ArrayList<ListItem>()
    private var listAdapter: ListAdapter? = null

    private var retryOfflinePlayback = true
    private var listItemForRetry: ListItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        // Get the folder into which the downloaded offline content will be stored.
        // There can be multiple of such root folders and every can contain several offline contents.
        rootFolder = getDir("offline", ContextWrapper.MODE_PRIVATE)

        // Creating the ListView containing 2 example streams, which can be downloaded using this app.
        listItems.addAll(getListItems())
        listAdapter = ListAdapter(this, 0, listItems, this)
        listView.adapter = listAdapter
        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            onListItemClicked(parent.getItemAtPosition(position) as ListItem)
        }
    }

    override fun onResume() {
        super.onResume()
        requestOfflineContentOptions(listItems)
    }

    override fun onStop() {
        super.onStop()
        listItems.map { it.offlineContentManager }.forEach(OfflineContentManager::release)
        listItems.clear()
        listAdapter = null
        listView.onItemClickListener = null
    }

    private fun requestOfflineContentOptions(listItems: List<ListItem>) {
        // Request OfflineContentOptions from the OfflineContentManager.
        // Note that the getOptions call is asynchronous, and that the result will be delivered to the according listener method onOptionsAvailable
        listItems.map { it.offlineContentManager }.forEach(OfflineContentManager::getOptions)
    }

    private fun onListItemClicked(listItem: ListItem) {
        playSource(listItem)
    }

    private fun playSource(listItem: ListItem) {
        var sourceConfig: SourceConfig? = null
        try {
            // First we try to get an OfflineSourceConfig from the OfflineContentManager, as we prefer offline content
            sourceConfig = listItem.offlineContentManager.offlineSourceConfig
        } catch (e: IOException) {
            // If it fails to load needed files
            Toast.makeText(this, "Unable to load DRM license files", Toast.LENGTH_LONG).show()
        } catch (e: DrmLicenseKeyExpiredException) {
            try {
                listItemForRetry = listItem
                retryOfflinePlayback = true
                listItem.offlineContentManager.renewOfflineLicense()
            } catch (e: NoConnectionException) {
                Toast.makeText(this, "The DRM license expired, but there is no network connection", Toast.LENGTH_LONG).show()
            }
        }

        // If no offline content is available, or it fails to get an OfflineSourceConfig, we take the original SourceConfig for online streaming
        if (sourceConfig == null) {
            sourceConfig = listItem.sourceConfig
        }
        startPlayerActivity(sourceConfig)
    }

    private fun startPlayerActivity(sourceConfig: SourceConfig?) {
        val playerActivityIntent = Intent(this, PlayerActivity::class.java)

        // Add the SourceItem to the Intent
        val extraName = if (sourceConfig is OfflineSourceConfig) {
            PlayerActivity.OFFLINE_SOURCE_CONFIG
        } else {
            PlayerActivity.SOURCE_CONFIG
        }
        val gson = Gson()
        playerActivityIntent.putExtra(extraName, gson.toJson(sourceConfig))

        //Start the PlayerActivity
        startActivity(playerActivityIntent)
    }

    /*
     * OfflineContentManagerListener callbacks
     */
    override fun onCompleted(sourceConfig: SourceConfig, offlineContentOptions: OfflineContentOptions) {
        val listItem = getListItemWithSourceItem(sourceConfig)
        // Update the OfflineContentOptions, reset progress and notify the ListAdapter to update the views
        listItem?.offlineContentOptions = offlineContentOptions
        listItem?.progress = 0f
        listAdapter?.notifyDataSetChanged()
    }

    override fun onError(sourceConfig: SourceConfig, errorEvent: ErrorEvent) {
        Toast.makeText(this, errorEvent.message, Toast.LENGTH_SHORT).show()
    }

    override fun onProgress(sourceConfig: SourceConfig, progress: Float) {
        val listItem = getListItemWithSourceItem(sourceConfig)
        val oldProgress = listItem?.progress
        listItem?.progress = progress

        // Only show full progress changes
        if (oldProgress?.toInt() != progress.toInt()) {
            listAdapter?.notifyDataSetChanged()
        }
    }

    override fun onOptionsAvailable(sourceConfig: SourceConfig, offlineContentOptions: OfflineContentOptions) {
        val listItem = getListItemWithSourceItem(sourceConfig)
        // Update the OfflineContentOptions and notify the ListAdapter to update the views
        listItem?.offlineContentOptions = offlineContentOptions
        listAdapter?.notifyDataSetChanged()
    }

    override fun onDrmLicenseUpdated(sourceConfig: SourceConfig) {
        if (retryOfflinePlayback) {
            if (listItemForRetry?.sourceConfig === sourceConfig) {
                // At the last try, the license was expired
                // so we try it now again
                val listItem = listItemForRetry
                retryOfflinePlayback = false
                listItemForRetry = null
                if (listItem != null) {
                    playSource(listItem)
                }
            }
        }
    }

    override fun onSuspended(sourceConfig: SourceConfig) {
        Toast.makeText(this, "Suspended: ${sourceConfig.title}", Toast.LENGTH_SHORT).show()
    }

    override fun onResumed(sourceConfig: SourceConfig) {
        Toast.makeText(this, "Resumed: ${sourceConfig.title}", Toast.LENGTH_SHORT).show()
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
                entriesCheckList[i] = entry.state == OfflineOptionEntryState.Downloaded || entry.action == OfflineOptionEntryAction.Download
            }

            // Building and showing the AlertDialog
            val dialogBuilder = generateAlertDialogBuilder(listItem, entries, entriesAsText, entriesCheckList)
            dialogBuilder.show()
        }
    }

    override fun delete(listItem: ListItem) {
        // To delete everything of a specific OfflineContentManager, we call deleteAll
        listItem.offlineContentManager.deleteAll()
        Toast.makeText(this, "Deleting " + listItem.sourceConfig.title, Toast.LENGTH_SHORT).show()
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
            listItem.offlineContentOptions?.let {
                offlineContentManager.process(it)
            }
        } catch (e: NoConnectionException) {
            e.printStackTrace()
        }

    }

    private fun generateAlertDialogBuilder(listItem: ListItem, entries: List<OfflineOptionEntry>, entriesAsText: Array<String>, entryCheckList: BooleanArray): AlertDialog.Builder {
        val dialogBuilder = AlertDialog.Builder(this).setMultiChoiceItems(entriesAsText, entryCheckList) { _, which, isChecked ->
            try {
                // Set an Download/Delete action, if the user changes the checked state
                val offlineOptionEntry = entries[which]
                offlineOptionEntry.action = if (isChecked) OfflineOptionEntryAction.Download else OfflineOptionEntryAction.Delete
            } catch (e: IllegalOperationException) {
            }
        }
        dialogBuilder.setPositiveButton(android.R.string.ok) { _, _ -> download(listItem) }
        dialogBuilder.setNegativeButton(android.R.string.cancel, null)
        return dialogBuilder
    }

    private fun getListItemWithSourceItem(sourceConfig: SourceConfig): ListItem? {
        // Find the matching SourceItem in the List, containing all our SourceItems
        return listItems.find { item -> item.sourceConfig === sourceConfig }
    }

    private fun getListItems(): List<ListItem> {
        val listItems = ArrayList<ListItem>()

        // Initialize a SourceConfig
        val artOfMotion = SourceConfig("https://bitmovin-a.akamaihd.net/content/MI201109210084_1/mpds/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.mpd", SourceType.Dash)
        artOfMotion.thumbnailTrack = ThumbnailTrack("https://bitmovin-a.akamaihd.net/content/MI201109210084_1/thumbnails/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.vtt")
        artOfMotion.title = "Art of Motion"

        // Initialize an OfflineContentManager in the rootFolder with the id "artOfMotion"
        val artOfMotionOfflineContentManager = OfflineContentManager.getOfflineContentManager(artOfMotion,
                rootFolder.path, "artOfMotion", this, this)

        // Create a ListItem from the SourceConfig and the OfflienContentManager
        val artOfMotionListItem = ListItem(artOfMotion, artOfMotionOfflineContentManager)

        // Add the ListItem to the List
        listItems.add(artOfMotionListItem)

        // Initialize a SourceConfig with a DRM configuration
        val artOfMotionDrm = SourceConfig("https://bitmovin-a.akamaihd.net/content/art-of-motion_drm/mpds/11331.mpd", SourceType.Dash)
        artOfMotionDrm.drmConfig = WidevineConfig("https://widevine-proxy.appspot.com/proxy")
        artOfMotionDrm.title = "Art of Motion with DRM"

        // Initialize an OfflineContentManager in the rootFolder with the id "artOfMotionDrm"
        val artOfMotionDrmOfflineContentManager = OfflineContentManager.getOfflineContentManager(artOfMotionDrm,
                rootFolder.path, "artOfMotionDrm", this, this)

        // Create a ListItem from the SourceConfig and the OfflineContentManager
        val artOfMotionDrmListItem = ListItem(artOfMotionDrm, artOfMotionDrmOfflineContentManager)

        // Add the ListItem to the List
        listItems.add(artOfMotionDrmListItem)

        return listItems
    }
}
