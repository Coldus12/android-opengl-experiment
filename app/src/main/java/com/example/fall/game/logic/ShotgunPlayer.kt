package com.example.fall.game.logic

import android.content.Context
import com.example.fall.R
import com.example.fall.data.game_data.BulletData
import com.example.fall.data.game_data.BulletTextures
import com.example.fall.data.persistent_data.PlayerData
import com.example.fall.data.persistent_data.PlayerStates
import com.example.fall.game.graphics.Camera
import com.example.fall.game.math.Mat4
import com.example.fall.game.math.Vec4
import kotlin.math.PI

// ShotgunPlayer
//--------------------------------------------------------------------------------------------------
class ShotgunPlayer(private var context: Context, startPosX: Float, startPosY: Float) : Player(context) {
    // Data
    //----------------------------------------------------------------------------------------------
    private var healthBar: HealthBar
    private var maxHp = 100

    init {
        data = PlayerData(
            startPosX,
            startPosY,
            1f,
            R.drawable.shotgun_player,
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
        loadTexture()
    }

    override fun takeDamage(dmg: Int) {
        super.takeDamage(dmg)
        healthBar.updateHealth(1f - data.health.toFloat()/maxHp.toFloat())
    }

    // Everything related to shooting with a shotgun
    //----------------------------------------------------------------------------------------------
    private val RPS = 2f // Number of bullets per second is called rounds per second. Huh
    private val timeBetweenRounds = (1000L / RPS).toLong()
    private val nrOfPellets = 20
    private val bulletTemplate = BulletData(
        data.posX,
        data.posY,
        data.lookDirection,
        0.1f,
        12f,
        2,
        BulletTextures.Pellet,
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

    private var timeSinceLastShot = timeBetweenRounds + 1

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
        timeSinceLastShot += timeInMs

        if (timeSinceLastShot >= timeBetweenRounds && data.currentlyShooting) {
            data.currentlyShooting = false

            val bulletData = bulletTemplate.copy()
            bulletData.posX = data.posX
            bulletData.posY = data.posY
            bulletData.direction = data.lookDirection - (PI / 6).toFloat()
            bulletData.exists = true

            val nrOfShots = timeSinceLastShot / timeBetweenRounds

            for (i in 0 .. nrOfShots) {
                for (p in 0 .. nrOfPellets) {
                    val copy = bulletData.copy()
                    copy.direction += p * (PI/(6 * nrOfPellets/2)).toFloat()

                    val bullet = Bullet(copy)
                    bullet.update(i * timeBetweenRounds)
                    gameRef.addBullet(bullet)
                }
            }

            timeSinceLastShot = 0L
        }

        // fixes the random shooting even after not pressing the "trigger"
        data.currentlyShooting = false
    }

    // Drawing the player
    //----------------------------------------------------------------------------------------------
    override fun draw() {
        healthBar.draw()

        val v = cam.getV()
        val p = cam.getP()

        shader.useProgram()
        texture.setTexture()

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