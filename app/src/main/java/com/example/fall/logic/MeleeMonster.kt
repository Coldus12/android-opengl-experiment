package com.example.fall.logic

import android.content.Context
import android.util.Log
import com.example.fall.R
import com.example.fall.data.Block
import com.example.fall.data.MonsterData
import com.example.fall.data.MonsterStates
import com.example.fall.graphics.Animation
import com.example.fall.math.Mat4
import com.example.fall.math.Vec4
import kotlin.math.*
import kotlin.random.Random

class MeleeMonster(private var context: Context, posX: Float, posY: Float, lookDirection: Float) : Monster(context) {
    init {
        data = MonsterData(
            posX,
            posY,
            1f,
            R.drawable.cockroach,
            MonsterStates.Standing,
            lookDirection,
            20,
            20,
            true
        )

        speed = 5f

        loadShader()
        loadAnimation()
    }

    //
    //----------------------------------------------------------------------------------------------
    private lateinit var animation: Animation
    private fun loadAnimation() {
        animation = Animation(
            context,
            data.resourceId,
            1,
            4,
            64,
            64
        )

        animation.setTimes(2)
    }

    //
    //----------------------------------------------------------------------------------------------
    override fun doesBulletHitIt(bullet: Bullet): Boolean {
        // To make it simpler both the monster's and the bullet's hitbox is reduced to a simple
        // circle. Is it precise? Not at all. Was it faster to implement? Yep.
        val bulletdata = bullet.getData()
        val dx = data.posX - bulletdata.posX
        val dy = data.posY - bulletdata.posY
        val distance = sqrt(dx * dx + dy * dy)
        val radiuses = data.size + bulletdata.size

        if (distance <= radiuses) {
            bullet.setExists(false)
            data.health -= bulletdata.dmg
            if (data.health <= 0)
                data.alive = false
        }

        return (distance <= radiuses)
    }

    //
    //----------------------------------------------------------------------------------------------
    override fun isAlive() : Boolean {
        return data.alive
    }

    //
    //----------------------------------------------------------------------------------------------
    private val attacksPerSecond = 2
    private val spa = 1000L / attacksPerSecond
    private var isAttacking = false
    private lateinit var playerRef: Player
    override fun attack(player: Player) {
        isAttacking = true
        playerRef = player
    }

    private var timeSinceLastAttack = spa + 1
    private fun updateAttacks(timeInMs: Long) {
        timeSinceLastAttack += timeInMs

        if (isAttacking && (timeSinceLastAttack >= spa)) {
            playerRef.takeDamage(data.dmg)

            timeSinceLastAttack = 0L
            isAttacking = false
        }
    }

    //
    //----------------------------------------------------------------------------------------------
    override fun move(game: Game, dx: Float, dy: Float) {}

    private var gameRef: Game? = null
    private lateinit var map: Map
    override fun setGameRef(game: Game) {
        if (gameRef == null)
            map = game.getMap()
        gameRef = game
    }

    private fun moveRandomly(timeInMs: Long) {
        if (!moveForward(timeInMs))
            turnABit(timeInMs)
    }

    private fun turnABit(timeInMs: Long) {
        data.lookDirection += timeInMs * 0.1f
    }

    private var neighbouringBlocks: MutableList<Block> = mutableListOf()
    private fun collisionDetect(x: Float, y: Float) : Boolean {
        neighbouringBlocks = map.getMapNear(x, y, 5)

        var ret = false
        for (i in neighbouringBlocks) {
            ret = doesMonsterCollideWithBlock(i, x, y)
            if (ret) return ret
        }

        return ret
    }

    private fun doesMonsterCollideWithBlock(block: Block, newX: Float, newY: Float) : Boolean {
        return if (!block.passable) {
            val closestX =
                clamp(newX, block.posX - block.blockSize / 2, block.posX + block.blockSize / 2)
            val closestY =
                clamp(newY, block.posY - block.blockSize / 2, block.posY + block.blockSize / 2)

            val dx = newX - closestX
            val dy = newY - closestY

            (dx * dx + dy * dy) < (data.size * data.size)
        } else false
    }

    private fun clamp(v1: Float, min: Float, max: Float): Float {
        if (v1 in min..max)
            return v1

        return if (v1 < min) min else max
    }

    private var nextX = 0f
    private var nextY = 0f
    private fun moveForward(timeInMs: Long) : Boolean {
        nextX = cos(data.lookDirection) * speed * timeInMs / 100
        nextY = sin(data.lookDirection) * speed * timeInMs / 100

        if (!collisionDetect(data.posX + nextX, data.posY + nextY)) {
            data.posX += nextX
            data.posY += nextY

            return true
        }

        return false
    }

    private fun moveTowardsPlayer(timeInMs: Long) {
        val pX = gameRef!!.player.getPosX()
        val pY = gameRef!!.player.getPosY()

        var dx = data.posX - pX
        var dy = data.posY - pY

        if (abs(dx) >= 0.5 || abs(dy) >= 0.5) {
            val lineSize = sqrt(dx * dx + dy * dy)

            dx /= lineSize // direction x
            dy /= lineSize // direction y

            var rad = atan2(dy.toDouble(), dx.toDouble())
            rad += PI

            data.lookDirection = rad.toFloat()
            data.currentState = MonsterStates.Moving

            animation.update(timeInMs)

            val changeX = dx * speed * timeInMs / 100
            val changeY = dy * speed * timeInMs / 100

            if (!collisionDetect(data.posX - changeX, data.posY - changeY)) {
                data.posX -= changeX
                data.posY -= changeY
            }
        } else {
            attack(gameRef!!.player)
        }
    }

    private fun doesItSeePlayer() : Boolean {
        if (gameRef != null) {
            val pX = gameRef!!.player.getPosX()
            val pY = gameRef!!.player.getPosY()

            val dx = data.posX - pX
            val dy = data.posY - pY

            if (abs(dx) <= 0.5 && abs(dy) <= 0.5)
                return true

            val lineSize = sqrt(dx * dx + dy * dy)
            val delta = 1f / lineSize

            var cX = 0f
            var cY = 0f
            for (i in 0 .. lineSize.toInt()) {
                cX = (1f - i * delta) * data.posX + (i * delta) * pX
                cY = (1f - i * delta) * data.posY + (i * delta) * pY

                if (!map.getBlockAt(cX,cY).passable)
                    return false
            }

            return true
        }

        return false
    }

    //
    //----------------------------------------------------------------------------------------------
    override fun draw() {
        shader.useProgram()

        when (data.currentState) {
            MonsterStates.Standing -> animation.setFrame(0)
            MonsterStates.Moving -> animation.setCurrentFrame()
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

    //
    //----------------------------------------------------------------------------------------------
    private fun doSomething(timeInMs: Long) {
        if (doesItSeePlayer()) {
            moveTowardsPlayer(timeInMs)
        } else {
            if (Random.nextInt()%3 == 0) {
                moveRandomly(timeInMs)
            }
        }
    }

    //
    //----------------------------------------------------------------------------------------------
    override fun update(timeInMs: Long) {
        updateAttacks(timeInMs)
        doSomething(timeInMs)
    }
}