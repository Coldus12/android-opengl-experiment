package com.example.fall.game.logic

import com.example.fall.game.graphics.opengl.Shader
import com.example.fall.game.graphics.opengl.Texture

// Creature
//--------------------------------------------------------------------------------------------------
abstract class Creature : Updateable {
    /** The texture of the creature
     * */
    protected lateinit var texture: Texture

    /** The shader of the creature
     * */
    protected lateinit var shader: Shader

    /** The function responsible for loading the creature's texture.
     * */
    protected open fun loadTexture() {}
    /** The function responsible for loading the creature's shader.
     * */
    protected open fun loadShader() {}

    /** What happens if the creature is damaged
     * */
    open fun takeDamage(dmg: Int) {}

    /** How the different creatures move
     * */
    abstract fun move(game: Game, dx: Float = 0f, dy: Float = 0f)
    abstract fun draw()
}