package com.example.fall.player_picker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.fall.databinding.ActivityChoosePlayerBinding

// Player picker activity
//--------------------------------------------------------------------------------------------------
/** Simple activity with the character pager on it.
 * */
class ChoosePlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChoosePlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChoosePlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.vp.adapter = CharacterPagerAdapter(supportFragmentManager)
    }
}