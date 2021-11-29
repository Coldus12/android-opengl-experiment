package com.example.fall

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.example.fall.databinding.ActivityGameOverBinding

// GameOverActivity
//--------------------------------------------------------------------------------------------------
/** A simple Game Over screen which shows the final score, and the number of levels reached.
 *  For this activity to show the correct score and number of levels it needs extra data from the
 *  intent. The extra data required is as follows (string, value):
 *  ("score", actualScore) ; ("levels", nrOfLevels)
 * */
class GameOverActivity : Activity() {
    private lateinit var binding: ActivityGameOverBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGameOverBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.root.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        val score = intent.getIntExtra("score", 0)
        val levelsReached = intent.getIntExtra("levels", 0)

        val t1 = resources.getString(R.string.score, score)
        val t2 = resources.getString(R.string.levels_reached, levelsReached)

        binding.score.text = t1
        binding.levelsReached.text = t2
    }
}