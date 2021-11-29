package com.example.fall.game.logic

import android.content.Context
import android.content.Intent
import android.opengl.GLSurfaceView
import android.util.Log
import com.example.fall.GameOverActivity
import com.example.fall.data.*
import com.example.fall.game.graphics.BlockRenderer
import com.example.fall.game.graphics.BulletRenderer
import kotlin.math.PI
import kotlin.random.Random

class Game(private var context: Context, type: PlayerType) : IGraphicalGame, Thread() {
    private var mapSize = 100
    private var nrOfMonsters = 20

    private var blockRenderer: BlockRenderer
    private var bulletRenderer: BulletRenderer
    private var map: Map
    private var blockList: MutableList<Block>
    private lateinit var visibleBlocks: MutableList<Block>
    private var bullets = mutableListOf<Bullet>()
    private var monsters = mutableListOf<Monster>()

    private var width = 0
    private var height = 0

    private var run = true
    private var ready = true

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
            if (ready) {
                update(timeItTook)
                render()
                glView?.requestRender()
                //----------------
                timeItTook = System.currentTimeMillis() - start
                if (timeItTook == 0L) timeItTook = 1L
                if (targetSFP - timeItTook > 0)
                    sleep(targetSFP - timeItTook)
                start = System.currentTimeMillis()
            }
        }
    }

    private fun update(timeInMs: Long) {
        updateMonsters(timeInMs)
        updateBullets(timeInMs)
        player.update(timeInMs)

        if (monsters.size == 0)
            nextLevel()

        if (!player.isAlive())
            gameOver()
    }

    private lateinit var db: DeadPlayersDB
    private fun gameOver() {
        run = false
        val intent = Intent(context, GameOverActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra("score", player.data.score)
        intent.putExtra("levels", player.data.nrOfLevelsReached)

        db = DeadPlayersDB.getDatabase(context)
        db.playerDao().insert(player.data)

        context.startActivity(intent)
    }

    init {
        map = Map(mapSize,mapSize, 3f)

        player = when(type) {
            PlayerType.Pistol -> PistolPlayer(context, map.getStartingX(), map.getStartingY())
            PlayerType.Shotgun -> ShotgunPlayer(context, map.getStartingX(), map.getStartingY())
        }

        blockList = map.getMap()
        generateMonsters(nrOfMonsters)

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
        this.width = width.toInt()
        this.height = height.toInt()

        player.cam.setSize(width, height)
        for (m in monsters)
            m.setScreenData(this.width, this.height)
    }

    private fun nextLevel() {
        ready = false
        glView?.queueEvent {
            nrOfMonsters += 5
            mapSize += 5

            player.data.nrOfLevelsReached++

            // A thousand extra points per level
            player.addScore(1000)

            map = Map(mapSize, mapSize, 3f)
            generateMonsters(nrOfMonsters)

            for (m in monsters)
                m.setScreenData(this.width, this.height)

            player.data.posX = map.getStartingX()
            player.data.posY = map.getStartingY()
            ready = true
        }
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

        val iter = visibleBlocks.iterator()

        while (iter.hasNext()) {
            val b = iter.next()
            blockRenderer.draw(b)
        }
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