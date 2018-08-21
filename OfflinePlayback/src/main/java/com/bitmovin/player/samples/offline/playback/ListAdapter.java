/*
 * Bitmovin Player Android SDK
 * Copyright (C) 2017, Bitmovin GmbH, All Rights Reserved
 *
 * This source code and its use and distribution, is subject to the terms
 * and conditions of the applicable license agreement.
 */

package com.bitmovin.player.samples.offline.playback;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bitmovin.player.config.media.SourceItem;
import com.bitmovin.player.offline.options.OfflineContentOptions;
import com.bitmovin.player.offline.options.OfflineOptionEntry;
import com.bitmovin.player.offline.options.OfflineOptionEntryState;

import java.util.List;

class ListAdapter extends ArrayAdapter<ListItem>
{
    private ListItemActionListener listItemActionListener;

    public ListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<ListItem> objects, ListItemActionListener listItemActionListener)
    {
        super(context, resource, objects);
        this.listItemActionListener = listItemActionListener;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        View view = convertView;

        if (view == null)
        {
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        final ListItem listItem = getItem(position);

        TextView title = (TextView) view.findViewById(R.id.title);
        TextView state = (TextView) view.findViewById(R.id.state);
        ImageButton pauseResume = (ImageButton) view.findViewById(R.id.btnPauseResume);
        ImageButton btnDelete = (ImageButton) view.findViewById(R.id.btnDelete);
        ImageButton btnDownload = (ImageButton) view.findViewById(R.id.btnDownload);

        SourceItem sourceItem = listItem.getSourceItem();
        OfflineContentOptions offlineContentOptions = listItem.getOfflineContentOptions();

        title.setText(sourceItem.getTitle());
        if (offlineContentOptions != null)
        {
            btnDownload.setVisibility(View.VISIBLE);
            btnDownload.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    listItemActionListener.showSelectionDialog(listItem);
                }
            });

            // If any option is downloading, we show a progress in the list
            if (isDownloading(offlineContentOptions))
            {
                state.setText(String.format("Downloading - %.0f %%", listItem.getProgress()));
                state.setVisibility(View.VISIBLE);
                pauseResume.setImageResource(R.drawable.ic_pause_black_24dp);
                pauseResume.setVisibility(View.VISIBLE);
                pauseResume.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        listItemActionListener.suspend(listItem);
                    }
                });
            }
            else if (isSuspended(offlineContentOptions))
            {
                pauseResume.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                pauseResume.setVisibility(View.VISIBLE);
                pauseResume.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        listItemActionListener.resume(listItem);
                    }
                });
            }
            else
            {
                state.setVisibility(View.INVISIBLE);
                pauseResume.setVisibility(View.INVISIBLE);
            }
            // If any option is downloaded, we show the delete button
            if (hasDownloaded(offlineContentOptions))
            {
                btnDelete.setVisibility(View.VISIBLE);
                btnDelete.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        listItemActionListener.delete(listItem);
                    }
                });
            }
            else
            {
                btnDelete.setVisibility(View.GONE);
            }

        }
        else
        {
            // If no options are available, we hide the download and the delete button
            btnDelete.setVisibility(View.GONE);
            btnDownload.setVisibility(View.GONE);
        }

        return view;
    }

    /**
     * Returns true, if one {@link OfflineOptionEntry}s state is {@link OfflineOptionEntryState#DOWNLOADING}
     *
     * @param offlineContentOptions
     * @return true, if one {@link OfflineOptionEntry}s state is {@link OfflineOptionEntryState#DOWNLOADING}
     */
    private boolean isDownloading(OfflineContentOptions offlineContentOptions)
    {
        List<OfflineOptionEntry> allOfflineOptionEntries = Util.getAsOneList(offlineContentOptions);
        for (OfflineOptionEntry entry : allOfflineOptionEntries)
        {
            if (entry.getState() == OfflineOptionEntryState.DOWNLOADING)
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true, if one {@link OfflineOptionEntry}s state is {@link OfflineOptionEntryState#SUSPENDED}
     *
     * @param offlineContentOptions
     * @return true, if one {@link OfflineOptionEntry}s state is {@link OfflineOptionEntryState#SUSPENDED}
     */
    private boolean isSuspended(OfflineContentOptions offlineContentOptions)
    {
        List<OfflineOptionEntry> allOfflineOptionEntries = Util.getAsOneList(offlineContentOptions);
        for (OfflineOptionEntry entry : allOfflineOptionEntries)
        {
            if (entry.getState() == OfflineOptionEntryState.SUSPENDED)
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true, if one {@link OfflineOptionEntry}s state is {@link OfflineOptionEntryState#DOWNLOADED}
     *
     * @param offlineContentOptions
     * @return true, if one {@link OfflineOptionEntry}s state is {@link OfflineOptionEntryState#DOWNLOADED}
     */
    private boolean hasDownloaded(OfflineContentOptions offlineContentOptions)
    {
        List<OfflineOptionEntry> allOfflineOptionEntries = Util.getAsOneList(offlineContentOptions);
        for (OfflineOptionEntry entry : allOfflineOptionEntries)
        {
            if (entry.getState() == OfflineOptionEntryState.DOWNLOADED)
            {
                return true;
            }
        }
        return false;
    }
}
