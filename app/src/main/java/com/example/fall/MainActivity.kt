package com.example.fall

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import com.example.fall.cemetery.CemeteryActivity
import com.example.fall.databinding.ActivityMainBinding
import com.example.fall.player_picker.ChoosePlayerActivity


// The main activity of the game.
//--------------------------------------------------------------------------------------------------
/** The main activity of the game.
 *  This activity has 2 buttons, a "start" button, and a "cemetery" button.
 *  The first one takes us to the character picker activity, where after selecting a character
 *  the actual game shall start.
 *  The second button transitions us to the CemeteryActivity, where all the dead characters are
 *  listed and shown in a recyclerView.
 * */
class MainActivity : Activity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        val animation = AnimationUtils.loadAnimation(this, R.anim.animation)

        setContentView(binding.root)
        binding.startButton.startAnimation(animation)
        binding.cemeteryButton.startAnimation(animation)
        binding.startButton.setOnClickListener {
            startActivity(Intent(this, ChoosePlayerActivity::class.java))
        }

        binding.cemeteryButton.setOnClickListener {
            startActivity(Intent(this, CemeteryActivity::class.java))
        }
    }
}