package com.harshRajpurohit.videoPlayer

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.harshRajpurohit.videoPlayer.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.themesList[MainActivity.themeIndex])
        val binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "About"
        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.aboutText.text =
            "Developed By: Harsh H. Rajpurohit \n\nIf you want to provide feedback, I will love to hear that."
    }
}