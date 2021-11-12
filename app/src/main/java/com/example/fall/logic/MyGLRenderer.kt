package com.example.fall.logic

import android.graphics.Point
import android.graphics.PointF
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import com.example.fall.data.Playah
import com.example.fall.data.PlayerStates
import java.util.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.random.Random

class MyGLRenderer : GLSurfaceView.Renderer {

    //private lateinit var mTriangle: Triangle
    //lateinit var rect: Rectangle
    private lateinit var renderer: PlayerRenderer
    private lateinit var data: Playah

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        GLES30.glClearColor(0.5f, 0.3f, 1.0f, 1.0f)

        data = Playah(
            PointF(0.0f,0.0f),
            "no_model",
            PlayerStates.standing,
            100,
            Point(1,0)
        )

        renderer = PlayerRenderer()
    }

    override fun onDrawFrame(unused: GL10) {
        // Redraw background color
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
        renderer.changeData(data)

        renderer.draw()
    }

    fun changePos(x: Float, y: Float) {
        data.position.x = x
        data.position.y = y
        renderer.changeData(data)
    }

    fun rotate(angle: Float) {
        renderer.rotate(angle)
    }

    fun deltaPos(x: Float, y: Float) {
        data.position.x += x
        data.position.y += y
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)

        val ratio = width.toFloat() / height.toFloat()
        PlayerRenderer.screenRatio = ratio
        renderer.changeData(data)

        renderer.draw()
    }
}