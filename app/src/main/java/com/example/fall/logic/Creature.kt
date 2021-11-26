package com.example.fall.logic

import com.example.fall.graphics.opengl.Shader
import com.example.fall.graphics.opengl.Texture

abstract class Creature : Updateable {
    protected lateinit var texture: Texture
    protected lateinit var shader: Shader

    protected open fun loadTexture() {}
    protected open fun loadShader() {}

    open fun takeDamage(dmg: Int) {}
    abstract fun move(game: Game, dx: Float = 0f, dy: Float = 0f)
    abstract fun draw()
}