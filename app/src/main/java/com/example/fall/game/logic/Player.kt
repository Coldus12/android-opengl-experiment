package com.example.fall.game.logic

import android.content.Context
import com.example.fall.game.graphics.opengl.Texture
import com.example.fall.R
import com.example.fall.data.game_data.Block
import com.example.fall.data.persistent_data.PlayerData
import com.example.fall.data.persistent_data.PlayerStates
import com.example.fall.game.graphics.Camera
import com.example.fall.game.graphics.opengl.Shader

// PlayerTypes / CharacterTypes - the different playable "classes"
//--------------------------------------------------------------------------------------------------
/** An enum to differentiate between playerTypes
 * */
enum class PlayerType {
    Pistol,
    Shotgun
}

// Player
//--------------------------------------------------------------------------------------------------
/** The parent class which the different playable "classes" inherit from.
 * @param context Context to load the necessary resources from drawables
 * */
abstract class Player(private var context: Context) : Creature() {
    // Data
    //----------------------------------------------------------------------------------------------
    lateinit var cam: Camera
        protected set

    protected var speed = 10f
    lateinit var data: PlayerData
        protected set

    protected open var coordsPerVertex = 2
    protected open var playerGeometry =
        floatArrayOf(
            -1f, 1f,
            1f, 1f,
            1f, -1f,
            -1f, -1f
        )

    open fun getPosX() : Float {
        return data.posX
    }

    open fun getPosY() : Float {
        return data.posY
    }

    open fun isAlive(): Boolean {
        return data.health >= 0
    }

    // OpenGL load functions
    //----------------------------------------------------------------------------------------------
    override fun loadTexture() {
        texture = Texture(context, data.modelResourceId)
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

    // Player "actions"
    //----------------------------------------------------------------------------------------------
    override fun takeDamage(dmg: Int) {
        data.health -= dmg
    }

    /** Adds points to a player's score
     * @param score points to be added to the player's score.
     * */
    open fun addScore(score: Int) {
        data.score += score
    }

    /** How the different playerTypes shoot.
     * Each playable class must implement this.
     * @param game the game in which the character shoots
     * */
    abstract fun shoot(game: Game)

    /** Rotates the player around its center by the given radian
     * @param rad the radian by which the player should be rotated
     * */
    open fun rotate(rad: Float) {
        data.lookDirection = rad
    }

    //  Everything that's related to player movement.
    //----------------------------------------------------------------------------------------------
    private var nextX = 0f
    private var nextY = 0f

    private var dx = 0f
    private var dy = 0f

    private lateinit var gameRef: Game

    /** Saves where the player wanted the character to move.
     * No actual movement happens here, since that would make
     * the player's speed/movement input speed dependent.
     * @param game The game
     * @param dx the desired amount of movement in the x direction
     * @param dy the desired amount of movement in the y direction
     * */
    override fun move(game: Game, dx: Float, dy: Float) {
        data.currentState = PlayerStates.Moving

        this.dx += dx
        this.dy += dy

        gameRef = game
    }

    /** Updates the player's position baed on the desired change in position,
     * the player's speed, and the time passed since the last update.
     * @param timeInMs time passed since last run
     * */
    protected open fun updatePosition(timeInMs: Long) {
        if (dx == 0f && dy == 0f)
            data.currentState = PlayerStates.Standing
        else {
            nextX = data.posX + dx * speed * timeInMs / 100
            nextY = data.posY + dy * speed * timeInMs / 100

            val visibleBlocks = gameRef.getVisibleBlocks()

            if (!collisionDetect(nextX, nextY, visibleBlocks)) {
                data.posX = nextX
                data.posY = nextY
            } else if (!collisionDetect(nextX, data.posY, visibleBlocks)) {
                data.posX = nextX
            } else if (!collisionDetect(data.posX, nextY, visibleBlocks)) {
                data.posY = nextY
            }

            cam.setPos(data.posX, data.posY)

            dx = 0f
            dy = 0f
        }
    }

    /** Checks if at a given position the player would collide with a block of the map.
     * @param x the x coordinate of the position where the player might collide with something
     * @param y the y coordinate
     * @param visibleBlocks the blocks visible, and therefore the blocks
     * with which the player might collide
     * @return does the player collide with a block at the given position
     * */
    protected fun collisionDetect(x: Float, y: Float, visibleBlocks: MutableList<Block>): Boolean {
        var ret = false

        for (i in visibleBlocks) {
            ret = doesPlayerCollideWithBlock(i, x, y)
            if (ret) return ret
        }

        return ret
    }

    /** Checks if the player collides with a given block at a given position
     *
     * This is based on:
     * https://stackoverflow.com/a/1879223/4589636 (Cygon's answer)
     *
     * @param newX x coordinate of the position
     * @param newY y coordinate of the position
     * @param block the block
     * @return whether or not the player collides with the block at that position
     * */
    protected open fun doesPlayerCollideWithBlock(block: Block, newX: Float, newY: Float): Boolean {
        return if (!block.passable) {
            val closestX =
                clamp(newX, block.posX - block.blockSize / 2, block.posX + block.blockSize / 2)
            val closestY =
                clamp(newY, block.posY - block.blockSize / 2, block.posY + block.blockSize / 2)

            val dx = newX - closestX
            val dy = newY - closestY

            (dx * dx + dy * dy) < (data.size * data.size)
        } else
            false
    }

    protected fun clamp(v1: Float, min: Float, max: Float): Float {
        if (v1 in min..max)
            return v1

        return if (v1 < min) min else max
    }
}