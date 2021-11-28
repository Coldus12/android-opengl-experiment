package com.example.fall.game.logic

import android.content.Context
import com.example.fall.game.graphics.opengl.Texture
import com.example.fall.R
import com.example.fall.data.Block
import com.example.fall.data.PlayerData
import com.example.fall.data.PlayerStates
import com.example.fall.game.graphics.Camera
import com.example.fall.game.graphics.opengl.Shader

enum class PlayerType {
    Pistol,
    Shotgun
}

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

    open fun addScore(score: Int) {
        data.score += score
    }

    abstract fun shoot(game: Game)

    open fun getPosX() : Float {
        return data.posX
    }

    open fun getPosY() : Float {
        return data.posY
    }

    //
    //----------------------------------------------------------------------------------------------
    open fun rotate(rad: Float) {
        data.lookDirection = rad
    }

    //
    //----------------------------------------------------------------------------------------------
    open fun isAlive(): Boolean {
        return data.health >= 0
    }

    //  Everything that's related to player movement.
    //----------------------------------------------------------------------------------------------
    private var nextX = 0f
    private var nextY = 0f

    private var dx = 0f
    private var dy = 0f
    override fun move(game: Game, dx: Float, dy: Float) {
        data.currentState = PlayerStates.Moving

        val visibleBlocks = game.getVisibleBlocks()
        nextX = data.posX + dx
        nextY = data.posY + dy

        if (!collisionDetect(nextX, nextY, visibleBlocks)) {
            this.dx += dx
            this.dy += dy
        } else if (!collisionDetect(nextX, data.posY, visibleBlocks)) {
            this.dx += dx
        } else if (!collisionDetect(data.posX, nextY, visibleBlocks)) {
            this.dy += dy
        }
    }

    protected open fun updatePosition(timeInMs: Long) {
        if (dx == 0f && dy == 0f)
            data.currentState = PlayerStates.Standing

        data.posX += dx * speed * timeInMs/100
        data.posY += dy * speed * timeInMs/100
        cam.setPos(data.posX, data.posY)

        dx = 0f
        dy = 0f
    }

    protected fun collisionDetect(x: Float, y: Float, visibleBlocks: MutableList<Block>): Boolean {
        var ret = false

        for (i in visibleBlocks) {
            ret = doesPlayerCollideWithBlock(i, x, y)
            if (ret) return ret
        }

        return ret
    }

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