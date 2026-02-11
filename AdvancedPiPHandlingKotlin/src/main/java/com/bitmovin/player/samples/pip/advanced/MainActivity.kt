package com.bitmovin.player.samples.pip.advanced

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.bitmovin.player.samples.pip.advanced.databinding.ActivityMainBinding
import com.bitmovin.player.samples.pip.advanced.databinding.ListItemVideoBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var listAdapter: VideoListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(insets.left, insets.top, insets.right, insets.bottom)
            WindowInsetsCompat.CONSUMED
        }
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }

        listAdapter = VideoListAdapter(this, 0, VideoCatalog.items)
        binding.videoList.adapter = listAdapter
        binding.videoList.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            val item = parent.getItemAtPosition(position) as VideoItem
            val intent = PlayerActivity.newIntent(this, item).apply {
                addFlags(android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                addFlags(android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
            startActivity(intent)
        }
    }

    private class VideoListAdapter(
        context: Context,
        @LayoutRes resource: Int,
        items: List<VideoItem>,
    ) : ArrayAdapter<VideoItem>(context, resource, items) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val binding = convertView?.let { ListItemVideoBinding.bind(it) }
                ?: ListItemVideoBinding.inflate(LayoutInflater.from(context), parent, false)

            val item = getItem(position)
            binding.videoTitle.text = item?.title

            val description = item?.description
            if (description.isNullOrBlank()) {
                binding.videoDescription.visibility = View.GONE
            } else {
                binding.videoDescription.visibility = View.VISIBLE
                binding.videoDescription.text = description
            }

            return binding.root
        }
    }
}
