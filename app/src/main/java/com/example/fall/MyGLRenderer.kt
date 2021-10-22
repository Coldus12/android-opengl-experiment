package com.example.fall

import android.opengl.GLES30
import android.opengl.GLSurfaceView
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyGLRenderer : GLSurfaceView.Renderer {

    //private lateinit var mTriangle: Triangle
    lateinit var rect: Rectangle

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        // Set the background frame color
        GLES30.glClearColor(0.5f, 0.3f, 1.0f, 1.0f)
        rect = Rectangle(0.0f,0.0f, 0.2f)
    }

    override fun onDrawFrame(unused: GL10) {
        // Redraw background color
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
        rect.draw()
        //mAndelbrot.draw()
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
    }
}