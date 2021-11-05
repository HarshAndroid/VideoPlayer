package com.harshRajpurohit.videoPlayer

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.harshRajpurohit.videoPlayer.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private lateinit var runnable: Runnable

    companion object{
        private lateinit var player: SimpleExoPlayer
        lateinit var playerList: ArrayList<Video>
        var position: Int = -1
        private var repeat: Boolean = false
        private var isFullscreen: Boolean = false
        private var isLocked: Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setTheme(R.style.playerActivityTheme)
        setContentView(binding.root)

        //for immersive mode
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.root).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        initializeLayout()
        initializeBinding()
    }

    private fun initializeLayout(){
        when(intent.getStringExtra("class")){
            "AllVideos" -> {
                playerList = ArrayList()
                playerList.addAll(MainActivity.videoList)
                createPlayer()
            }
            "FolderActivity" -> {
                playerList = ArrayList()
                playerList.addAll(FoldersActivity.currentFolderVideos)
                createPlayer()
            }
        }
        if(repeat) binding.repeatBtn.setImageResource(R.drawable.exo_controls_repeat_all)
        else binding.repeatBtn.setImageResource(R.drawable.exo_controls_repeat_off)
    }
    private fun initializeBinding(){
        binding.backBtn.setOnClickListener {
            finish()
        }
        binding.playPauseBtn.setOnClickListener {
            if(player.isPlaying) pauseVideo()
            else playVideo()
        }
        binding.nextBtn.setOnClickListener { nextPrevVideo() }
        binding.prevBtn.setOnClickListener { nextPrevVideo(isNext = false) }
        binding.repeatBtn.setOnClickListener {
            if(repeat){
                repeat = false
                player.repeatMode = Player.REPEAT_MODE_OFF
                binding.repeatBtn.setImageResource(R.drawable.exo_controls_repeat_off)
            }
            else{
                repeat = true
                player.repeatMode = Player.REPEAT_MODE_ONE
                binding.repeatBtn.setImageResource(R.drawable.exo_controls_repeat_all)
            }
        }
        binding.fullScreenBtn.setOnClickListener {
            if(isFullscreen){
                isFullscreen = false
                playInFullscreen(enable = false)
            }else{
                isFullscreen = true
                playInFullscreen(enable = true)
            }
        }
        binding.lockButton.setOnClickListener {
            if(!isLocked){
                //for hiding
                isLocked = true
                binding.playerView.hideController()
                binding.playerView.useController = false
                binding.lockButton.setImageResource(R.drawable.close_lock_icon)
            }
            else{
                //for showing
                isLocked = false
                binding.playerView.useController = true
                binding.playerView.showController()
                binding.lockButton.setImageResource(R.drawable.lock_open_icon)
            }
        }
    }
    private fun createPlayer(){
        try { player.release() }catch (e: Exception){}
        binding.videoTitle.text = playerList[position].title
        binding.videoTitle.isSelected = true
        player = SimpleExoPlayer.Builder(this).build()
        binding.playerView.player = player
        val mediaItem = MediaItem.fromUri(playerList[position].artUri)
        player.setMediaItem(mediaItem)
        player.prepare()
        playVideo()
        player.addListener(object : Player.Listener{
            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                if(playbackState == Player.STATE_ENDED) nextPrevVideo()
            }
        })
        playInFullscreen(enable = isFullscreen)
        setVisibility()
    }
    private fun playVideo(){
        binding.playPauseBtn.setImageResource(R.drawable.pause_icon)
        player.play()
    }
    private fun pauseVideo(){
        binding.playPauseBtn.setImageResource(R.drawable.play_icon)
        player.pause()
    }
    private fun nextPrevVideo(isNext: Boolean = true){
        if(isNext) setPosition()
        else setPosition(isIncrement = false)
        createPlayer()
    }
    private fun setPosition(isIncrement: Boolean = true){
        if(!repeat){
            if(isIncrement){
                if(playerList.size -1 == position)
                    position = 0
                else ++position
            }else{
                if(position  == 0)
                    position = playerList.size - 1
                else --position
            }
        }
    }
    private fun playInFullscreen(enable: Boolean){
        if(enable){
            binding.playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
            player.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
            binding.fullScreenBtn.setImageResource(R.drawable.fullscreen_exit_icon)
        }else{
            binding.playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
            player.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT
            binding.fullScreenBtn.setImageResource(R.drawable.fullscreen_icon)
        }
    }

    private fun setVisibility(){
        runnable = Runnable {
            if(binding.playerView.isControllerVisible) changeVisibility(View.VISIBLE)
            else changeVisibility(View.INVISIBLE)
            Handler(Looper.getMainLooper()).postDelayed(runnable, 300)
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable, 0)
    }
    private fun changeVisibility(visibility: Int){
        binding.topController.visibility = visibility
        binding.bottomController.visibility = visibility
        binding.playPauseBtn.visibility = visibility
        if(isLocked) binding.lockButton.visibility = View.VISIBLE
        else binding.lockButton.visibility = visibility
    }
    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}