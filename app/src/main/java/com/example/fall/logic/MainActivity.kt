package com.example.fall.logic

import android.app.Activity
import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fall.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : Activity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var game: Game

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val load = Load(this)
    }

    fun initGame() : Boolean {
        val glView = binding.glView
        val jMove = binding.joystickMove
        val jTurn = binding.joystickLook

        if (glView.getEglContInitialized()) {
            glView.queueEvent {
                game = Game(applicationContext)
                game.setGLView(glView)
                glView.setGraphicalGameInterface(game)
            }
        }

        jMove.setOnMoveListener { angle: Int, strength: Int ->
            val rad = PI/180.0 * angle

            val x = cos(rad) * (strength / 100.0)
            val y = sin(rad) * (strength / 100.0)

            game.player.move(game, x.toFloat(), y.toFloat())
        }

        jTurn.setOnMoveListener {angle: Int, strength: Int ->
            val rad = PI/180.0 * angle

            if (strength > 25) {
                game.player.rotate(rad.toFloat())
                game.player.shoot(game)
            }
        }

        return glView.getEglContInitialized()
    }
}

// Loading the Game class
class Load(activity: MainActivity) : ViewModel() {
    init {
        viewModelScope.launch {
            var run = true

            while (run) {
                run = !activity.initGame()
                delay(5)
            }
        }
    }
}