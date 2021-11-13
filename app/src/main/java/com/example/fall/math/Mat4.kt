package com.example.fall.math

import kotlin.math.cos
import kotlin.math.sin

class Mat4() {
    private lateinit var row1: Vec4
    private lateinit var row2: Vec4
    private lateinit var row3: Vec4
    private lateinit var row4: Vec4

    constructor(array: FloatArray) : this() {
        if (array.size == 16) {
            row1 = Vec4(array.copyOfRange(0,4))
            row2 = Vec4(array.copyOfRange(4,8))
            row3 = Vec4(array.copyOfRange(8,12))
            row4 = Vec4(array.copyOfRange(12,16))
        }
    }

    constructor(v1: Vec4, v2: Vec4, v3: Vec4, v4: Vec4) : this() {
        row1 = v1
        row2 = v2
        row3 = v3
        row4 = v4
    }

    fun getData() : FloatArray {
        val r1 = row1.getData()
        val r2 = row2.getData()
        val r3 = row3.getData()
        val r4 = row4.getData()

        return floatArrayOf(	r1[0], r1[1], r1[2], r1[3],
            r2[0], r2[1], r2[2], r2[3],
            r3[0], r3[1], r3[2], r3[3],
            r4[0], r4[1], r4[2], r4[3])
    }

    fun multiplyBy(mat: Mat4) : Mat4 {
        val mData = mat.getData()

        val col1 = Vec4(floatArrayOf(mData[0], mData[4], mData[8], mData[12]))
        val col2 = Vec4(floatArrayOf(mData[1], mData[5], mData[9], mData[13]))
        val col3 = Vec4(floatArrayOf(mData[2], mData[6], mData[10], mData[14]))
        val col4 = Vec4(floatArrayOf(mData[3], mData[7], mData[11], mData[15]))

        val ret = Mat4(floatArrayOf(row1.dot(col1), row1.dot(col2), row1.dot(col3), row1.dot(col4),
            row2.dot(col1), row2.dot(col2), row2.dot(col3), row2.dot(col4),
            row3.dot(col1), row3.dot(col2), row3.dot(col3), row3.dot(col4),
            row4.dot(col1), row4.dot(col2), row4.dot(col3), row4.dot(col4)))

        return ret
    }

    fun multiplyBy(vec: Vec4) : Vec4 {
        val r1 = row1.getData()
        val r2 = row2.getData()
        val r3 = row3.getData()
        val r4 = row4.getData()

        val col1 = Vec4(floatArrayOf(r1[0], r2[0], r3[0], r4[0]))
        val col2 = Vec4(floatArrayOf(r1[1], r2[1], r3[1], r4[1]))
        val col3 = Vec4(floatArrayOf(r1[2], r2[2], r3[2], r4[2]))
        val col4 = Vec4(floatArrayOf(r1[3], r2[3], r3[3], r4[3]))

        val ret = Vec4(floatArrayOf(vec.dot(col1), vec.dot(col2), vec.dot(col3), vec.dot(col4)))

        return ret
    }

    fun print() {
        row1.print()
        row2.print()
        row3.print()
        row4.print()
    }

    fun log() {
        row1.log()
        row2.log()
        row3.log()
        row4.log()
    }

    companion object {
        fun translateMat(v: Vec4) : Mat4 {
            val data = v.getData()

            return Mat4(floatArrayOf(   1.0f, 0.0f, 0.0f, 0.0f,
                                        0.0f, 1.0f, 0.0f, 0.0f,
                                        0.0f, 0.0f, 1.0f, 0.0f,
                                        data[0], data[1], data[2], data[3]))
        }

        fun scaleMat(v: Vec4) : Mat4 {
            val data = v.getData()

            return Mat4(floatArrayOf(   data[0] * 1.0f, 0.0f, 0.0f, 0.0f,
                                        0.0f, data[1] * 1.0f, 0.0f, 0.0f,
                                        0.0f, 0.0f, 1.0f, 0.0f,
                                        0.0f, 0.0f, 0.0f, 1.0f))
        }

        fun rotMat(rad: Float) : Mat4 {
            return Mat4(floatArrayOf(   cos(rad), -sin(rad), 0.0f, 0.0f,
                                        sin(rad), cos(rad), 0.0f, 0.0f,
                                        0.0f, 0.0f, 1.0f, 0.0f,
                                        0.0f, 0.0f, 0.0f, 1.0f))
        }
    }
}