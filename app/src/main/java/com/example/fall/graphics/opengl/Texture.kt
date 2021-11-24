package com.example.fall.graphics.opengl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES30
import android.opengl.GLUtils

class Texture() {

    private var mTexturehandle: Int = -1

    constructor(context: Context, resourceId: Int) : this() {
        loadTexture(context, resourceId)
    }

    constructor(bmp: Bitmap) : this() {
        loadTexture(bmp)
    }

    fun setTexture() {
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTexturehandle)
    }

    private fun loadTexture(bmp: Bitmap) {
        val textureHandle = IntArray(1)
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glGenTextures(1, textureHandle, 0)

        if (textureHandle[0] != 0) {
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureHandle[0])
            GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, bmp, 0)
            bmp.recycle()

            GLES30.glTexParameteri(
                GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_MIN_FILTER,
                GLES30.GL_NEAREST
            )

            GLES30.glTexParameteri(
                GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_MAG_FILTER,
                GLES30.GL_NEAREST
            )

            mTexturehandle = textureHandle[0]
        } else {
            throw RuntimeException("error loading texture")
        }
    }

    private fun loadTexture(context: Context, resourceId: Int) {
        val options = BitmapFactory.Options()
        options.inScaled = false
        val bmp = BitmapFactory.decodeResource(context.resources, resourceId, options)

        loadTexture(bmp)
    }
}