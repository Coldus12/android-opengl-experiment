package com.example.fall.logic

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet

class MyGLSurfaceView(context: Context, attrs: AttributeSet) : GLSurfaceView(context,attrs) {
    private val renderer: MyGLRenderer

    init {
        setEGLContextClientVersion(3)

        renderer = MyGLRenderer(context)
        setRenderer(renderer)
        renderMode = RENDERMODE_WHEN_DIRTY
    }

    fun moveDelta(x: Float, y: Float) {
        renderer.deltaPos(x,y)
    }

    fun rot(angle: Float) {
        renderer.rotate(angle)
    }

    fun readyToDraw(): Boolean {
        return renderer.readyToDraw()
    }
}