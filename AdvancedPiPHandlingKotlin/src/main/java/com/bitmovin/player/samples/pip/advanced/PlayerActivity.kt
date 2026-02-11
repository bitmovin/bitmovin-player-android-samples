package com.bitmovin.player.samples.pip.advanced

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.bitmovin.player.api.PlaybackConfig
import com.bitmovin.player.api.Player
import com.bitmovin.player.api.PlayerConfig
import com.bitmovin.player.api.source.SourceConfig
import com.bitmovin.player.api.source.SourceType
import com.bitmovin.player.samples.pip.advanced.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding
    private lateinit var player: Player
    private lateinit var pictureInPictureHandler: CustomPictureInPictureHandler
    private var currentItem: VideoItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
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
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        player = Player(
            context = this,
            playerConfig = PlayerConfig(
                playbackConfig = PlaybackConfig(isAutoplayEnabled = true),
            ),
        )
        binding.playerView.player = player

        handleIntent(intent)

        pictureInPictureHandler = CustomPictureInPictureHandler(this, player, binding.playerView)
        binding.playerView.setPictureInPictureHandler(pictureInPictureHandler)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                enterPipOrFinish()
            }
        })
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    override fun onStart() {
        super.onStart()
        binding.playerView.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.playerView.onResume()
    }

    override fun onPause() {
        binding.playerView.onPause()
        super.onPause()
    }

    override fun onStop() {
        binding.playerView.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        pictureInPictureHandler.dispose()
        binding.playerView.onDestroy()
        super.onDestroy()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            enterPipOrFinish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun enterPipOrFinish() {
        if (pictureInPictureHandler.isPictureInPictureAvailable) {
            pictureInPictureHandler.enterPictureInPicture()
        } else {
            finish()
        }
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        // CustomPictureInPictureHandler sets setAutoEnterEnabled(true) on supported Android versions (S and above), 
        // so we only need to call enterPictureInPicture() for older versions to auto enter PiP.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S && player.isPlaying) {
            pictureInPictureHandler.enterPictureInPicture()
        }
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        if (isInPictureInPictureMode) {
            supportActionBar?.hide()
        } else {
            supportActionBar?.show()
        }
        binding.playerView.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
    }

    private fun readVideo(intent: Intent?): VideoItem? {
        if (intent == null) return null
        val title = intent.getStringExtra(EXTRA_TITLE) ?: return null
        val url = intent.getStringExtra(EXTRA_URL) ?: return null
        val typeName = intent.getStringExtra(EXTRA_SOURCE_TYPE) ?: return null
        val sourceType = runCatching { SourceType.valueOf(typeName) }.getOrNull() ?: return null
        val description = intent.getStringExtra(EXTRA_DESCRIPTION)
        return VideoItem(
            title = title,
            description = description,
            url = url,
            sourceType = sourceType,
        )
    }

    private fun handleIntent(intent: Intent?) {
        val requested = readVideo(intent) ?: return
        title = requested.title
        if (currentItem == requested) return
        currentItem = requested
        val source = SourceConfig(
            requested.url,
            type = requested.sourceType,
            title = requested.title,
            description = requested.description,
        )
        player.load(source)
    }

    companion object {
        private const val EXTRA_TITLE = "extra_title"
        private const val EXTRA_DESCRIPTION = "extra_description"
        private const val EXTRA_URL = "extra_url"
        private const val EXTRA_SOURCE_TYPE = "extra_source_type"

        fun newIntent(context: Context, item: VideoItem): Intent {
            return Intent(context, PlayerActivity::class.java).apply {
                putExtra(EXTRA_TITLE, item.title)
                putExtra(EXTRA_DESCRIPTION, item.description)
                putExtra(EXTRA_URL, item.url)
                putExtra(EXTRA_SOURCE_TYPE, item.sourceType.name)
            }
        }
    }
}
