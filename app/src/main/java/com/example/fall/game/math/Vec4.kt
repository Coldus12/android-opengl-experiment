package com.example.fall.game.math

import android.util.Log

// Vec4
//--------------------------------------------------------------------------------------------------
/** A vector with 4 elements
 * */
class Vec4() {
    private var data = FloatArray(4)

    /** Initializes the vector with the given array (if the array has exactly 4 elements)
     * @param array the elements of the vec
     * */
    constructor(array: FloatArray) : this() {
        if (array.size == 4) {
            data = array
        }
    }

    /** Returns the elements in the vector
     * @return elements of the vector
     * */
    fun getData() : FloatArray
    {
        return data
    }

    /** Changes the value of one of the elements of the vector
     * @param location the element with the value to be changed
     * @param value new value
     * */
    fun set(location: Int, value: Float) {
        if (location < 4)
            data[location] = value
    }

    /** The dot product with another vector
     * @param v the other vector
     * @return the resulting dot product
     * */
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