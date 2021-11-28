package com.example.fall

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.fall.databinding.ActivityChoosePlayerBinding

class ChoosePlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChoosePlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChoosePlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.vp.adapter = CharacterPagerAdapter(supportFragmentManager)
    }
}