package com.example.fall.logic

import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.util.Log
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyGLRenderer() : GLSurfaceView.Renderer {

    private var game: IGraphicalGame? = null
    private var ready = false
    private var eglContextInitialized = false

    private var width: Int = -1
    private var height: Int = -1

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        GLES30.glClearColor(0.5f, 0.3f, 1.0f, 1.0f)
        game?.render()
        eglContextInitialized = true
    }

    override fun onDrawFrame(unused: GL10) {
        ready = false

        // Redraw background color
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
        game?.render()

        ready = true
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)

        this.width = width
        this.height = height

        game?.setCameraSize(width.toFloat(), height.toFloat())
        game?.render()
    }

    fun readyToDraw(): Boolean {
        return ready
    }

    fun getContextInitialized(): Boolean {
        return eglContextInitialized
    }

    fun setGraphicalGameInterface(game: IGraphicalGame) {
        this.game = game
        game.setCameraSize(width.toFloat(), height.toFloat())
        game.render()
        Log.i("[LOOG]","$width and $height")
    }
}