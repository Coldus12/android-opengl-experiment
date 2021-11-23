package com.example.fall.logic

import android.content.Context
import com.example.fall.opengl.Texture
import com.example.fall.R
import com.example.fall.data.PlayerData
import com.example.fall.math.Mat4
import com.example.fall.math.Vec4
import com.example.fall.opengl.Shader

class Player(private var context: Context) {
    private lateinit var texture: Texture

    private fun loadTexture() {
        texture = Texture(context, R.drawable.playermodel1)
    }

    private lateinit var data: PlayerData
    private lateinit var p: Mat4
    private lateinit var v: Mat4

    fun setViewProj(v: Mat4, p: Mat4) {
        this.v = v
        this.p = p
    }

    private val playerGeometry = floatArrayOf(-1f, 1f,
        1f, 1f,
        1f, -1f,
        -1f, -1f)

    fun setPlayerData(playerData: PlayerData) {
        this.data = playerData
    }

    private var shader: Shader = Shader(context, R.raw.player_vertex_shader, R.raw.player_fragment_shader, playerGeometry, 2, "vPosition")

    init {
        loadTexture()
    }

    fun draw() {
        shader.useProgram()
        texture.setTexture()

        val r = Mat4.rotMat(data.lookDirection)
        val t = Mat4.translateMat(Vec4(floatArrayOf(data.posX, data.posY, 0f, 1f)))
        val s = Mat4.scaleMat(Vec4(floatArrayOf(data.size, data.size, 0f, 1f)))

        val sr = s.multiplyBy(r)
        val m = sr.multiplyBy(t)

        val vp = v.multiplyBy(p)
        val mvp = m.multiplyBy(vp)

        shader.setUniformMat(mvp, "MVPMatrix")
        shader.drawGeometry()
    }

    /*fun shoot() {

    }*/
}