package com.example.fall

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.fall.databinding.PlayerChooserFragmentBinding
import com.example.fall.game.GameActivity

class ChoosePlayerFragment(private var name: String, private var weapon: String, private var playerType: Int) : Fragment() {
    private lateinit var binding: PlayerChooserFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = PlayerChooserFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.name.text = getString(R.string.char_name, name)
        binding.weapon.text = getString(R.string.char_weapon, weapon)

        when (playerType) {
            0 -> binding.charPic.setImageResource(R.drawable.pistolie)
            1 -> binding.charPic.setImageResource(R.drawable.shottie)
            else -> binding.charPic.setImageResource(R.drawable.pistolie)
        }

        binding.startButton.setOnClickListener {
            val intent = Intent(this.context, GameActivity::class.java)
            intent.putExtra("playerType", playerType)
            startActivity(intent)
        }
    }
}