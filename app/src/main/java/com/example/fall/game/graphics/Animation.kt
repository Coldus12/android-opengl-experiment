package com.example.fall.game.graphics

import android.content.Context
import android.util.Log
import com.example.fall.game.logic.Updateable

// Animation
//--------------------------------------------------------------------------------------------------
/** Creates a sprite where the smaller images can be set / advanced by giving a time.
 * This works by giving each small image a time it is shown for, and  if enoguh time passed, then
 * the update function changes the current picture.
 * @param context context - required for loading resources
 * @param resourceId the id of the drawable resource
 * @param nrOfRows number of rows
 * @param nrOfColumns number of columns
 * @param spriteWidth the width of the individual smaller images
 * @param spriteHeight the height of the individual smaller images
 * */
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

    /** Sets the times for each small picture
     * @param timesInMs a list containing the times for the textures
     * */
    fun setTimes(timesInMs: MutableList<Long>) {
        timeList = timesInMs.subList(0, nrOfFrames-1)
        currentTime = timeList[0]
        ready = true
    }

    /** Sets a uniform time for all images
     * @param timeInMs the time each texture will be shown for
     * */
    fun setTimes(timeInMs: Long) {
        // Reset times if they are already set
        if (ready)
            timeList = mutableListOf()

        for (i in 0 until nrOfFrames)
            timeList.add(timeInMs)

        currentTime = timeList[0]

        ready = true
    }

    /** Updates the current texture to be correct.
     * */
    override fun update(timeInMs: Long) {
        currentTime -= timeInMs

        if (currentTime <= 0L) {
            currentFrameNr++
            if (currentFrameNr > nrOfFrames - 1)
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