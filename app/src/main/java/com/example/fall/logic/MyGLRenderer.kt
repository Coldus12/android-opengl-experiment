package com.example.fall.logic

import android.content.Context
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyGLRenderer(private val context: Context, private var glView: GLSurfaceView) : GLSurfaceView.Renderer {

    private lateinit var game: Game
    private var ready = false

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        GLES30.glClearColor(0.5f, 0.3f, 1.0f, 1.0f)
        game = Game(context)
        game.setGLView(glView)
        game.render()
    }

    override fun onDrawFrame(unused: GL10) {
        ready = false

        // Redraw background color
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
        game.render()

        ready = true
    }

    fun rotate(angle: Float) {
        game.rotatePlayer(angle)
    }

    fun deltaPos(x: Float, y: Float) {
        game.movePlayer(x,y)
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
        game.setCameraSize(width.toFloat(), height.toFloat())
        game.render()
    }

    fun readyToDraw(): Boolean {
        return ready
    }
}