package com.example.fall.logic

import android.graphics.PointF
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import java.util.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.random.Random

class MyGLRenderer : GLSurfaceView.Renderer {

    //private lateinit var mTriangle: Triangle
    //lateinit var rect: Rectangle
    private var circleList = ArrayList<Rectangle>()
    private var random = Random(Calendar.getInstance().timeInMillis)
    private var draw = true
    private var projectionMatrix = FloatArray(16)

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        GLES30.glClearColor(0.5f, 0.3f, 1.0f, 1.0f)

        for (i in 0..10) {
            circleList.add(Rectangle(2*random.nextFloat() - 1,random.nextFloat()*2 - 1, random.nextFloat()*2-1, random.nextInt(3, 15)))
            circleList[i].changeColor(random.nextFloat(), random.nextFloat(),random.nextFloat(), 1.0f)
        }
    }

    fun reGenCircles() {
        draw = false
        //circleList.clear()
        for (i in 0 until circleList.size) {
            circleList[i].center = PointF(random.nextFloat()*2-1, random.nextFloat()*2-1)
            circleList[i].changeColor(random.nextFloat(), random.nextFloat(),random.nextFloat(), 1.0f)
            circleList[i].r = random.nextFloat()
        }

        draw = true
    }

    override fun onDrawFrame(unused: GL10) {
        // Redraw background color
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)

        if (draw) {
            for (i in 0 until circleList.size) {
                circleList[i].draw()
            }
        }
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)

        val ratio = width.toFloat() / height.toFloat()

        var mat = floatArrayOf( 1/ratio * 1f, 0f, 0f, 0f,
                                0f, 1f, 0f, 0f,
                                0f, 0f, 1f, 0f,
                                0f, 0f, 0f, 1f)

        //Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)
        Rectangle.mat = mat
    }
}