package com.example.fall.logic

import android.content.Context
import android.opengl.GLES30
import android.util.Log
import com.example.fall.data.Block
import com.example.fall.data.BlockTextureTypes
import com.example.fall.data.PlayerData
import com.example.fall.data.PlayerStates
import com.example.fall.math.Map
import kotlin.math.roundToInt

class Game(context: Context) {
    private var player: Player
    private var blockRenderer: BlockRenderer
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
    private var context: Context

    init {
        this.context = context

        map = Map(500,500, 3f)
        data.posX = map.getStartingX()
        data.posY = map.getStartingY()
        blockList = map.getMap()

        player = Player()
        player.setPlayerData(data)
        player.loadTexture(context)
        blockRenderer = BlockRenderer()
        blockRenderer.loadTextures(context)

        cam = Camera(data.posX, data.posY, 1f, 1f)
        cam.zoom(70f)

        player.setViewProj(cam.getV(), cam.getP())
        blockRenderer.setViewProj(cam.getV(), cam.getP())
    }

    fun setCameraSize(width: Float, height: Float) {
        cam.setSize(width, height)
    }

    fun nextLevel() {
        map = Map(500,500, 3f)

        data.posX = map.getStartingX()
        data.posY = map.getStartingY()

        player.setPlayerData(data)
    }

    fun render() {
        rendermap()
        renderplayer()
    }

    private fun rendermap() {
        visibleBlocks = map.getMapNear(data.posX, data.posY, 32)
        blockRenderer.setViewProj(cam.getV(), cam.getP())

        for (i in visibleBlocks.indices) {
            blockRenderer.draw(visibleBlocks[i])
        }
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
    }
}