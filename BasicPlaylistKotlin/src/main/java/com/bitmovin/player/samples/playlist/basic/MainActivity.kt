package com.bitmovin.player.samples.playlist.basic

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bitmovin.player.BitmovinPlayer
import com.bitmovin.player.api.event.listener.OnPlayListener
import com.bitmovin.player.api.event.listener.OnPlaybackFinishedListener
import com.bitmovin.player.api.event.listener.OnReadyListener
import com.bitmovin.player.config.media.SourceItem
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var bitmovinPlayer: BitmovinPlayer? = null

    // Holds all items of the playlist
    private var playlistItems = ArrayList<PlaylistItem>()
    // Stores the index of the next playlist item to be played
    private var nextPlaylistItem = 0
    private var lastItemFinished = false
    private var playlistStarted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.bitmovinPlayer = bitmovinPlayerView.player
        this.playlistItems.addAll(getPlaylistItems())

        this.addListenersToPlayer()

        // Start the playlist
        this.playNextItem()
    }

    private fun getPlaylistItems(): List<PlaylistItem> = listOf(
            PlaylistItem("Art of Motion", "https://bitmovin-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8"),
            PlaylistItem("Sintel", "https://bitmovin-a.akamaihd.net/content/sintel/hls/playlist.m3u8")
    )

    /**
     * Plays the next playlist item in the playlist.
     */
    private fun playNextItem() {
        // check if there are unplayed items in the playlist
        if (nextPlaylistItem >= this.playlistItems.size) {
            return
        }
        // fetch the next item to play from the playlist
        val itemToPlay = this.playlistItems[nextPlaylistItem]
        nextPlaylistItem += 1

        // Create a source item based on the playlist item and load it
        val sourceItem = SourceItem(itemToPlay.url)
        sourceItem.title = itemToPlay.title

        // load the new source item
        this.bitmovinPlayer?.load(sourceItem)
    }

    private val onPlayListener = OnPlayListener {
        // Remember that the playlist was started by the user
        playlistStarted = true

        // When the replay button in the UI was tapped or a player.play() API call was issued after the last
        // playlist item has finished, we repeat the whole playlist instead of just repeating the last item
        if (lastItemFinished) {
            // Unload the last played item and reset the playlist state
            bitmovinPlayer?.unload()
            lastItemFinished = false
            nextPlaylistItem = 0
            // Restart playlist with first item in list
            playNextItem()
        }
    }

    private val onReadyListener = OnReadyListener {
        // Autoplay all playlist items after the initial playlist item was started by either tapping the
        // play button or by issuing the player.play() API call.
        if (playlistStarted) {
            bitmovinPlayer?.play()
        }
    }

    private val onPlaybackFinishedListener = OnPlaybackFinishedListener {
        // Automatically play next item in the playlist if there are still unplayed items left
        lastItemFinished = nextPlaylistItem >= playlistItems.size
        if (!lastItemFinished) {
            playNextItem()
        }
    }

    override fun onStart() {
        bitmovinPlayerView.onStart()
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        bitmovinPlayerView.onResume()
    }

    override fun onPause() {
        bitmovinPlayerView.onPause()
        super.onPause()
    }

    override fun onStop() {
        bitmovinPlayerView.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        bitmovinPlayerView.onDestroy()
        super.onDestroy()
    }

    private fun addListenersToPlayer() {
        this.bitmovinPlayer?.addEventListener(this.onReadyListener)
        this.bitmovinPlayer?.addEventListener(this.onPlayListener)
        this.bitmovinPlayer?.addEventListener(this.onPlaybackFinishedListener)
    }

    // A simple class defining a playlist item
    private data class PlaylistItem(val title: String, val url: String)

}
