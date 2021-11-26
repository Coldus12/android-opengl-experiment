package com.example.fall.game.graphics

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.fall.game.graphics.opengl.Texture

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

    fun getFrameNr() : Int {
        return textures.size
    }

    fun setTexture(x: Int, y: Int) {
        if ((x in 0 until nrOfColumns) && (y in 0 until nrOfRows)) {
            textures[y * nrOfRows + x].setTexture()
            return
        }

        Log.e("[Sprite]","Index out bounds! x: $x y: $y; bounds: 0 <= x < $nrOfColumns && 0 <= y < $nrOfRows")
        textures[0].setTexture()
    }

    fun setTexture(frameNr: Int) {
        if (frameNr in 0 until textures.size)
            textures[frameNr].setTexture()
        else
            textures[0].setTexture()
    }
}