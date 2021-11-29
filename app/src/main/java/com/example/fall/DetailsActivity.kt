package com.example.fall

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.fall.databinding.ActivityDetailsBinding

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = intent.getStringExtra("name")
        val score = intent.getIntExtra("score", 0)
        val levelsReached = intent.getIntExtra("levels", 0)
        val resId = intent.getIntExtra("resId", R.drawable.pistolie)

        val t1 = resources.getString(R.string.score, score)
        val t2 = resources.getString(R.string.levels_reached, levelsReached)
        val t3 = resources.getString(R.string.char_name, name)

        binding.ivChar.setImageResource(resId)
        binding.tvName.text = t3
        binding.tvScore.text = t1
        binding.tvLevels.text = t2
    }
}