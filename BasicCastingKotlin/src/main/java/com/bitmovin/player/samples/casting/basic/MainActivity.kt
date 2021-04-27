package com.bitmovin.player.samples.casting.basic

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import com.bitmovin.player.casting.BitmovinCastManager
import com.google.android.gms.cast.framework.CastButtonFactory
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Update the context the BitmovinCastManager is using
        // This should be done in every Activity's onCreate using the cast function
        BitmovinCastManager.getInstance().updateContext(this)

        //Setup ListView, ListAdapter and the ListItems
        val exampleListItems = getExampleListItems()
        listview.adapter = ListAdapter(this, 0, exampleListItems)
        listview.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            onListItemClicked(parent.getItemAtPosition(position) as ListItem)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_activity_main, menu)

        // Adding a Cast Button in the menu bar
        CastButtonFactory.setUpMediaRouteButton(this, menu, R.id.media_route_menu_item)

        return true
    }

    private fun onListItemClicked(item: ListItem) {
        val playerIntent = Intent(this, PlayerActivity::class.java)
        playerIntent.putExtra(SOURCE_URL, item.url)
        playerIntent.putExtra(SOURCE_TITLE, item.title)
        startActivity(playerIntent)
    }

    private fun getExampleListItems(): List<ListItem> = listOf(
            ListItem("Sintel", "https://bitdash-a.akamaihd.net/content/sintel/sintel.mpd"),
            ListItem("Art of Motion", "https://bitmovin-a.akamaihd.net/content/MI201109210084_1/mpds/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.mpd")
    )

    private data class ListItem(val title: String, val url: String)

    private class ListAdapter(context: Context, @LayoutRes resource: Int, objects: List<ListItem>) : ArrayAdapter<ListItem>(context, resource, objects) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var view = convertView

            if (view == null) {
                view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false)
            }

            (view as TextView).text = getItem(position)?.title
            return view
        }
    }

}
