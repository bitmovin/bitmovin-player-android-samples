/*
 * Bitmovin Player Android SDK
 * Copyright (C) 2017, Bitmovin GmbH, All Rights Reserved
 *
 * This source code and its use and distribution, is subject to the terms
 * and conditions of the applicable license agreement.
 */

package com.bitmovin.player.samples.metadata.basic;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.bitmovin.player.PlayerView;
import com.bitmovin.player.api.Player;
import com.bitmovin.player.api.event.EventListener;
import com.bitmovin.player.api.event.PlayerEvent;
import com.bitmovin.player.api.event.SourceEvent;
import com.bitmovin.player.api.metadata.Metadata;
import com.bitmovin.player.api.metadata.emsg.EventMessage;
import com.bitmovin.player.api.metadata.id3.Id3Frame;
import com.bitmovin.player.api.metadata.scte.ScteMessage;
import com.bitmovin.player.api.source.SourceConfig;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {

    private PlayerView playerView;
    private Player player;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.playerView = this.findViewById(R.id.bitmovinPlayerView);
        this.player = this.playerView.getPlayer();

        this.gson = new Gson();
        // Adding the metadata listener to the player
        this.player.on(SourceEvent.MetadataParsed.class, metadataParsedListener);
        this.player.on(PlayerEvent.Metadata.class, metadataListener);

        this.initializePlayer();
    }

    @Override
    protected void onStart() {
        this.playerView.onStart();
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.playerView.onResume();
    }

    @Override
    protected void onPause() {
        this.playerView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        this.playerView.onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // Removing metadata listener from player
        this.player.off(metadataParsedListener);
        this.player.off(metadataListener);
        this.playerView.onDestroy();

        super.onDestroy();
    }

    protected void initializePlayer() {
        //TODO: add source containing metadata
        this.player.load(SourceConfig.fromUrl(""));
    }

    private void logMetadata(Metadata metadata, String type) {
        switch (type) {
            case ScteMessage.TYPE:
                for (int i = 0; i < metadata.length(); i++) {
                    ScteMessage scteMessage = (ScteMessage) metadata.get(i);

                    Log.i("METADATA", "SCTE: " + gson.toJson(scteMessage));
                }
                break;
            case Id3Frame.TYPE:
                for (int i = 0; i < metadata.length(); i++) {
                    Id3Frame id3Frame = (Id3Frame) metadata.get(i);

                    Log.i("METADATA", "ID3Frame: " + gson.toJson(id3Frame));
                }
                break;
            case EventMessage.TYPE:
                for (int i = 0; i < metadata.length(); i++) {
                    EventMessage eventMessage = (EventMessage) metadata.get(i);

                    Log.i("METADATA", "EMSG: " + gson.toJson(eventMessage));
                }
                break;
        }
    }

    // Metadata Listener
    private final EventListener<PlayerEvent.Metadata> metadataListener = metadata -> {
        Log.i("METADATA", "onMetadata:");
        logMetadata(metadata.getMetadata(), metadata.getType());
    };

    // Parsed Metadata Listener
    private final EventListener<SourceEvent.MetadataParsed> metadataParsedListener = metadataParsed -> {
        Log.i("METADATA", "onMetadataParsed:");
        logMetadata(metadataParsed.getMetadata(), metadataParsed.getType());
    };
}
