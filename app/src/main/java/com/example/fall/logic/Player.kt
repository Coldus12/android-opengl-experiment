package com.example.fall.logic

import android.content.Context
import com.example.fall.graphics.opengl.Texture
import com.example.fall.R
import com.example.fall.data.PlayerData
import com.example.fall.graphics.Camera
import com.example.fall.graphics.opengl.Shader

abstract class Player(private var context: Context) : Creature() {
    protected var speed = 10f

    lateinit var cam: Camera
        protected set

    lateinit var data: PlayerData
        protected set

    override fun loadTexture() {
        texture = Texture(context, data.modelResourceId)
    }

    protected open var playerGeometry =
        floatArrayOf(
            -1f, 1f,
            1f, 1f,
            1f, -1f,
            -1f, -1f
        )

    protected open var coordsPerVertex = 2

    override fun takeDamage(dmg: Int) {
        data.health -= dmg
    }

    override fun loadShader() {
        shader = Shader(
            context,
            R.raw.player_vertex_shader,
            R.raw.player_fragment_shader,
            playerGeometry,
            coordsPerVertex,
            "vPosition"
        )
    }

    abstract fun getPosX() : Float
    abstract fun getPosY() : Float
    abstract fun rotate(rad: Float)
    abstract fun shoot(game: Game)
}