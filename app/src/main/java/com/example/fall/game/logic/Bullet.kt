package com.example.fall.game.logic

import com.example.fall.data.game_data.BulletData
import kotlin.math.cos
import kotlin.math.sin

// Bullet
//--------------------------------------------------------------------------------------------------
/** A wrapper around the data class.
 *  The only useful property of this class is that it has an update function.
 *  @param data the actualy bullet data
 * */
class Bullet(private var data: BulletData) : Updateable {

    fun getData() : BulletData {
        return data
    }

    fun setExists(exists: Boolean) {
        data.exists = exists
    }

    fun getPosX() : Float {
        return data.posX
    }

    fun getPosY() : Float {
        return data.posY
    }

    fun getExists() : Boolean {
        return data.exists
    }

    override fun update(timeInMs: Long) {
        data.posX += cos(data.direction) * data.speed * timeInMs/100
        data.posY += sin(data.direction) * data.speed * timeInMs/100
    }
}