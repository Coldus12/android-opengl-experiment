package com.example.fall

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.fall.databinding.ActivityGameOverBinding

class GameOverActivity : Activity() {

    private lateinit var binding: ActivityGameOverBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGameOverBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.root.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        val score = intent.getIntExtra("score", 0)
        val levelsReached = intent.getIntExtra("levels", 0)

        val t1 = resources.getString(R.string.score, score)
        val t2 = resources.getString(R.string.levels_reached, levelsReached)

        binding.score.text = t1
        binding.levelsReached.text = t2
    }
}