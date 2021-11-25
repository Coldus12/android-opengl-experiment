package com.example.fall.logic

import com.example.fall.data.BulletData
import kotlin.math.cos
import kotlin.math.sin

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