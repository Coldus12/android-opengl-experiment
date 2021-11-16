package com.example.fall

import android.graphics.PointF
import com.example.fall.logic.Camera
import com.example.fall.math.Map
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

    // Map generation test
    /*@Test
    fun fff() {
        var map = Map(100,100)
        assertEquals(1,0)
    }*/

    // TRASH
    /*@Test
    fun testThisShit() {
        val m = Mat4(floatArrayOf(0.05f, 0.0f, 0.0f, 0.0f,
                                    0.0f, 0.05f, 0.0f, 0.0f,
                                    0.0f, 0.0f, 1.0f, 0.0f,
                                    3.1606042f, 1.0628605f, 0.0f, 1.0f))

        val vp = Mat4(floatArrayOf(3.761111f, 0.0f, 0.0f, 0.0f,
                                    0.0f, 2.0f, 0.0f, 0.0f,
                                    0.0f, 0.0f, 1.0f, 0.0f,
                                    -11.075193f, -1.0215734f, 0.0f, 1.0f))

        val mvp = Mat4(floatArrayOf(0.18805556f, 0.0f, 0.0f, 0.0f,
                                    0.0f, 0.1f, 0.0f, 0.0f,
                                    0.0f, 0.0f, 1.0f, 0.0f,
                                    0.81219006f, 1.1041476f, 0.0f, 1.0f))

        /*mvp.print()
        val realMvp = m.multiplyBy(vp)
        println()
        realMvp.print()*/

        assertEquals(1,1)
    }*/

    /*@Test
    fun testThisShit2() {
        val m = Mat4(floatArrayOf(0.05f, 0.0f, 0.0f, 0.0f,
                                    0.0f, 0.05f, 0.0f, 0.0f,
                                    0.0f, 0.0f, 1.0f, 0.0f,
                                    3.31683f, 2.7730212f, 0.0f, 1.0f))

        //data posX = 3.31683
        //data posY = 2.7730212

        val v = Mat4(floatArrayOf(1.0f, 0.0f, 0.0f, 0.0f,
                                    0.0f, 1.0f, 0.0f, 0.0f,
                                    0.0f, 0.0f, 1.0f, 0.0f,
                                    -3.0146794f, -3.162562f, 0.0f, 1.0f))

        //camera posX = 3.0146794
        //camera posY = 3.162562

        val p = Mat4(floatArrayOf(3.761111f, 0.0f, 0.0f, 0.0f,
                                    0.0f, 2.0f, 0.0f, 0.0f,
                                    0.0f, 0.0f, 1.0f, 0.0f,
                                    0.0f, 0.0f, 0.0f, 1.0f))

        val vp = Mat4(floatArrayOf(3.761111f, 0.0f, 0.0f, 0.0f,
                                    0.0f, 2.0f, 0.0f, 0.0f,
                                    0.0f, 0.0f, 1.0f, 0.0f,
                                    -11.338544f, -6.325124f, 0.0f, 1.0f))

        val mvp = Mat4(floatArrayOf(0.18805556f, 0.0f, 0.0f, 0.0f,
                                    0.0f, 0.1f, 0.0f, 0.0f,
                                    0.0f, 0.0f, 1.0f, 0.0f,
                                    1.1364212f, -0.77908134f, 0.0f, 1.0f))

        val mv = m.multiplyBy(v)
        val mvpR = mv.multiplyBy(p)
        mv.print()

        /*val realVp = v.multiplyBy(p)
        realVp.print()
        println()
        vp.print()
        println()
        val realMvP = m.multiplyBy(vp)
        realMvP.print()
        println()
        mvp.print()*/

        assertEquals(0,1)
    }*/

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