package com.example.fall.math

class Mat4() {
    private var data = FloatArray(16)

    constructor(array: FloatArray) : this() {
        data = array
    }

    fun get(colNr: Int, rowNr: Int): Float {
        return data[rowNr * 4 + colNr]
    }

    fun set(colNr: Int, rowNr: Int, value: Float) {
        data[rowNr * 4 + colNr] = value
    }

    fun getArray(): FloatArray {
        return data
    }

    fun multiplyBy(mat4: Mat4): Mat4 {
        val ret = Mat4(data)

        for (i in 0..3) {
            for (j in 0..3) {
                var dRet = 0.0f

                for (n in 0..3) {
                    dRet += get(n, j) * mat4.get(i, n)
                }

                ret.set(i, j, dRet)
            }
        }

        return ret
    }

    fun multiplyBy(vec4: Vec4) {
        //TODO
    }
}