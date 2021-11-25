package com.example.fall.logic

import android.content.Context
import com.example.fall.graphics.opengl.Texture
import com.example.fall.R
import com.example.fall.data.Block
import com.example.fall.data.BlockTextureTypes
import com.example.fall.math.Mat4
import com.example.fall.math.Vec4
import com.example.fall.graphics.opengl.Shader

class BlockRenderer(private var context: Context) {
    private lateinit var wall1: Texture
    private lateinit var wall2: Texture
    private lateinit var wall3: Texture
    private lateinit var floor: Texture

    private fun loadTextures() {
        wall1 = Texture(context, R.drawable.one)
        wall2 = Texture(context, R.drawable.two)
        wall3 = Texture(context, R.drawable.three)
        floor = Texture(context, R.drawable.floor_t)
    }

    private lateinit var p: Mat4
    private lateinit var v: Mat4

    private val blockGeometry =
        floatArrayOf(
            -0.5f, 0.5f,
            0.5f, 0.5f,
            0.5f, -0.5f,
            -0.5f, -0.5f
        )

    private var shader = Shader(context, R.raw.block_vertex_shader, R.raw.block_fragment_shader, blockGeometry, 2, "vPosition")

    fun setViewProj(v: Mat4, p: Mat4) {
        this.v = v
        this.p = p
    }

    init {
        loadTextures()
    }

    fun draw(data: Block) {
        shader.useProgram()

        when (data.type) {
            BlockTextureTypes.Wall1 -> wall1.setTexture()
            BlockTextureTypes.Wall2 -> wall2.setTexture()
            BlockTextureTypes.Wall3 -> wall3.setTexture()
            else -> {floor.setTexture()}
        }

        val r = Mat4.rotMat(0f)
        val t = Mat4.translateMat(Vec4(floatArrayOf(data.posX, data.posY, 0f, 1f)))
        val s = Mat4.scaleMat(Vec4(floatArrayOf(data.blockSize, data.blockSize, 0f, 1f)))

        val sr = s.multiplyBy(r)
        val m = sr.multiplyBy(t)

        val vp = v.multiplyBy(p)
        val mvp = m.multiplyBy(vp)

        shader.setUniformMat(mvp, "MVPMatrix")
        shader.drawGeometry()
    }
}