/*
 * Bitmovin Player Android SDK
 * Copyright (C) 2017, Bitmovin GmbH, All Rights Reserved
 *
 * This source code and its use and distribution, is subject to the terms
 * and conditions of the applicable license agreement.
 */

package com.bitmovin.player.samples.offline.playback;

import android.Manifest;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bitmovin.player.api.deficiency.ErrorEvent;
import com.bitmovin.player.api.deficiency.exception.DrmLicenseKeyExpiredException;
import com.bitmovin.player.api.deficiency.exception.IllegalOperationException;
import com.bitmovin.player.api.deficiency.exception.NoConnectionException;
import com.bitmovin.player.api.drm.WidevineConfig;
import com.bitmovin.player.api.media.thumbnail.ThumbnailTrack;
import com.bitmovin.player.api.offline.OfflineContentManager;
import com.bitmovin.player.api.offline.OfflineContentManagerListener;
import com.bitmovin.player.api.offline.OfflineSourceConfig;
import com.bitmovin.player.api.offline.options.OfflineContentOptions;
import com.bitmovin.player.api.offline.options.OfflineOptionEntry;
import com.bitmovin.player.api.offline.options.OfflineOptionEntryAction;
import com.bitmovin.player.api.offline.options.OfflineOptionEntryState;
import com.bitmovin.player.api.source.SourceConfig;
import com.bitmovin.player.api.source.SourceType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OfflineContentManagerListener, ListItemActionListener {
    private File rootFolder;
    private List<ListItem> listItems;
    private ListView listView;
    private ListAdapter listAdapter;

    private boolean retryOfflinePlayback = true;
    private ListItem listItemForRetry = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        listView = (ListView) findViewById(R.id.listview);
        requestMissingPermissions();

        // Get the folder into which the downloaded offline content will be stored.
        // There can be multiple of such root folders and every can contain several offline contents.
        rootFolder = getDir("offline", ContextWrapper.MODE_PRIVATE);

        // Creating the ListView containing 2 example streams, which can be downloaded using this app.
        listItems = getListItems();
        listAdapter = new ListAdapter(this, 0, listItems, this);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener((parent, view, position, id) -> onListItemClicked((ListItem) parent.getItemAtPosition(position)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestOfflineContentOptions(listItems);
    }

    @Override
    protected void onStop() {
        for (ListItem listItem : listItems) {
            listItem.getOfflineContentManager().release();
        }
        listItems = null;
        listAdapter = null;
        listView.setOnItemClickListener(null);
        super.onStop();
    }

    private void requestOfflineContentOptions(List<ListItem> listItems) {
        for (ListItem listItem : listItems) {
            // Request OfflineContentOptions from the OfflineContentManager.
            // Note that the getOptions call is asynchronous, and that the result will be delivered to the according listener method onOptionsAvailable
            listItem.getOfflineContentManager().getOptions();
        }
    }

    private void onListItemClicked(ListItem listItem) {
        playSource(listItem);
    }

    private void playSource(ListItem listItem) {
        SourceConfig sourceConfig = null;
        try {
            // First we try to get an OfflineSourceConfig from the OfflineContentManager, as we prefer offline content
            sourceConfig = listItem.getOfflineContentManager().getOfflineSourceConfig();
        } catch (IOException e) {
            // If it fails to load needed files
        } catch (DrmLicenseKeyExpiredException e) {
            try {
                listItemForRetry = listItem;
                retryOfflinePlayback = true;
                listItem.getOfflineContentManager().renewOfflineLicense();
            } catch (NoConnectionException e1) {
                Toast.makeText(this, "The DRM license expired, but there is no network connection", Toast.LENGTH_LONG).show();
            }
        }

        // If no offline content is available, or it fails to get an OfflineSourceConfig, we take the original SourceConfig for online streaming
        if (sourceConfig == null) {
            sourceConfig = listItem.getSourceConfig();
        }
        startPlayerActivity(sourceConfig);
    }

    private void startPlayerActivity(SourceConfig sourceConfig) {
        Intent playerActivityIntent = new Intent(this, PlayerActivity.class);

        // Add the SourceConfig to the Intent
        String extraName = sourceConfig instanceof OfflineSourceConfig ? PlayerActivity.OFFLINE_SOURCE_ITEM : PlayerActivity.SOURCE_ITEM;
        playerActivityIntent.putExtra(extraName, sourceConfig);

        //Start the PlayerActivity
        startActivity(playerActivityIntent);
    }

    /*
     * Implementation of the OfflineContentManagerListener methods
     */

    @Override
    public void onCompleted(SourceConfig sourceConfig, OfflineContentOptions offlineContentOptions) {
        ListItem listItem = getListItemWithSourceConfig(sourceConfig);
        if (listItem != null) {
            // Update the OfflineContentOptions, reset progress and notify the ListAdapter to update the views
            listItem.setOfflineContentOptions(offlineContentOptions);
            listItem.setProgress(0);
            listAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onError(SourceConfig sourceConfig, ErrorEvent errorEvent) {
        Toast.makeText(this, errorEvent.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProgress(SourceConfig sourceConfig, float progress) {
        ListItem listItem = getListItemWithSourceConfig(sourceConfig);
        if (listItem != null) {
            float oldProgress = listItem.getProgress();
            listItem.setProgress(progress);

            // Only show full progress changes
            if ((int) oldProgress != (int) progress) {
                listAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onOptionsAvailable(SourceConfig sourceConfig, OfflineContentOptions offlineContentOptions) {
        ListItem listItem = getListItemWithSourceConfig(sourceConfig);
        if (listItem != null) {
            // Update the OfflineContentOptions and notify the ListAdapter to update the views
            listItem.setOfflineContentOptions(offlineContentOptions);
            listAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDrmLicenseUpdated(SourceConfig sourceConfig) {
        if (retryOfflinePlayback) {
            if (listItemForRetry.getSourceConfig() == sourceConfig) {
                // At the last try, the license was expired
                // so we try it now again
                ListItem listItem = listItemForRetry;
                retryOfflinePlayback = false;
                listItemForRetry = null;
                playSource(listItem);
            }
        }
    }

    @Override
    public void onSuspended(SourceConfig sourceConfig) {
        Toast.makeText(this, "Suspended: " + sourceConfig.getTitle(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResumed(SourceConfig sourceConfig) {
        Toast.makeText(this, "Resumed: " + sourceConfig.getTitle(), Toast.LENGTH_SHORT).show();
    }

    /*
     * Listener methods for the two buttons every ListItem has
     */
    @Override
    public void showSelectionDialog(ListItem listItem) {
        OfflineContentOptions offlineContentOptions = listItem.getOfflineContentOptions();

        // Generating the needed lists, to create an AlertDialog, listing all options
        List<OfflineOptionEntry> entries = Util.getAsOneList(offlineContentOptions);
        String[] entriesAsText = new String[entries.size()];
        boolean[] entriesCheckList = new boolean[entries.size()];
        for (int i = 0; i < entriesAsText.length; i++) {
            OfflineOptionEntry oh = entries.get(i);
            try {
                // Resetting the Action if set
                oh.setAction(null);
            } catch (IllegalOperationException e) {
                // Won't happen
            }
            entriesAsText[i] = oh.getId() + "-" + oh.getMimeType();
            entriesCheckList[i] = oh.getState() == OfflineOptionEntryState.Downloaded || oh.getAction() == OfflineOptionEntryAction.Download;
        }

        // Building and showing the AlertDialog
        AlertDialog.Builder dialogBuilder = generateAlertDialogBuilder(listItem, entries, entriesAsText, entriesCheckList);
        dialogBuilder.show();
    }

    @Override
    public void delete(ListItem listItem) {
        // To delete everything of a specific OfflineContentManager, we call deleteAll
        listItem.getOfflineContentManager().deleteAll();
        Toast.makeText(this, "Deleting " + listItem.getSourceConfig().getTitle(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void suspend(ListItem listItem) {
        listItem.getOfflineContentManager().suspend();
    }

    @Override
    public void resume(ListItem listItem) {
        listItem.getOfflineContentManager().resume();
    }

    public void download(ListItem listItem) {
        OfflineContentManager offlineContentManager = listItem.getOfflineContentManager();
        if (offlineContentManager == null) {
            return;
        }

        try {
            // Passing the OfflineContentOptions with set OfflineOptionEntryActions to the OfflineContentManager
            offlineContentManager.process(listItem.getOfflineContentOptions());
        } catch (NoConnectionException e) {
            e.printStackTrace();
        }
    }

    private AlertDialog.Builder generateAlertDialogBuilder(final ListItem listItem, final List<OfflineOptionEntry> entries, String[] entriesAsText, boolean[] entryCheckList) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this).setMultiChoiceItems(entriesAsText, entryCheckList, (dialog, which, isChecked) -> {
            try {
                // Set an Download/Delete action, if the user changes the checked state
                OfflineOptionEntry offlineOptionEntry = entries.get(which);
                offlineOptionEntry.setAction(isChecked ? OfflineOptionEntryAction.Download : OfflineOptionEntryAction.Delete);
            } catch (IllegalOperationException e) {
                e.printStackTrace();
            }

        });
        dialogBuilder.setPositiveButton(android.R.string.ok, (dialog, which) -> download(listItem));
        dialogBuilder.setNegativeButton(android.R.string.cancel, null);
        return dialogBuilder;
    }

    private ListItem getListItemWithSourceConfig(SourceConfig sourceConfig) {
        // Find the matching SourceConfig in the List, containing all our SourceConfigs
        for (ListItem listItem : listItems) {
            if (listItem.getSourceConfig() == sourceConfig) {
                return listItem;
            }
        }
        return null;
    }

    private List<ListItem> getListItems() {
        List<ListItem> listItems = new ArrayList<>();

        // Initialize a SourceConfig
        SourceConfig artOfMotion = new SourceConfig("https://bitmovin-a.akamaihd.net/content/MI201109210084_1/mpds/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.mpd", SourceType.Dash);
        artOfMotion.setThumbnailTrack(new ThumbnailTrack("https://bitmovin-a.akamaihd.net/content/MI201109210084_1/thumbnails/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.vtt"));
        artOfMotion.setTitle("Art of Motion");

        // Initialize an OfflineContentManager in the rootFolder with the id "artOfMotion"
        OfflineContentManager artOfMotionOfflineContentManager = OfflineContentManager.getOfflineContentManager(artOfMotion, rootFolder.getPath(), "artOfMotion", this, this);

        // Create a ListItem from the SourceConfig and the OfflienContentManager
        ListItem artOfMotionListItem = new ListItem(artOfMotion, artOfMotionOfflineContentManager);

        // Add the ListItem to the List
        listItems.add(artOfMotionListItem);

        // Initialize a SourceConfig with a DRM configuration
        SourceConfig artOfMotionDrm = new SourceConfig("https://bitmovin-a.akamaihd.net/content/art-of-motion_drm/mpds/11331.mpd", SourceType.Dash);
        artOfMotionDrm.setDrmConfig(new WidevineConfig("https://cwip-shaka-proxy.appspot.com/no_auth"));
        artOfMotionDrm.setTitle("Art of Motion with DRM");

        // Initialize an OfflineContentManager in the rootFolder with the id "artOfMotionDrm"
        OfflineContentManager artOfMotionDrmOfflineContentManager = OfflineContentManager.getOfflineContentManager(artOfMotionDrm, rootFolder.getPath(), "artOfMotionDrm", this, this);

        // Create a ListItem from the SourceConfig and the OfflineContentManager
        ListItem artOfMotionDrmListItem = new ListItem(artOfMotionDrm, artOfMotionDrmOfflineContentManager);

        // Add the ListItem to the List
        listItems.add(artOfMotionDrmListItem);


        return listItems;
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> { /* Do nothing */ }
    );

    private void requestMissingPermissions() {
        if (Build.VERSION.SDK_INT < 33) return;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }
}
