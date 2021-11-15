package com.example.fall.logic

import com.example.fall.math.Mat4
import com.example.fall.math.Vec4

class Camera(posX: Float, posY: Float, cWidth: Float, cHeight: Float) {

    private var centerX: Float
    private var centerY: Float
    private var sizeWidth: Float
    private var sizeHeight: Float

    init {
        centerX = posX
        centerY = posY
        sizeWidth = cWidth
        sizeHeight = cHeight
    }

    fun setPos(posX: Float, posY: Float) {
        centerX = posX
        centerY = posY
    }

    fun getV() : Mat4 {
        return Mat4.translateMat(Vec4(floatArrayOf(-centerX, -centerY, 0f, 1f)))
    }

    fun getP() : Mat4 {
        return Mat4.scaleMat(Vec4(floatArrayOf(2f/sizeWidth, 2f/sizeHeight, 0f, 1f)))
    }

    fun getVinv() : Mat4 {
        return Mat4.translateMat(Vec4(floatArrayOf(centerX, centerY, 0f, 1f)))
    }

    fun getPinv() : Mat4 {
        return Mat4.scaleMat(Vec4(floatArrayOf(sizeWidth/2f, sizeHeight/2f, 0f, 1f)))
    }

    fun zoom(s: Float) {
        sizeWidth *= s
        sizeHeight *= s
    }

    fun pan(dX: Float, dY: Float) {
        centerX += dX
        centerY += dY
    }
}