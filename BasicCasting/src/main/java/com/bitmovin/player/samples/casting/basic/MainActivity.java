/*
 * Bitmovin Player Android SDK
 * Copyright (C) 2017, Bitmovin GmbH, All Rights Reserved
 *
 * This source code and its use and distribution, is subject to the terms
 * and conditions of the applicable license agreement.
 */

package com.bitmovin.player.samples.casting.basic;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bitmovin.player.cast.BitmovinCastManager;
import com.google.android.gms.cast.framework.CastButtonFactory;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    private ListView listView;
    private ListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Update the context the BitmovinCastManager is using
        // This should be done in every Activity's onCreate using the cast function
        BitmovinCastManager.getInstance().updateContext(this);

        //Setup ListView, ListAdapter and the ListItems
        List<ListItem> exampleListItems = getExampleListItems();
        this.listView = (ListView) findViewById(R.id.listview);
        this.listAdapter = new ListAdapter(this, 0, exampleListItems);
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                onListItemClicked((ListItem) parent.getItemAtPosition(position));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_activity_main, menu);

        // Adding a Cast Button in the menu bar
        CastButtonFactory.setUpMediaRouteButton(this, menu, R.id.media_route_menu_item);

        return true;
    }

    private void onListItemClicked(ListItem item)
    {
        Intent playerIntent = new Intent(this, PlayerActivity.class);
        playerIntent.putExtra(PlayerActivity.SOURCE_URL, item.getUrl());
        playerIntent.putExtra(PlayerActivity.SOURCE_TITLE, item.getTitle());
        startActivity(playerIntent);
    }

    private List<ListItem> getExampleListItems()
    {
        List<ListItem> items = new ArrayList<ListItem>();
        items.add(new ListItem("Sintel", "https://bitdash-a.akamaihd.net/content/sintel/sintel.mpd"));
        items.add(new ListItem("Art of Motion", "https://bitmovin-a.akamaihd.net/content/MI201109210084_1/mpds/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.mpd"));
        return items;
    }

    private static class ListItem
    {
        private String title;
        private String url;

        public ListItem(String title, String url)
        {
            this.title = title;
            this.url = url;
        }

        public String getTitle()
        {
            return title;
        }

        public String getUrl()
        {
            return url;
        }
    }

    private static final class ListAdapter extends ArrayAdapter<ListItem>
    {
        public ListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<ListItem> objects)
        {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
        {
            View view = convertView;

            if (view == null)
            {
                view = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            }

            ((TextView) view).setText(getItem(position).getTitle());
            return view;
        }
    }
}
