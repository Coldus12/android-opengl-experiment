package com.example.fall.opengl

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES30
import android.opengl.GLUtils

class Texture(context: Context, resourceId: Int) {

    private var mTexturehandle: Int

    init {
        mTexturehandle = loadTexture(context, resourceId)
    }

    fun setTexture() {
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTexturehandle)
    }

    private fun loadTexture(context: Context, resourceId: Int): Int {
        val textureHandle = IntArray(1)
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glGenTextures(1, textureHandle, 0)

        if (textureHandle[0] != 0) {
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureHandle[0])
            val options = BitmapFactory.Options()
            options.inScaled = false

            val bmp = BitmapFactory.decodeResource(context.resources, resourceId, options)
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

            return textureHandle[0]
        } else {
            throw RuntimeException("error loading texture")
        }
    }
}