package com.example.fall.logic

import android.content.Context
import com.example.fall.R
import com.example.fall.data.BulletData
import com.example.fall.data.BulletTextures
import com.example.fall.math.Mat4
import com.example.fall.math.Vec4
import com.example.fall.graphics.opengl.Shader
import com.example.fall.graphics.opengl.Texture

class BulletRenderer(private var context: Context)  {
    private lateinit var pellet: Texture
    private lateinit var standard: Texture

    private lateinit var p: Mat4
    private lateinit var v: Mat4
    private val geometryData = floatArrayOf(-0.5f, 0.5f,
        0.5f, 0.5f,
        0.5f, -0.5f,
        -0.5f, -0.5f)

    private var shader = Shader(context, R.raw.block_vertex_shader, R.raw.block_fragment_shader, geometryData, 2, "vPosition")

    fun setViewProj(v: Mat4, p: Mat4) {
        this.v = v
        this.p = p
    }

    private fun loadTexture() {
        pellet = Texture(context, R.drawable.whatever)
        standard = Texture(context, R.drawable.standardbullet)
    }

    init {
        loadTexture()
    }

    fun draw(data: BulletData) {
        shader.useProgram()

        when (data.texture) {
            BulletTextures.pellet -> pellet.setTexture()
            BulletTextures.standard -> standard.setTexture()
            else -> {}
        }

        val r = Mat4.rotMat(data.direction)
        val t = Mat4.translateMat(Vec4(floatArrayOf(data.posX, data.posY, 0f, 1f)))
        val s = Mat4.scaleMat(Vec4(floatArrayOf(data.size, data.size, 0f, 1f)))

        val sr = s.multiplyBy(r)
        val m = sr.multiplyBy(t)

        val vp = v.multiplyBy(p)
        val mvp = m.multiplyBy(vp)

        shader.setUniformMat(mvp, "MVPMatrix")
        shader.drawGeometry()
    }
}