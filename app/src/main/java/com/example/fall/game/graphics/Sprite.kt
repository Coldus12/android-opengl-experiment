package com.example.fall.game.graphics

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.fall.game.graphics.opengl.Texture

// Sprite
//--------------------------------------------------------------------------------------------------
/** An image made up of smaller images ordered in columns/rows.
 * @param context context - required for loading resources
 * @param resourceId the id of the drawable resource
 * @param nrOfRows number of rows
 * @param nrOfColumns number of columns
 * @param spriteWidth the width of the individual smaller images
 * @param spriteHeight the height of the individual smaller images
 * */
class Sprite(private var context: Context, private var resourceId: Int,
             private var nrOfRows: Int, private var nrOfColumns: Int,
             private var spriteWidth: Int, private var spriteHeight: Int) {

    private var textures: MutableList<Texture>

    init {
        val list = getSubBmps()
        textures = mutableListOf<Texture>()

        for (i in list) {
            textures.add(
                Texture(i)
            )
        }
    }

    /** Loads the bitmap and then cuts it up into the smaller images and stores it in a list.
     * @return the list containing the smaller bitmaps
     * */
    private fun getSubBmps() : MutableList<Bitmap> {
        val options = BitmapFactory.Options()
        options.inScaled = false
        val bmp = BitmapFactory.decodeResource(context.resources, resourceId, options)

        val list = mutableListOf<Bitmap>()
        for (i in 0 until nrOfColumns) {
            for (j in 0 until nrOfRows) {
                val listItem = Bitmap.createBitmap(
                    bmp,
                    i * spriteWidth,
                    j * spriteWidth,
                    spriteWidth,
                    spriteHeight
                )

                list.add(listItem)
            }
        }

        return list
    }

    /** @return number of frames/smaller images in this sprite
     * */
    fun getFrameNr() : Int {
        return textures.size
    }

    /** Sets the texture at the given x,y coordinate
     * @param x img location in a row (which column is the picture in)
     * @param y img location in a column (which row is the picture in)
     * */
    fun setTexture(x: Int, y: Int) {
        if ((x in 0 until nrOfColumns) && (y in 0 until nrOfRows)) {
            textures[y * nrOfRows + x].setTexture()
            return
        }

        Log.e("[Sprite]","Index out bounds! x: $x y: $y; bounds: 0 <= x < $nrOfColumns && 0 <= y < $nrOfRows")
        textures[0].setTexture()
    }

    /** Sets the texture at the given location in the stored texture list
     * @param frameNr texture location
     * */
    fun setTexture(frameNr: Int) {
        if (frameNr in 0 until textures.size)
            textures[frameNr].setTexture()
        else
            textures[0].setTexture()
    }
}