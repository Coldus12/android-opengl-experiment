package com.example.fall.game

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import com.example.fall.game.logic.IGraphicalGame

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