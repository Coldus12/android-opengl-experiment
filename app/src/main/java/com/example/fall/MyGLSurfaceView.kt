package com.example.fall

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import android.view.View
import java.util.*
import kotlin.random.Random

class MyGLSurfaceView(context: Context) : GLSurfaceView(context), View.OnClickListener {
    private val renderer: MyGLRenderer

    private var random: Random

    init {
        setEGLContextClientVersion(3)

        renderer = MyGLRenderer()
        setRenderer(renderer)

        random = Random(Calendar.getInstance().timeInMillis)

        setOnClickListener(this)
    }

    var nr = 3;
    override fun onClick(p0: View?) {
        //renderer.rect.changePosition((random.nextInt()%200)/100.0f - 1.0f,(random.nextInt()%200)/100.0f - 1.0f, (random.nextInt()%200)/100.0f - 1.0f)
        //renderer.rect.changePosition(random.nextFloat()*2 - 1.0f, random.nextFloat()*2 - 1.0f, random.nextFloat(), nr)
        renderer.rect.changePosition(0.0f, 0.0f, 0.5f, nr)
        renderer.rect.changeColor((random.nextInt()%100) / 100.0f, (random.nextInt()%100) / 100.0f, (random.nextInt()%100) / 100.0f, 1.0f)
        nr++
    }


}