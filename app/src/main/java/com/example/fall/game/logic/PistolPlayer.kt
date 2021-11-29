package com.example.fall.game.logic

import android.content.Context
import com.example.fall.R
import com.example.fall.data.game_data.BulletData
import com.example.fall.data.game_data.BulletTextures
import com.example.fall.data.persistent_data.PlayerData
import com.example.fall.data.persistent_data.PlayerStates
import com.example.fall.game.graphics.Animation
import com.example.fall.game.graphics.Camera
import com.example.fall.game.math.Mat4
import com.example.fall.game.math.Vec4

// PistolPlayer
//--------------------------------------------------------------------------------------------------
class PistolPlayer(private var context: Context, startPosX: Float, startPosY: Float) : Player(context) {
    // Data
    //----------------------------------------------------------------------------------------------
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

    override fun takeDamage(dmg: Int) {
        super.takeDamage(dmg)
        healthBar.updateHealth(1f - data.health.toFloat()/maxHp.toFloat())
    }

    // Loading the animation
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

    //  Everything related to shooting with a pistol
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

    private lateinit var gameRef: Game

    /** Sets the player's state to shooting.
     * @param game The game
     * */
    override fun shoot(game: Game) {
        data.currentlyShooting = true

        gameRef = game
    }

    override fun updatePosition(timeInMs: Long) {
        super.updatePosition(timeInMs)
        animation.update(timeInMs)
    }

    private var timeSinceLastShot = timeBetweenRounds + 1
    private var lastTime = System.currentTimeMillis()

    /** If the player tried to shoot this is the function that is going to
     *  "spawn" in the bullets and progress them if necessary.
     *
     *  More detail:
     *  If the time since last shot is more than the time between shots, and the
     *  player is in a shooting state, then bullet(s) will be spawned in. And the player
     *  shall transition into a non-shooting state. If the "timeInMs" is a multiple of the time
     *  between shots, then this function will shoot bullets as if the player tried to shoot
     *  throughout that time.
     *
     *  @param timeInMs time since this function's last run
     * */
    private fun updateShotsFired(timeInMs: Long) {
        if (System.currentTimeMillis() - lastTime >= timeBetweenRounds && data.currentlyShooting) {
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

            lastTime = System.currentTimeMillis()
        }
    }

    // Drawing the player
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

    // Updating the position and the shots
    //----------------------------------------------------------------------------------------------
    override fun update(timeInMs: Long) {
        updatePosition(timeInMs)
        updateShotsFired(timeInMs)
    }
}