package com.example.fall.game.logic

import android.content.Context
import com.example.fall.R
import com.example.fall.data.game_data.MonsterData
import com.example.fall.game.graphics.opengl.Shader
import com.example.fall.game.graphics.opengl.Texture
import com.example.fall.game.math.Mat4

// Monster
//--------------------------------------------------------------------------------------------------
/** The parent class which the different monster "classes" inherit from.
 * @param context Context to load the necessary resources from drawables
 * */
abstract class Monster(private var context: Context) : Creature() {
    // Data
    //----------------------------------------------------------------------------------------------
    protected var speed = 5f
    protected open lateinit var p: Mat4
    protected open lateinit var v: Mat4

    open fun setViewProj(v: Mat4, p: Mat4) {
        this.v = v
        this.p = p
    }

    lateinit var data: MonsterData
        protected set

    protected open var coordsPerVertex = 2
    protected open var monsterGeometry =
        floatArrayOf(
            -1f, 1f,
            1f, 1f,
            1f, -1f,
            -1f, -1f
        )

    // OpenGL load functions
    //----------------------------------------------------------------------------------------------
    override fun loadTexture() {
        texture = Texture(context, data.resourceId)
    }

    override fun loadShader() {
        shader = Shader(
            context,
            R.raw.monster_vertex_shader,
            R.raw.monster_fragment_shader,
            monsterGeometry,
            coordsPerVertex,
            "vPosition"
        )
    }

    // Basic monster functions
    //----------------------------------------------------------------------------------------------
    /** Most monsters use a monsterIndicator to lead the player to them. This function sets the
     * screen data/ratio of the monsterIndicator through the monster itself.
     *
     * @param width screen width
     * @param height screen height
     * */
    abstract fun setScreenData(width: Int, height: Int)
    abstract fun isAlive() : Boolean
    /** Collision detection with a bullet.
     *  @param bullet bullet that might have hit the monster
     *  @return did the bulllet hit the monster
     * */
    abstract fun doesBulletHitIt(bullet: Bullet) : Boolean
    /** The monster needs a reference to the game for a myriad of reasons. One example would be the
     * monster's movement since it needs to know where the blocks in the map are, so it can avoid
     * them.
     * @param game the game
     * */
    abstract fun setGameRef(game: Game)
    /** What happens if a monster attacks the player.
     * @param player the player the monster is attacking
     * */
    abstract fun attack(player: Player)
}