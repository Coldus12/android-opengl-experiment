package com.example.fall.logic

import android.content.Context
import com.example.fall.graphics.opengl.Texture
import com.example.fall.R
import com.example.fall.data.PlayerData
import com.example.fall.graphics.Camera
import com.example.fall.graphics.opengl.Shader

abstract class Player(private var context: Context) {
    protected lateinit var texture: Texture
    protected lateinit var shader: Shader

    lateinit var cam: Camera
        protected set

    lateinit var data: PlayerData
        protected set

    protected fun loadTexture() {
        texture = Texture(context, data.modelResourceId)
    }

    protected open var playerGeometry =
        floatArrayOf(
            -1f, 1f,
            1f, 1f,
            1f, -1f,
            -1f, -1f
        )

    protected open var coords_per_vertex = 2

    protected fun loadShader() {
        shader = Shader(
            context,
            R.raw.player_vertex_shader,
            R.raw.player_fragment_shader,
            playerGeometry,
            coords_per_vertex,
            "vPosition"
        )
    }

    abstract fun move(game: Game, dx: Float, dy: Float)
    abstract fun rotate(rad: Float)
    abstract fun shoot(game: Game)
    abstract fun draw()
}