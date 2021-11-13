package com.example.fall.logic

import android.graphics.Point
import android.graphics.PointF
import com.example.fall.math.Mat4
import com.example.fall.math.Vec4

class Camera(private var center: PointF, private var size: PointF) {

    private var cVec = Vec4(floatArrayOf(center.x, center.y, 0f, 1f))
    private var minusCVec = Vec4(floatArrayOf(-center.x, -center.y, 0f, 1f))
    private var sVec = Vec4(floatArrayOf(2f/size.x, 2f/size.y, 0f, 1f))
    private var sVecInv = Vec4(floatArrayOf(size.x/2f, size.y/2f, 0f, 1f))

    fun setPos(v: PointF) {
        center = v
        cVec = Vec4(floatArrayOf(center.x, center.y, 0f, 1f))
        minusCVec = Vec4(floatArrayOf(-center.x, -center.y, 0f, 1f))
    }

    fun getV() : Mat4 {
        return Mat4.translateMat(minusCVec)
    }

    fun getP() : Mat4 {
        return Mat4.scaleMat(sVec)
    }

    fun getVinv() : Mat4 {
        return Mat4.translateMat(cVec)
    }

    fun getPinv() : Mat4 {
        return Mat4.scaleMat(sVecInv)
    }

    fun zoom(s: Float) {
        size.x *= s
        size.y *= s
    }

    fun pan(v: PointF) {
        center.x += v.x
        center.y += v.y

        cVec = Vec4(floatArrayOf(center.x, center.y, 0f, 1f))
        minusCVec = Vec4(floatArrayOf(-center.x, -center.y, 0f, 1f))
    }
}