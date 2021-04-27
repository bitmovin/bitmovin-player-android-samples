package com.bitmovin.player.samples.custom.ui.html

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cardView.setOnClickListener {
            startActivity(Intent(this@MainActivity, PlaybackActivity::class.java))
        }
    }
}
