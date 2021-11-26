package com.example.fall.game.logic

import android.content.Context
import com.example.fall.R
import com.example.fall.game.graphics.opengl.Shader
import com.example.fall.game.graphics.opengl.Texture
import com.example.fall.game.math.Mat4
import com.example.fall.game.math.Vec4
import kotlin.math.cos
import kotlin.math.sin

class MonsterIndicator(private var context: Context) {
    private lateinit var texture: Texture
    private val size = 0.05f
    private val geometryData =
        floatArrayOf(
            -0.5f, 0.5f,
            0.5f, 0.5f,
            0.5f, -0.5f,
            -0.5f, -0.5f
        )

    private var shader = Shader(context, R.raw.standard_vertex_shader, R.raw.standard_fragment_shader, geometryData, 2, "vPosition")

    private var screenW = 1
    private var screenH = 1
    private var degree = 0f
    private var radius = 0.15f

    private fun loadTexture() {
        texture = Texture(context, R.drawable.arrow_ready)
    }

    init {
        loadTexture()
    }

    fun setDegree(rad: Float) {
        degree = rad
    }

    fun setScreenData(width: Int, height: Int) {
        screenW = width
        screenH = height
    }

    fun draw() {
        shader.useProgram()
        texture.setTexture()

        val ratio = screenW.toFloat() / screenH.toFloat()
        val r = Mat4.rotMat(degree)
        val t = Mat4.translateMat(Vec4(floatArrayOf(radius * cos(degree) * 1/ratio, radius * sin(degree), 0f, 1f)))
        val s = Mat4.scaleMat(Vec4(floatArrayOf(size * ratio, size, 0f, 1f)))

        val sr = s.multiplyBy(r)
        val mvp = sr.multiplyBy(t)

        shader.setUniformMat(mvp, "MVPMatrix")
        shader.drawGeometry()
    }
}