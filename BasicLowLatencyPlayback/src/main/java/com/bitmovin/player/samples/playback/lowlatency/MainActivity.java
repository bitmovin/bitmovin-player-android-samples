/*
 * Bitmovin Player Android SDK
 * Copyright (C) 2019, Bitmovin GmbH, All Rights Reserved
 *
 * This source code and its use and distribution, is subject to the terms
 * and conditions of the applicable license agreement.
 */

package com.bitmovin.player.samples.playback.lowlatency;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.bitmovin.player.config.drm.WidevineConfiguration;
import com.bitmovin.player.config.media.DASHSource;
import com.bitmovin.player.config.media.SourceItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    private EditText streamBox;
    private EditText drmBox;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        this.streamBox = this.findViewById(R.id.streamBox);
        this.drmBox = this.findViewById(R.id.drmBox);

        ListView listView = this.findViewById(R.id.list);

        List<SourceItem> sourceItems = new ArrayList<>();
        SourceItem sourceItem = new SourceItem(new DASHSource("https://akamaibroadcasteruseast.akamaized.net/cmaf/live/657078/akasource/out.mpd"));
        sourceItem.setTitle("Akamai out.mpd, DASH");
        sourceItems.add(sourceItem);

        SourceItemAdapter sourceItemAdapter = new SourceItemAdapter(this, android.R.layout.simple_list_item_1, sourceItems);
        listView.setAdapter(sourceItemAdapter);

        AdapterView.OnItemClickListener onItemClickListener= new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                MainActivity.this.onListItemClicked((SourceItem) parent.getItemAtPosition(position));
            }
        };
        listView.setOnItemClickListener(onItemClickListener);

        listView.requestFocus();
    }

    private void onListItemClicked(SourceItem listItem)
    {
        this.playSource(listItem);
    }

    private void playSource(SourceItem sourceItem)
    {
        String drm = null;
        WidevineConfiguration widevineConfiguration = (WidevineConfiguration) sourceItem.getDrmConfiguration(WidevineConfiguration.UUID);
        if (widevineConfiguration != null)
        {
            drm = widevineConfiguration.getLicenseUrl();
        }
        this.play(sourceItem.getDashSource().getUrl(), drm);
    }

    public void play(View view)
    {
        String stream = this.streamBox.getText().toString();
        String drm = this.drmBox.getText().toString();

        this.play(stream, drm);
    }

    public void play(String stream, String drm)
    {
        Intent intent = new Intent(this, PlayerActivity.class);
        intent.putExtra(PlayerActivity.STREAM, stream);
        if (drm != null && !TextUtils.isEmpty(drm))
        {
            intent.putExtra(PlayerActivity.DRM, drm);
        }
        this.startActivity(intent);
    }

    public class SourceItemAdapter extends ArrayAdapter<SourceItem>
    {
        public SourceItemAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<SourceItem> objects)
        {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent)
        {
            View view = convertView;

            if (view == null)
            {
                view = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            }
            final SourceItem listItem = getItem(position);

            ((TextView) view).setText(listItem.getTitle());
            return view;
        }
    }
}
