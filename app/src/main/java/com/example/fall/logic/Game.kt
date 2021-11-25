package com.example.fall.logic

import android.content.Context
import android.opengl.GLSurfaceView
import com.example.fall.data.*

class Game(private var context: Context) : IGraphicalGame, Thread() {
    private var blockRenderer: BlockRenderer
    private var bulletRenderer: BulletRenderer
    private var map: Map
    private var blockList: MutableList<Block>
    private lateinit var visibleBlocks: MutableList<Block>
    private var bullets = mutableListOf<Bullet>()

    private var monster: Monster

    private var run = true

    var player: Player
        private set

    private var glView: GLSurfaceView? = null
    fun setGLView(surface: GLSurfaceView) {
        glView = surface
    }

    private val targetFPS = 30L
    override fun run() {
        val targetSFP = 1000L/targetFPS
        var start = System.currentTimeMillis()
        var timeItTook = 0L

        while (run) {
            timeItTook = System.currentTimeMillis() - start
            if (timeItTook == 0L) timeItTook = 1L
            //Do stuff
            //----------------
            update(timeItTook)
            render()
            glView?.requestRender()
            //----------------
            sleep(targetSFP - timeItTook)
            start = System.currentTimeMillis()
        }
    }

    private fun update(timeInMs: Long) {
        stepBullets(timeInMs)
        player.update(timeInMs)
    }

    init {
        map = Map(500,500, 3f)
        player = PistolPlayer(context, map.getStartingX(), map.getStartingY())
        monster = MeleeMonster(context, map.getStartingX(), map.getStartingY(), 0f)
        blockList = map.getMap()

        blockRenderer = BlockRenderer(context)
        bulletRenderer = BulletRenderer(context)

        blockRenderer.setViewProj(player.cam.getV(), player.cam.getP())

        this.start()
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
        monster.setViewProj(player.cam.getV(), player.cam.getP())
        monster.draw()

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
            bulletRenderer.draw(b.getData())
        }
    }

    private fun stepBullets(timeMs: Long) {
        bulletRenderer.setViewProj(player.cam.getV(), player.cam.getP())

        // To avoid concurrent modifications
        val listCopy = bullets.toMutableList()

        for (b in listCopy) {
            b.update(timeMs)
            if (!map.getBlockAt(b.getPosX(), b.getPosY()).passable)
                b.setExists(false)
        }

        bullets.removeAll { b -> !b.getExists() }
    }

    fun getVisibleBlocks() : MutableList<Block> {
        return visibleBlocks
    }

    fun addBullet(bullet: Bullet) {
        bullets.add(bullet)
    }
}