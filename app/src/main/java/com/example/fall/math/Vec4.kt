package com.example.fall.math

import android.util.Log

class Vec4() {
    private var data = FloatArray(4)

    constructor(array: FloatArray) : this() {
        if (array.size == 4) {
            data = array
        }
    }

    fun getData() : FloatArray
    {
        return data
    }

    fun set(location: Int, value: Float) {
        if (location < 4)
            data[location] = value
    }

    fun dot(v: Vec4) : Float {
        val vd = v.getData()

        return data[0] * vd[0] + data[1] * vd[1] + data[2] * vd[2] + data[3] * vd[3]
    }

    fun print() {
        print("${data[0]} ${data[1]} ${data[2]} ${data[3]} \n")
    }

    fun log() {
        Log.i("[LOOG]","${data[0]} ${data[1]} ${data[2]} ${data[3]} \n")
    }
}