package com.example.fall

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.fall.databinding.ActivityMainBinding
import com.example.fall.game.GameActivity

class MainActivity : Activity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        binding.startButton.setOnClickListener {
            startActivity(Intent(this, ChoosePlayerActivity::class.java))
        }
    }
}