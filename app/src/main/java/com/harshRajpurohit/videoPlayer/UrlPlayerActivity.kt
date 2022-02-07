package com.harshRajpurohit.videoPlayer

import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.material.snackbar.Snackbar
import com.harshRajpurohit.videoPlayer.databinding.ActivityUrlPlayerBinding


class UrlPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUrlPlayerBinding

    companion object{
        private lateinit var player: ExoPlayer
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        setTheme(R.style.playerActivityTheme)
        binding = ActivityUrlPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //for immersive mode
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.root).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        try{createPlayer()}catch (e: Exception){
            Snackbar.make(binding.root, e.localizedMessage!!.toString(), 10000).show()
        }
    }

    private fun createPlayer(){
        player = ExoPlayer.Builder(this)
            .build()
        val mediaItem = MediaItem.fromUri(UrlActivity.linkToPlay)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.playWhenReady = true
        binding.styledPlayerView.player = player
        binding.styledPlayerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
        player.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT

        //for removing exoplayer's custom layout buttons
        binding.styledPlayerView.setShowPreviousButton(false)
        binding.styledPlayerView.setShowNextButton(false)
        binding.styledPlayerView.setShowRewindButton(false)
        binding.styledPlayerView.setShowFastForwardButton(false)

    }
}