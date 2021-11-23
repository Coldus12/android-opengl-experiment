package com.example.fall.logic

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.Log
import com.example.fall.data.*
import com.example.fall.opengl.Camera
import kotlin.math.cos
import kotlin.math.sin

class Game(private var context: Context) : IGraphicalGame {
    private var player: Player
    private var blockRenderer: BlockRenderer
    private var bulletRenderer: BulletRenderer
    private var data: PlayerData = PlayerData(
        0f,
        0f,
        1f,
        "no_model",
        PlayerStates.standing,
        100,
        0f
    )

    private var cam: Camera
    private var map: Map
    private var blockList: MutableList<Block>
    private lateinit var visibleBlocks: MutableList<Block>
    private var bullets = mutableListOf<BulletData>()

    private var surfaceView: GLSurfaceView? = null
    fun setGLView(surface: GLSurfaceView) {
        surfaceView = surface
    }

    init {
        map = Map(500,500, 3f)
        data.posX = map.getStartingX()
        data.posY = map.getStartingY()
        blockList = map.getMap()

        player = Player(context)
        player.setPlayerData(data)
        blockRenderer = BlockRenderer(context)
        bulletRenderer = BulletRenderer(context)

        cam = Camera(data.posX, data.posY, 1f, 1f)
        cam.zoom(70f)

        player.setViewProj(cam.getV(), cam.getP())
        blockRenderer.setViewProj(cam.getV(), cam.getP())
    }

    override fun setCameraSize(width: Float, height: Float) {
        cam.setSize(width, height)
    }

    fun nextLevel() {
        map = Map(500,500, 3f)

        data.posX = map.getStartingX()
        data.posY = map.getStartingY()

        player.setPlayerData(data)
    }

    override fun render() {
        stepBullets()
        rendermap()
        renderBullets()
        renderplayer()
    }

    private fun rendermap() {
        visibleBlocks = map.getMapNear(data.posX, data.posY, 32)
        blockRenderer.setViewProj(cam.getV(), cam.getP())

        for (i in visibleBlocks.indices)
            blockRenderer.draw(visibleBlocks[i])
    }

    private fun renderplayer() {
        player.setViewProj(cam.getV(), cam.getP())
        player.draw()
    }

    private fun clamp(v1: Float, min: Float, max: Float): Float {
        if (v1 in min..max)
            return v1

        return if (v1 < min) min else max
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

    private fun collisionDetect(x: Float, y: Float): Boolean {
        var ret = false

        for (i in visibleBlocks) {
            ret = doesPlayerCollideWithBlock(i, x, y)
            if (ret) return ret
        }

        return ret
    }

    fun movePlayer(dx: Float, dy: Float) {
        val nextX = data.posX + dx
        val nextY = data.posY + dy

        if (!collisionDetect(nextX, nextY)) {
            data.posX += dx
            data.posY += dy

            cam.setPos(data.posX, data.posY)
        } else if (!collisionDetect(nextX, data.posY)) {
            data.posX += dx

            cam.setPos(data.posX, data.posY)
        } else if (!collisionDetect(data.posX, nextY)) {
            data.posY += dy

            cam.setPos(data.posX, data.posY)
        }
    }

    fun rotatePlayer(rad: Float) {
        data.lookDirection = rad
        shoot(rad)
    }

    fun renderBullets() {
        // To avoid concurrent modifications
        val listCopy = bullets.toMutableList()

        for (b in listCopy) {
            bulletRenderer.draw(b)
        }
    }

    fun stepBullets() {
        bulletRenderer.setViewProj(cam.getV(), cam.getP())

        // To avoid concurrent modifications
        val listCopy = bullets.toMutableList()

        for (b in listCopy) {
            b.posX += cos(b.direction) * b.speed
            b.posY += sin(b.direction) * b.speed

            if (!map.getBlockAt(b.posX, b.posY).passable)
                b.exists = false
        }

        bullets.removeAll { b -> !b.exists }
    }

    var i = 0
    fun shoot(rad: Float) {
        i++
        val dx = cos(rad)
        val dy = sin(rad)
        val size = 0.3f
        val speed = 1f

        bullets.add(BulletData(
            data.posX + dx,
            data.posY + dy,
            rad,
            size,
            speed,
            BulletTextures.standard,
            true
        ))
    }
}