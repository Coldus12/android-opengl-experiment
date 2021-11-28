package com.example.fall.game.logic

import android.content.Context
import android.util.Log
import com.example.fall.R
import com.example.fall.data.*
import com.example.fall.game.graphics.Animation
import com.example.fall.game.graphics.Camera
import com.example.fall.game.math.Mat4
import com.example.fall.game.math.Vec4

class PistolPlayer(private var context: Context, startPosX: Float, startPosY: Float) : Player(context) {
    private var healthBar: HealthBar
    private var maxHp = 100

    init {
        data = PlayerData(
            startPosX,
            startPosY,
            1f,
            R.drawable.pistol_moving_sprite,
            PlayerStates.Standing,
            false,
            100,
            0f,
            1,
            0
        )

        maxHp = data.health

        cam = Camera(data.posX, data.posY, 1f, 1f)
        cam.zoom(70f)

        healthBar = HealthBar(context)
        loadShader()
        loadAnimation()
    }

    //
    //----------------------------------------------------------------------------------------------
    override fun takeDamage(dmg: Int) {
        super.takeDamage(dmg)
        healthBar.updateHealth(1f - data.health.toFloat()/maxHp.toFloat())
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

        animation.setTimes(20)
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
        12f,
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

    override fun updatePosition(timeInMs: Long) {
        super.updatePosition(timeInMs)
        animation.update(timeInMs)
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
        healthBar.draw()

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