/*
 * Bitmovin Player Android SDK
 * Copyright (C) 2017, Bitmovin GmbH, All Rights Reserved
 *
 * This source code and its use and distribution, is subject to the terms
 * and conditions of the applicable license agreement.
 */

package com.bitmovin.player.samples.metadata.basic;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

import com.bitmovin.player.BitmovinPlayer;
import com.bitmovin.player.BitmovinPlayerView;
import com.bitmovin.player.api.event.data.MetadataEvent;
import com.bitmovin.player.api.event.data.MetadataParsedEvent;
import com.bitmovin.player.api.event.listener.OnMetadataListener;
import com.bitmovin.player.api.event.listener.OnMetadataParsedListener;
import com.bitmovin.player.config.media.SourceItem;
import com.bitmovin.player.model.Metadata;
import com.bitmovin.player.model.emsg.EventMessage;
import com.bitmovin.player.model.id3.Id3Frame;
import com.bitmovin.player.model.scte.ScteMessage;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity
{

    private BitmovinPlayerView bitmovinPlayerView;
    private BitmovinPlayer bitmovinPlayer;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.bitmovinPlayerView = (BitmovinPlayerView) this.findViewById(R.id.bitmovinPlayerView);
        this.bitmovinPlayer = this.bitmovinPlayerView.getPlayer();

        this.gson = new Gson();
        // Adding the metadata listener to the player
        this.bitmovinPlayer.addEventListener(metadataParsedListener);
        this.bitmovinPlayer.addEventListener(metadataListener);

        this.initializePlayer();
    }

    @Override
    protected void onStart()
    {
        this.bitmovinPlayerView.onStart();
        super.onStart();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        this.bitmovinPlayerView.onResume();
    }

    @Override
    protected void onPause()
    {
        this.bitmovinPlayerView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop()
    {
        this.bitmovinPlayerView.onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        // Removing metadata listener from player
        this.bitmovinPlayer.removeEventListener(metadataParsedListener);
        this.bitmovinPlayer.removeEventListener(metadataListener);
        this.bitmovinPlayerView.onDestroy();

        super.onDestroy();
    }

    protected void initializePlayer()
    {
        //TODO: add source containing metadata
        this.bitmovinPlayer.load(new SourceItem(""));
    }

    private void logMetadata(MetadataEvent metadataEvent) {
        Metadata metadata = metadataEvent.getMetadata();

        switch (metadataEvent.getType())
        {
            case ScteMessage.TYPE:
                for (int i = 0; i < metadata.length(); i++)
                {
                    ScteMessage scteMessage = (ScteMessage) metadata.get(i);

                    Log.i("METADATA", "SCTE: " + gson.toJson(scteMessage));
                }
                break;
            case Id3Frame.TYPE:
                for (int i = 0; i < metadata.length(); i++)
                {
                    Id3Frame id3Frame = (Id3Frame) metadata.get(i);

                    Log.i("METADATA", "ID3Frame: " + gson.toJson(id3Frame));
                }
                break;
            case EventMessage.TYPE:
                for (int i = 0; i < metadata.length(); i++)
                {
                    EventMessage eventMessage = (EventMessage) metadata.get(i);

                    Log.i("METADATA", "EMSG: " + gson.toJson(eventMessage));
                }
                break;
        }
    }

    // Metadata Listener
    private OnMetadataListener metadataListener = new OnMetadataListener()
    {
        @Override
        public void onMetadata(MetadataEvent metadataEvent)
        {
            Log.i("METADATA", "onMetadata:");
            logMetadata(metadataEvent);
        }
    };

    // Parsed Metadata Listener
    private OnMetadataParsedListener metadataParsedListener = new OnMetadataParsedListener()
    {
        @Override
        public void onMetadataParsed(MetadataParsedEvent metadataEvent)
        {
            Log.i("METADATA", "onMetadataParsed:");
            logMetadata(metadataEvent);
        }
    };
}
