package com.example.fall.logic

import android.opengl.GLES30
import android.opengl.GLSurfaceView
import com.example.fall.data.Player
import com.example.fall.data.PlayerStates
import com.example.fall.math.Map
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyGLRenderer : GLSurfaceView.Renderer {

    private lateinit var renderer: PlayerRenderer
    private lateinit var blockRenderer: BlockRenderer
    private lateinit var data: Player
    private lateinit var square: Player
    private lateinit var cam: Camera
    private lateinit var map: Map
    private var ready = false

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        GLES30.glClearColor(0.5f, 0.3f, 1.0f, 1.0f)
        map = Map(500,500, 2f)

        data = Player(
            map.getStartingX(),
            map.getStartingY(),
            "no_model",
            PlayerStates.standing,
            100,
            0f
        )

        renderer = PlayerRenderer()
        blockRenderer = BlockRenderer()
    }

    private fun rendermap() {
        ready = false
        val list = map.getMapNear(data.posX, data.posY, 32)
        blockRenderer.setViewProj(cam.getV(), cam.getP())

        val passable = floatArrayOf(1f, 0.5f, 0.5f, 1f)
        val nope = floatArrayOf(0f, 0.5f, 0f, 1f)

        for (i in list.indices) {
            if (list[i].passable)
                blockRenderer.draw(list[i], passable)
            else
                blockRenderer.draw(list[i], nope)
        }
    }

    override fun onDrawFrame(unused: GL10) {
        ready = false

        // Redraw background color
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)

        cam.setPos(data.posX, data.posY)

        renderer.setViewProj(cam.getV(), cam.getP())
        rendermap()

        renderer.draw(data)
        ready = true
    }

    fun changePos(x: Float, y: Float) {
        data.posX = x
        data.posY = y
    }

    fun rotate(angle: Float) {
        data.lookDirection = angle
    }

    fun deltaPos(x: Float, y: Float) {
        data.posX += x
        data.posY += y

        cam.setPos(data.posX, data.posY)
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)

        cam = Camera(data.posX, data.posY, width.toFloat(), height.toFloat())
        cam.zoom(70f)

        renderer.setViewProj(cam.getV(), cam.getP())
        rendermap()
        renderer.draw(data)
    }

    fun readyToDraw(): Boolean {
        return ready
    }
}