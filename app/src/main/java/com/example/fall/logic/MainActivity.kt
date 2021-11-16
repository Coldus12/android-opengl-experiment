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

    //private lateinit var gLView: GLSurfaceView
    private lateinit var binding: ActivityMainBinding

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val glView = binding.glView
        val jMove = binding.joystickMove
        val jTurn = binding.joystickLook

        jMove.setOnMoveListener { angle: Int, strength: Int ->
            val rad = PI/180.0 * angle

            val x = cos(rad) * (strength / 100.0) / 10.0 * 10
            val y = sin(rad) * (strength / 100.0) / 10.0 * 10

            glView.moveDelta(x.toFloat(),y.toFloat())
            //glView.requestRender()
        }

        jTurn.setOnMoveListener {angle: Int, strength: Int ->
            val rad = PI/180.0 * angle

            //Log.i("[LOG]","Rad $rad")
            if (strength > 50)
                glView.rot(rad.toFloat())

            //glView.requestRender()
        }

        val draw = Draw(glView)
    }
}

class Draw(glView: MyGLSurfaceView): ViewModel() {
    init {
        viewModelScope.launch {
            while(true) {
                if (glView.readyToDraw())
                    glView.requestRender()

                delay(1)
            }
        }
    }
}