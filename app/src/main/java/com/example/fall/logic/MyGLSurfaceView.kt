package com.example.fall.logic

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet

class MyGLSurfaceView(context: Context, attrs: AttributeSet) : GLSurfaceView(context,attrs) {
    private val renderer: MyGLRenderer

    init {
        setEGLContextClientVersion(3)

        renderer = MyGLRenderer()
        setRenderer(renderer)
        renderMode = RENDERMODE_WHEN_DIRTY
    }

    fun setGraphicalGameInterface(game: IGraphicalGame) {
        renderer.setGraphicalGameInterface(game)
    }

    fun getEglContInitialized(): Boolean {
        return renderer.getContextInitialized()
    }

    fun getReadyToDraw() : Boolean {
        return  renderer.readyToDraw()
    }
}