package com.example.fall.game

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fall.databinding.ActivityGameBinding
import com.example.fall.game.logic.Game
import com.example.fall.game.logic.PlayerType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// GameActivity - the activity responsible for the game
//--------------------------------------------------------------------------------------------------
/** The activity which contains the surfaceView on which everything is drawn,
 *  the controller views, which with which the player moves/shoots, and the game itself.
 * */
class GameActivity : Activity() {

    private lateinit var binding: ActivityGameBinding
    private lateinit var game: Game
    private lateinit var type: PlayerType

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val playerType = intent.extras?.get("playerType")
        type = when (playerType) {
            0 -> PlayerType.Pistol
            1 -> PlayerType.Shotgun
            else -> PlayerType.Pistol
        }

        val load = Load(this)
    }

    /** Initiates/creates the game if the application has a valid OpenGL context.
     * */
    fun initGame() : Boolean {
        val glView = binding.glView
        val jMove = binding.joystickMove
        val jTurn = binding.joystickLook

        if (glView.getEglContInitialized()) {
            glView.queueEvent {
                game = Game(applicationContext, type)
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
//--------------------------------------------------------------------------------------------------
/** Loads the game (and all of its shaders and textures) once the application has
 * a valid OpenGL context.
 * */
class Load(activity: GameActivity) : ViewModel() {
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