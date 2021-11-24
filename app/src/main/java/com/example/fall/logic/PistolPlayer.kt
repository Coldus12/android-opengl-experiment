package com.example.fall.logic

import android.content.Context
import com.example.fall.R
import com.example.fall.data.*
import com.example.fall.graphics.Camera
import com.example.fall.graphics.Sprite
import com.example.fall.math.Mat4
import com.example.fall.math.Vec4
import kotlin.math.cos
import kotlin.math.sin

class PistolPlayer(private var context: Context, startPosX: Float, startPosY: Float) : Player(context) {
    init {
        data = PlayerData(
            startPosX,
            startPosY,
            1f,
            R.drawable.playermodel1,
            PlayerStates.standing,
            100,
            0f
        )

        cam = Camera(data.posX, data.posY, 1f, 1f)
        cam.zoom(70f)

        loadShader()
        loadTexture()
        loadSprite()
    }

    //
    //----------------------------------------------------------------------------------------------
    private lateinit var sprite: Sprite
    fun loadSprite() {
        sprite = Sprite(context, R.drawable.pistol_moving_sprite, 1, 8, 64, 64)
    }

    //  Everything that's related to player movement.
    //----------------------------------------------------------------------------------------------
    private var nextX = 0f
    private var nextY = 0f
    override fun move(game: Game, dx: Float, dy: Float) {
        val visibleBlocks = game.getVisibleBlocks()
        nextX = data.posX + dx
        nextY = data.posY + dy

        if (!collisionDetect(nextX, nextY, visibleBlocks)) {
            data.posX += dx
            data.posY += dy

            cam.setPos(data.posX, data.posY)
        } else if (!collisionDetect(nextX, data.posY, visibleBlocks)) {
            data.posX += dx

            cam.setPos(data.posX, data.posY)
        } else if (!collisionDetect(data.posX, nextY, visibleBlocks)) {
            data.posY += dy

            cam.setPos(data.posX, data.posY)
        }

        data.currentState = if (data.currentState == PlayerStates.moving2) PlayerStates.moving1 else PlayerStates.moving2
    }

    private fun collisionDetect(x: Float, y: Float, visibleBlocks: MutableList<Block>): Boolean {
        var ret = false

        for (i in visibleBlocks) {
            ret = doesPlayerCollideWithBlock(i, x, y)
            if (ret) return ret
        }

        return ret
    }

    private fun doesPlayerCollideWithBlock(block: Block, newX: Float, newY: Float): Boolean {
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

    private fun clamp(v1: Float, min: Float, max: Float): Float {
        if (v1 in min..max)
            return v1

        return if (v1 < min) min else max
    }

    //
    //----------------------------------------------------------------------------------------------
    override fun rotate(rad: Float) {
        data.lookDirection = rad
    }

    //  Shooting with a pistol
    //----------------------------------------------------------------------------------------------
    private val bulletTemplate = BulletData(
        data.posX,
        data.posY,
        data.lookDirection,
        0.3f,
        1f,
        BulletTextures.standard,
        true
    )

    override fun shoot(game: Game) {
        val bullet = bulletTemplate.copy()
        bullet.posX = data.posX
        bullet.posY = data.posY
        bullet.direction = data.lookDirection
        bullet.exists = true
        game.addBullet(bullet)
    }

    //
    //----------------------------------------------------------------------------------------------
    override fun draw() {
        val v = cam.getV()
        val p = cam.getP()

        shader.useProgram()
        //texture.setTexture()
        when (data.currentState) {
            PlayerStates.standing -> sprite.get(0,0).setTexture()
            PlayerStates.moving1 -> sprite.get(2,0).setTexture()
            PlayerStates.moving2 -> sprite.get(6,0).setTexture()
            else -> sprite.get(0,0)
        }

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
}