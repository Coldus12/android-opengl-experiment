package com.example.fall

import android.graphics.PointF
import com.example.fall.logic.Camera
import com.example.fall.math.Mat4
import com.example.fall.math.Vec4
import junit.framework.Assert.assertEquals
import org.junit.Assert
import org.junit.Test
import kotlin.math.PI
import kotlin.math.round

class MatMultiplicationTest {
    @Test
    fun matMultiplicationCorrect() {
        // Could've used the mat functions mate.... Anyhow:
        // Translate mat
        val t = Mat4(floatArrayOf(  1f, 0f, 0f, 0f,
                                    0f, 1f, 0f, 0f,
                                    0f, 0f, 1f, 0f,
                                    14f, 12f, 0f, 1f))

        // Rotation mat
        val r = Mat4(floatArrayOf(  0f, -1f, 0f, 0f,
                                    1f, 0f, 0f, 0f,
                                    0f, 0f, 1f, 0f,
                                    0f, 0f, 0f, 1f))

        // Scale mat
        val s = Mat4(floatArrayOf(  3f, 0f, 0f, 0f,
                                    0f, 3f, 0f, 0f,
                                    0f, 0f, 1f, 0f,
                                    0f, 0f, 0f, 1f))

        val tr = t.multiplyBy(r)
        val m = tr.multiplyBy(s)

        val vp = Mat4(floatArrayOf( 1f, 0f, 0f, 0f,
                                    0f, 1f, 0f, 0f,
                                    0f, 0f, 1f, 0f,
                                    -40f, 40f, 0f, 1f))

        val mvp = m.multiplyBy(vp)

        val one = Vec4(floatArrayOf(0f, 0f, 0f, 1f))
        val res = mvp.multiplyBy(one)

        // Simple translate test
        var modelV = t.multiplyBy(one)
        assertEquals(14f, modelV.getData()[0])
        assertEquals(12f, modelV.getData()[1])

        // Translate and rotate test
        modelV = tr.multiplyBy(one)
        assertEquals(12f, modelV.getData()[0])
        assertEquals(-14f, modelV.getData()[1])

        // Translate, rotate, scale test
        modelV = m.multiplyBy(one)
        assertEquals(36f,modelV.getData()[0])
        assertEquals(-42f,modelV.getData()[1])

        // Model (t,r,s) + view test
        assertEquals(-4f,res.getData()[0])
        assertEquals(-2f,res.getData()[1])
    }

    @Test
    fun matMultiplicationWithFunctions() {
        val t = Mat4.translateMat(Vec4(floatArrayOf(14f, 12f, 0f, 1f)))
        val r = Mat4.rotMat((PI/2f).toFloat())
        val s = Mat4.scaleMat(Vec4(floatArrayOf(3f, 3f, 0f, 1f)))

        val tr = t.multiplyBy(r)
        val m = tr.multiplyBy(s)

        val vp = Mat4.translateMat(Vec4(floatArrayOf(40f, -40f, 0f, 1f)))
        val mvp = m.multiplyBy(vp)

        val one = Vec4(floatArrayOf(0f, 0f, 0f, 1f))
        val res = mvp.multiplyBy(one)

        // Simple translate test
        var modelV = t.multiplyBy(one)
        assertEquals(14f, round(modelV.getData()[0]))
        assertEquals(12f, round(modelV.getData()[1]))

        // Translate and rotate test
        modelV = tr.multiplyBy(one)
        assertEquals(-12f, round(modelV.getData()[0]))
        assertEquals(14f, round(modelV.getData()[1]))

        // Translate, rotate, scale test
        modelV = m.multiplyBy(one)
        assertEquals(-36f, round(modelV.getData()[0]))
        assertEquals(42f, round(modelV.getData()[1]))

        // Model (t,r,s) + view test
        assertEquals(4f, round(res.getData()[0]))
        assertEquals(2f, round(res.getData()[1]))
    }

    @Test
    fun matMultiplicationWithCamera() {
        val ogVec = Vec4(floatArrayOf(1f, 0f, 0f, 1f))

        val t = Mat4.translateMat(Vec4(floatArrayOf(10f, 10f, 0f, 1f)))
        val r = Mat4.rotMat((PI/2f).toFloat())
        val s = Mat4.scaleMat(Vec4(floatArrayOf(3f, 3f, 0f, 1f)))

        val sr = s.multiplyBy(r)
        val m = sr.multiplyBy(t)

        val cam = Camera(10f,10f, 0f,0f)

        val mvp = m.multiplyBy(cam.getV())
        val res = mvp.multiplyBy(ogVec)

        // Simple translate test
        var modelV = s.multiplyBy(ogVec)
        assertEquals(3f, round(modelV.getData()[0]))
        assertEquals(0f, round(modelV.getData()[1]))

        // Translate and rotate test
        modelV = sr.multiplyBy(ogVec)
        assertEquals(-0f, round(modelV.getData()[0]))
        assertEquals(3f, round(modelV.getData()[1]))

        // Translate, rotate, scale test
        modelV = m.multiplyBy(ogVec)
        assertEquals(10f, round(modelV.getData()[0]))
        assertEquals(13f, round(modelV.getData()[1]))

        // Model (t,r,s) + view test
        assertEquals(-0f, round(res.getData()[0]))
        assertEquals(3f, round(res.getData()[1]))
    }

    @Test
    fun vecMultiplicationCorrect() {
        val pvec = Vec4(floatArrayOf(14f,12f,0f,1f))
        val camVec = Vec4(floatArrayOf(-10f, -10f, 0f, 1f))

        val expected = Vec4(floatArrayOf(4f, 2f, 0f, 1f))

        val mat = Mat4.translateMat(camVec)

        val result = mat.multiplyBy(pvec)

        assertEquals(expected.getData()[0], result.getData()[0])
        assertEquals(expected.getData()[1], result.getData()[1])
        assertEquals(expected.getData()[2], result.getData()[2])
        assertEquals(expected.getData()[3], result.getData()[3])
    }
}