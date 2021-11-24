package com.example.fall.logic

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.Log
import com.example.fall.data.*
import kotlin.math.cos
import kotlin.math.sin

class Game(private var context: Context) : IGraphicalGame {
    private var blockRenderer: BlockRenderer
    private var bulletRenderer: BulletRenderer
    private var map: Map
    private var blockList: MutableList<Block>
    private lateinit var visibleBlocks: MutableList<Block>
    private var bullets = mutableListOf<BulletData>()

    var player: Player
        private set

    private var glView: GLSurfaceView? = null
    fun setGLView(surface: GLSurfaceView) {
        glView = surface
    }

    init {
        map = Map(500,500, 3f)
        player = PistolPlayer(context, map.getStartingX(), map.getStartingY())
        blockList = map.getMap()

        blockRenderer = BlockRenderer(context)
        bulletRenderer = BulletRenderer(context)

        blockRenderer.setViewProj(player.cam.getV(), player.cam.getP())
    }

    override fun setCameraSize(width: Float, height: Float) {
        player.cam.setSize(width, height)
    }

    fun nextLevel() {
        map = Map(500,500, 3f)

        player.data.posX = map.getStartingX()
        player.data.posY = map.getStartingY()
    }

    override fun render() {
        stepBullets()
        rendermap()
        renderBullets()
        renderplayer()
    }

    private fun rendermap() {
        visibleBlocks = map.getMapNear(player.data.posX, player.data.posY, 16)
        blockRenderer.setViewProj(player.cam.getV(), player.cam.getP())

        for (i in visibleBlocks.indices)
            blockRenderer.draw(visibleBlocks[i])
    }

    private fun renderplayer() {
        player.draw()
    }

    private fun renderBullets() {
        // To avoid concurrent modifications
        val listCopy = bullets.toMutableList()

        for (b in listCopy) {
            bulletRenderer.draw(b)
        }
    }

    private fun stepBullets() {
        bulletRenderer.setViewProj(player.cam.getV(), player.cam.getP())

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

    fun getVisibleBlocks() : MutableList<Block> {
        return visibleBlocks
    }

    fun addBullet(bullet: BulletData) {
        bullets.add(bullet)
    }

    /*private var FPS = 30
    private var spf = 1000.0 / 30
    private var run = true
    fun mainloop() {
        while (run) {
            val start = System.currentTimeMillis()
            glView?.requestRender()
            val timeItTook = System.currentTimeMillis() - start
            Log.i("[LOOG]", "Time it took: $timeItTook -> FPS = ${1000.0 / timeItTook}")
            //Thread.sleep(spf.toLong() - timeItTook)
            Thread.sleep(
                if (spf.toLong() - timeItTook <= 0) 20L else spf.toLong() - timeItTook
            )
        }
    }*/
}