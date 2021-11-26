package com.example.fall.game.graphics

import android.content.Context
import com.example.fall.game.logic.Updateable

class Animation(private var context: Context, private var resourceId: Int,
                private var nrOfRows: Int, private var nrOfColumns: Int,
                private var spriteWidth: Int, private var spriteHeight: Int) : Updateable {

    private var sprite: Sprite =
        Sprite(context, resourceId, nrOfRows, nrOfColumns, spriteWidth, spriteHeight)

    private var nrOfFrames: Int = sprite.getFrameNr()
    private var ready = false
    private var timeList: MutableList<Long> = mutableListOf()
    private var currentFrameNr = 0
    private var currentTime = 0L

    fun setTimes(timesInMs: MutableList<Long>) {
        timeList = timesInMs.subList(0, nrOfFrames-1)
        currentTime = timeList[0]
        ready = true
    }

    fun setTimes(timeInMs: Long) {
        // Reset times if they are already set
        if (ready)
            timeList = mutableListOf()

        for (i in 0 until nrOfFrames)
            timeList.add(timeInMs)

        currentTime = timeList[0]

        ready = true
    }

    override fun update(timeInMs: Long) {
        currentTime -= timeInMs

        if (currentTime <= 0L) {
            currentFrameNr++
            if (currentFrameNr >= nrOfFrames - 1)
                currentFrameNr = 0

            currentTime = timeList[currentFrameNr]
        }
    }

    fun setFrame(nr: Int) {
        return sprite.setTexture(nr)
    }

    fun setCurrentFrame() {
        sprite.setTexture(currentFrameNr)
    }
}