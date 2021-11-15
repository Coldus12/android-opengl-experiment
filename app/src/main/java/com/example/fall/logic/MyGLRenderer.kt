package com.example.fall.logic

import android.graphics.Point
import android.graphics.PointF
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.util.Log
import com.example.fall.data.Playah
import com.example.fall.data.PlayerStates
import java.util.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.PI
import kotlin.random.Random

class MyGLRenderer : GLSurfaceView.Renderer {

    private lateinit var renderer: PlayerRenderer
    private lateinit var data: Playah
    private lateinit var square: Playah
    private lateinit var cam: Camera

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        GLES30.glClearColor(0.5f, 0.3f, 1.0f, 1.0f)

        data = Playah(
            PointF(0.0f,0.0f),
            "no_model",
            PlayerStates.standing,
            100,
            0f
        )

        square = Playah(
            PointF(0f, 0f),
            "no",
            PlayerStates.standing,
            100,
            (PI/4).toFloat()
        )

        renderer = PlayerRenderer()

        cam = Camera(data.position.x, data.position.y, 1f,1f)

        renderer.setViewProj(cam.getV(), cam.getP())
    }

    override fun onDrawFrame(unused: GL10) {
        // Redraw background color
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)

        cam.setPos(data.position.x, data.position.y)

        renderer.setViewProj(cam.getV(), cam.getP())
        renderer.draw(square, floatArrayOf(0.5f, 0f, 0.5f, 1f))
        renderer.draw(data)
    }

    fun changePos(x: Float, y: Float) {
        data.position.x = x
        data.position.y = y
    }

    fun rotate(angle: Float) {
        data.lookDirection = angle
    }

    fun deltaPos(x: Float, y: Float) {
        data.position.x += x
        data.position.y += y

        cam.setPos(data.position.x, data.position.y)
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)

        val ratio = width.toFloat() / height.toFloat()
        cam = Camera(data.position.x, data.position.y, ratio, 1f)

        renderer.setViewProj(cam.getV(), cam.getP())
        renderer.draw(square)
        renderer.draw(data)
    }
}