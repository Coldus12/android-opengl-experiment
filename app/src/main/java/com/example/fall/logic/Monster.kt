package com.example.fall.logic

import android.content.Context
import com.example.fall.R
import com.example.fall.data.MonsterData
import com.example.fall.graphics.opengl.Shader
import com.example.fall.graphics.opengl.Texture
import com.example.fall.math.Mat4

abstract class Monster(private var context: Context) : Creature() {
    protected var speed = 10f

    protected open lateinit var p: Mat4
    protected open lateinit var v: Mat4

    open fun setViewProj(v: Mat4, p: Mat4) {
        this.v = v
        this.p = p
    }

    lateinit var data: MonsterData
        protected set

    override fun loadTexture() {
        texture = Texture(context, data.resourceId)
    }

    protected open var monsterGeometry =
        floatArrayOf(
            -1f, 1f,
            1f, 1f,
            1f, -1f,
            -1f, -1f
        )

    protected open var coordsPerVertex = 2

    override fun loadShader() {
        shader = Shader(
            context,
            R.raw.standard_vertex_shader,
            R.raw.standard_fragment_shader,
            monsterGeometry,
            coordsPerVertex,
            "vPosition"
        )
    }

    abstract fun attack(player: Player)
}