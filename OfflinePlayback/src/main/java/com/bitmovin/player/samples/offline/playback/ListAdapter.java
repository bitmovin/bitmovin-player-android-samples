/*
 * Bitmovin Player Android SDK
 * Copyright (C) 2017, Bitmovin GmbH, All Rights Reserved
 *
 * This source code and its use and distribution, is subject to the terms
 * and conditions of the applicable license agreement.
 */

package com.bitmovin.player.samples.offline.playback;

import android.content.Context;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bitmovin.player.api.offline.options.OfflineContentOptions;
import com.bitmovin.player.api.offline.options.OfflineOptionEntry;
import com.bitmovin.player.api.offline.options.OfflineOptionEntryState;
import com.bitmovin.player.api.source.SourceConfig;

import java.util.List;

class ListAdapter extends ArrayAdapter<ListItem> {
    private ListItemActionListener listItemActionListener;

    public ListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<ListItem> objects, ListItemActionListener listItemActionListener) {
        super(context, resource, objects);
        this.listItemActionListener = listItemActionListener;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        final ListItem listItem = getItem(position);

        TextView title = view.findViewById(R.id.title);
        TextView state = view.findViewById(R.id.state);
        ImageButton pauseResume = view.findViewById(R.id.btnPauseResume);
        ImageButton btnDelete = view.findViewById(R.id.btnDelete);
        ImageButton btnDownload = view.findViewById(R.id.btnDownload);

        SourceConfig sourceConfig = listItem.getSourceConfig();
        OfflineContentOptions offlineContentOptions = listItem.getOfflineContentOptions();

        title.setText(sourceConfig.getTitle());
        if (offlineContentOptions != null) {
            btnDownload.setVisibility(View.VISIBLE);
            btnDownload.setOnClickListener(v -> listItemActionListener.showSelectionDialog(listItem));

            // If any option is downloading, we show a progress in the list
            if (isDownloading(offlineContentOptions)) {
                state.setText(String.format("Downloading - %.0f %%", listItem.getProgress()));
                state.setVisibility(View.VISIBLE);
                pauseResume.setImageResource(R.drawable.ic_pause_black_24dp);
                pauseResume.setVisibility(View.VISIBLE);
                pauseResume.setOnClickListener(v -> listItemActionListener.suspend(listItem));
            }
            else if (isSuspended(offlineContentOptions)) {
                pauseResume.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                pauseResume.setVisibility(View.VISIBLE);
                pauseResume.setOnClickListener(v -> listItemActionListener.resume(listItem));
            }
            else if (hasFailed(offlineContentOptions)) {
                state.setText(String.format("Failed - %.0f %%", listItem.getProgress()));
                state.setVisibility(View.VISIBLE);
                pauseResume.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                pauseResume.setVisibility(View.VISIBLE);
                pauseResume.setOnClickListener(v -> listItemActionListener.resume(listItem));
            }
            else {
                state.setVisibility(View.INVISIBLE);
                pauseResume.setVisibility(View.INVISIBLE);
            }
            // If any option is downloaded, we show the delete button
            if (hasDownloaded(offlineContentOptions)) {
                btnDelete.setVisibility(View.VISIBLE);
                btnDelete.setOnClickListener(v -> listItemActionListener.delete(listItem));
            }
            else {
                btnDelete.setVisibility(View.GONE);
            }

        }
        else {
            // If no options are available, we hide the download and the delete button
            btnDelete.setVisibility(View.GONE);
            btnDownload.setVisibility(View.GONE);
        }

        return view;
    }

    /**
     * Returns true, if one {@link OfflineOptionEntry}s state is {@link OfflineOptionEntryState#Downloading}
     *
     * @param offlineContentOptions
     * @return true, if one {@link OfflineOptionEntry}s state is {@link OfflineOptionEntryState#Downloading}
     */
    private boolean isDownloading(OfflineContentOptions offlineContentOptions) {
        List<OfflineOptionEntry> allOfflineOptionEntries = Util.getAsOneList(offlineContentOptions);
        for (OfflineOptionEntry entry : allOfflineOptionEntries) {
            if (entry.getState() == OfflineOptionEntryState.Downloading) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true, if one {@link OfflineOptionEntry}s state is {@link OfflineOptionEntryState#Suspended}
     *
     * @param offlineContentOptions
     * @return true, if one {@link OfflineOptionEntry}s state is {@link OfflineOptionEntryState#Suspended}
     */
    private boolean isSuspended(OfflineContentOptions offlineContentOptions) {
        List<OfflineOptionEntry> allOfflineOptionEntries = Util.getAsOneList(offlineContentOptions);
        for (OfflineOptionEntry entry : allOfflineOptionEntries) {
            if (entry.getState() == OfflineOptionEntryState.Suspended) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true, if one {@link OfflineOptionEntry}s state is {@link OfflineOptionEntryState#Failed}
     *
     * @param offlineContentOptions
     * @return true, if one {@link OfflineOptionEntry}s state is {@link OfflineOptionEntryState#Failed}
     */
    private boolean hasFailed(OfflineContentOptions offlineContentOptions) {
        List<OfflineOptionEntry> allOfflineOptionEntries = Util.getAsOneList(offlineContentOptions);
        for (OfflineOptionEntry entry : allOfflineOptionEntries) {
            if (entry.getState() == OfflineOptionEntryState.Failed) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true, if one {@link OfflineOptionEntry}s state is {@link OfflineOptionEntryState#Downloaded}
     *
     * @param offlineContentOptions
     * @return true, if one {@link OfflineOptionEntry}s state is {@link OfflineOptionEntryState#Downloaded}
     */
    private boolean hasDownloaded(OfflineContentOptions offlineContentOptions) {
        List<OfflineOptionEntry> allOfflineOptionEntries = Util.getAsOneList(offlineContentOptions);
        for (OfflineOptionEntry entry : allOfflineOptionEntries) {
            if (entry.getState() == OfflineOptionEntryState.Downloaded) {
                return true;
            }
        }
        return false;
    }
}
