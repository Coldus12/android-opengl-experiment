package com.example.fall.logic

import android.app.Activity
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.util.Log
import com.example.fall.R
import com.example.fall.databinding.ActivityMainBinding
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : Activity() {

    //private lateinit var gLView: GLSurfaceView
    private lateinit var binding: ActivityMainBinding

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity.
        //gLView = MyGLSurfaceView(this)
        setContentView(binding.root)

        val glView = binding.glView
        val jMove = binding.joystickMove
        val jTurn = binding.joystickLook

        jMove.setOnMoveListener { angle: Int, strength: Int ->
            val rad = PI/180.0 * angle

            val x = cos(rad) * (strength / 100.0) / 10.0
            val y = sin(rad) * (strength / 100.0) / 10.0

            //Log.i("[LOG]","angle $angle strength $strength rad $rad x $x y $y")
            //tv.text = "angle $angle strength $strength rad $rad x $x y $y"

            glView.moveDelta(x.toFloat(),y.toFloat())
        }

        jTurn.setOnMoveListener {angle: Int, strength: Int ->
            val rad = PI/180.0 * angle

            Log.i("[LOG]","Rad $rad")
            if (strength > 50)
                glView.rot(rad.toFloat())
        }
    }
}