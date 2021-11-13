package com.example.fall.logic

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

class MyGLSurfaceView(context: Context, attrs: AttributeSet) : GLSurfaceView(context,attrs) {
    private val renderer: MyGLRenderer

    init {
        setEGLContextClientVersion(3)

        renderer = MyGLRenderer()
        setRenderer(renderer)
    }

    fun moveDelta(x: Float, y: Float) {
        renderer.deltaPos(x,y)
    }

    fun rot(angle: Float) {
        renderer.rotate(angle)
    }
}