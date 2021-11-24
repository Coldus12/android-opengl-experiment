package com.example.fall.logic

import android.app.Activity
import android.os.Bundle
import android.util.Log
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
        val draw = Draw(binding.glView)
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

            val x = cos(rad) * (strength / 100.0) / 10.0 * 10
            val y = sin(rad) * (strength / 100.0) / 10.0 * 10

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

// Coroutine to draw the scene every x ms
class Draw(glView: MyGLSurfaceView): ViewModel() {
    init {
        viewModelScope.launch {
            //var start = System.currentTimeMillis()
            //var timeItTook = System.currentTimeMillis()
            while(true) {

                if (glView.getReadyToDraw()) {
                    //timeItTook = System.currentTimeMillis() - start
                    //Log.i("[LOOG]","Time it took: $timeItTook, fps: ${1000/(timeItTook+1)}")
                    glView.requestRender()
                    //start = System.currentTimeMillis()
                }

                delay(1)
            }
        }
    }
}