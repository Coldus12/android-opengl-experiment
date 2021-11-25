package com.example.fall.logic

import android.content.Context
import com.example.fall.R
import com.example.fall.data.*
import com.example.fall.graphics.Animation
import com.example.fall.graphics.Camera
import com.example.fall.math.Mat4
import com.example.fall.math.Vec4

class PistolPlayer(private var context: Context, startPosX: Float, startPosY: Float) : Player(context) {
    init {
        data = PlayerData(
            startPosX,
            startPosY,
            1f,
            R.drawable.pistol_moving_sprite,
            PlayerStates.Standing,
            false,
            100,
            0f
        )

        cam = Camera(data.posX, data.posY, 1f, 1f)
        cam.zoom(70f)

        loadShader()
        loadAnimation()
    }

    //
    //----------------------------------------------------------------------------------------------
    private lateinit var animation: Animation
    private fun loadAnimation() {
        animation = Animation(
            context,
            data.modelResourceId,
            1,
            8,
            64,
            64
        )

        animation.setTimes(5)
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

    private fun updatePosition(timeInMs: Long) {
        if (dx == 0f && dy == 0f)
            data.currentState = PlayerStates.Standing

        animation.update(timeInMs)

        data.posX += dx * speed * timeInMs/100
        data.posY += dy * speed * timeInMs/100
        cam.setPos(data.posX, data.posY)

        dx = 0f
        dy = 0f
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
    private val RPS = 3 // Number of bullets per second is called rounds per second. Huh
    private val timeBetweenRounds = 1000L / RPS

    private val bulletTemplate = BulletData(
        data.posX,
        data.posY,
        data.lookDirection,
        0.3f,
        120f,
        5,
        BulletTextures.Standard,
        true
    )

    //
    private lateinit var gameRef: Game
    override fun shoot(game: Game) {
        data.currentlyShooting = true

        gameRef = game
    }

    //
    private var timeAtLastShot = System.currentTimeMillis()
    private var timeSinceLastShot = 0L
    private fun updateShotsFired(timeInMs: Long) {
        timeSinceLastShot = System.currentTimeMillis() - timeAtLastShot

        if (timeSinceLastShot >= timeBetweenRounds && data.currentlyShooting) {
            data.currentlyShooting = false

            val bulletData = bulletTemplate.copy()
            bulletData.posX = data.posX
            bulletData.posY = data.posY
            bulletData.direction = data.lookDirection
            bulletData.exists = true

            val nrOfShots = timeSinceLastShot / timeBetweenRounds

            for (i in 0 .. nrOfShots) {
                val bullet = Bullet(bulletData.copy())
                bullet.update(i * timeBetweenRounds)
                gameRef.addBullet(bullet)
            }

            timeAtLastShot = System.currentTimeMillis()
        }
    }

    //
    //----------------------------------------------------------------------------------------------
    override fun draw() {
        val v = cam.getV()
        val p = cam.getP()

        shader.useProgram()
        when (data.currentState) {
            PlayerStates.Standing -> animation.setFrame(0)
            PlayerStates.Moving -> animation.setCurrentFrame()
            else -> animation.setFrame(0)
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

    override fun update(timeInMs: Long) {
        updatePosition(timeInMs)
        updateShotsFired(timeInMs)
    }
}