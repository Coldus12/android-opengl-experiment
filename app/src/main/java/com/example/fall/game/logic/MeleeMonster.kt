package com.example.fall.game.logic

import android.content.Context
import com.example.fall.R
import com.example.fall.data.game_data.Block
import com.example.fall.data.game_data.MonsterData
import com.example.fall.data.game_data.MonsterStates
import com.example.fall.game.graphics.Animation
import com.example.fall.game.math.Mat4
import com.example.fall.game.math.Vec4
import kotlin.math.*
import kotlin.random.Random

// MeleeMonster
//--------------------------------------------------------------------------------------------------
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
            3,
            true
        )

        speed = 5f

        loadShader()
        loadAnimation()
        loadIndicator()
    }

    override fun isAlive() : Boolean {
        return data.alive
    }

    // Everything animation related
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

        animation.setTimes(10)
    }

    // MonsterIndicator
    //----------------------------------------------------------------------------------------------
    private lateinit var indicator: MonsterIndicator
    private fun loadIndicator() {
        indicator = MonsterIndicator(context)
    }

    private fun updateIndicator() {
        if (gameRef != null) {
            val dx = data.posX - gameRef!!.player.getPosX()
            val dy = data.posY - gameRef!!.player.getPosY()

            val deg = atan2(dy, dx)
            indicator.setDegree(deg)
        }
    }

    override fun setScreenData(width: Int, height: Int) {
        indicator.setScreenData(width, height)
    }

    // Bullet collision detection
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
            if (isAlive()) {
                bullet.setExists(false)
                data.health -= bulletdata.dmg
                if (data.health <= 0) {
                    data.alive = false

                    // 25 score point per melee monster
                    gameRef!!.player.addScore(25)
                }
            }
        }

        return (distance <= radiuses)
    }

    // Attack
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

    // Everything related to the MeleeMonster's movement
    //----------------------------------------------------------------------------------------------
    override fun move(game: Game, dx: Float, dy: Float) {}

    private var gameRef: Game? = null
    private lateinit var map: Map
    override fun setGameRef(game: Game) {
        if (gameRef == null)
            map = game.getMap()
        gameRef = game
    }

    /** The monster tries to go forward relative to its "lookDirection", but should that fail for
     * some reason, like a block blocking its way, then it turns a bit, and then tries to move
     * forward once again.
     * @param timeInMs time since this function's last run
     * */
    private fun moveRandomly(timeInMs: Long) {
        if (!moveForward(timeInMs))
            turnABit(timeInMs)
    }

    /** Turns the monster's "lookDirection" a bit based on the time provided.
     * @param timeInMs the time since the game loops last run
     * */
    private fun turnABit(timeInMs: Long) {
        data.lookDirection += timeInMs * 0.1f
    }

    private var neighbouringBlocks: MutableList<Block> = mutableListOf()

    /** Checks if at a given position the monster would collide with a block of the map.
     * @param x the x coordinate of the position where the monster might collide with something
     * @param y the y coordinate
     * @return does the monster collide with a block at the given position
     * */
    private fun collisionDetect(x: Float, y: Float) : Boolean {
        neighbouringBlocks = map.getMapNear(x, y, 5)

        var ret = false
        for (i in neighbouringBlocks) {
            ret = doesMonsterCollideWithBlock(i, x, y)
            if (ret) return ret
        }

        return ret
    }

    /** Checks if the monster collides with a given block at a given position
     *
     * This is based on:
     * https://stackoverflow.com/a/1879223/4589636 (Cygon's answer)
     *
     * @param newX x coordinate of the position
     * @param newY y coordinate of the position
     * @param block the block
     * @return whether or not the monster collides with the block at that position
     * */
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
    /** Moves the monster forward relative to its "lookDirection".
     * @param timeInMs time passed since the game loop's last run
     * @return could the monster move forward
     * */
    private fun moveForward(timeInMs: Long) : Boolean {
        nextX = cos(data.lookDirection) * speed * timeInMs / 100
        nextY = sin(data.lookDirection) * speed * timeInMs / 100

        if (!collisionDetect(data.posX + nextX, data.posY + nextY)) {
            animation.update(timeInMs)

            data.posX += nextX
            data.posY += nextY

            return true
        }

        return false
    }

    /** The monster tries to move towards the player, unless it is already close enough to attack
     * the player, in which case it attacks the player.
     * @param timeInMs time passed since the game loop's last run
     * */
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

    /** Checks if there is a direct line of sight between the monster and the player. It does this
     * by making a line between the player's and the monster's position and checking if there are
     * any solid blocks "lying" on the line.
     * @return does the monster see the player
     * */
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

    // Draw
    //----------------------------------------------------------------------------------------------
    override fun draw() {
        indicator.draw()
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

    // Very basic "AI"
    //----------------------------------------------------------------------------------------------
    /** If the monster sees the player then it moves towards the player, in any other case
     * the monster just either stands around, or goes forward until it cannot go any more forward
     * at which point it just turns around to find a direction in which it can move.
     * @param timeInMs time passed since the game loop's last run
     * */
    private fun doSomething(timeInMs: Long) {
        if (doesItSeePlayer()) {
            moveTowardsPlayer(timeInMs)
        } else {
            if (Random.nextInt()%3 == 0) {
                moveRandomly(timeInMs)
            }
        }
    }

    override fun update(timeInMs: Long) {
        updateIndicator()
        updateAttacks(timeInMs)
        doSomething(timeInMs)
    }
}