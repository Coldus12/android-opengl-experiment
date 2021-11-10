package com.example.fall.logic

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.Log
import android.view.MotionEvent
import android.view.View

class MyGLSurfaceView(context: Context) : GLSurfaceView(context) {
    private val renderer: MyGLRenderer

    init {
        setEGLContextClientVersion(3)

        renderer = MyGLRenderer()
        setRenderer(renderer)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)

        var x: Float = event.x
        var y: Float = event.y

        x -= width/2
        y = height/2 - y

        x /= width
        y /= height

        Log.i("[LOG]","TOUCH!!! $x $y")

        renderer.changePos(x,y)

        return true
    }

}