package com.example.fall.logic

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.Log
import com.example.fall.data.*
import kotlin.math.PI
import kotlin.random.Random

class Game(private var context: Context) : IGraphicalGame, Thread() {
    private val mapSize = 100

    private var blockRenderer: BlockRenderer
    private var bulletRenderer: BulletRenderer
    private var map: Map
    private var blockList: MutableList<Block>
    private lateinit var visibleBlocks: MutableList<Block>
    private var bullets = mutableListOf<Bullet>()
    private var monsters = mutableListOf<Monster>()

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
            //Do stuff
            //----------------
            update(timeItTook)
            render()
            glView?.requestRender()
            //----------------
            timeItTook = System.currentTimeMillis() - start
            if (timeItTook == 0L) timeItTook = 1L
            sleep(targetSFP - timeItTook)
            start = System.currentTimeMillis()
        }
    }

    private fun update(timeInMs: Long) {
        updateMonsters(timeInMs)
        updateBullets(timeInMs)
        player.update(timeInMs)
    }

    init {
        map = Map(mapSize,mapSize, 3f)
        player = PistolPlayer(context, map.getStartingX(), map.getStartingY())
        blockList = map.getMap()
        generateMonsters(30)

        blockRenderer = BlockRenderer(context)
        bulletRenderer = BulletRenderer(context)

        blockRenderer.setViewProj(player.cam.getV(), player.cam.getP())

        this.start()
    }

    //private var nrOfMonsters = 10
    private fun generateMonsters(nr: Int) {
        val passable = map.getPassableBlocks()
        if (nr != 0) {
            val blockPerMonster = passable.size / nr

            for (i in 0 until nr) {
                val m =
                    MeleeMonster(
                        context,
                        passable[i * blockPerMonster].posX,
                        passable[i * blockPerMonster].posY,
                        Random.nextFloat() * PI.toFloat()
                    )

                m.setGameRef(this)
                monsters.add(m)
            }
        }
    }

    private fun updateMonsters(timeInMs: Long) {
        val listCopy = monsters.toMutableList()
        val bulletCopy = bullets.toMutableList()

        for (m in listCopy) {
            m.setViewProj(player.cam.getV(), player.cam.getP())
            m.update(timeInMs)

            for (b in bulletCopy)
                b.setExists(!m.doesBulletHitIt(b))

            bullets.removeAll {b -> !b.getExists() }
        }

        monsters.removeAll { m -> !m.isAlive() }
    }

    private fun renderMonsters() {
        val listCopy = monsters.toMutableList()

        for (m in listCopy)
            m.draw()
    }

    override fun setCameraSize(width: Float, height: Float) {
        player.cam.setSize(width, height)
    }

    fun nextLevel() {
        map = Map(mapSize, mapSize, 3f)

        player.data.posX = map.getStartingX()
        player.data.posY = map.getStartingY()
    }

    override fun render() {
        rendermap()
        renderMonsters()
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

    private fun updateBullets(timeMs: Long) {
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

    fun getMap() : Map {
        return map
    }

    fun addBullet(bullet: Bullet) {
        bullets.add(bullet)
    }
}