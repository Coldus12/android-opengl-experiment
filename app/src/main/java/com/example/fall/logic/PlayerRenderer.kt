package com.example.fall.logic

import android.graphics.Color
import android.graphics.PointF
import android.opengl.GLES30
import android.util.Log
import com.example.fall.data.Playah
import com.example.fall.math.Mat4
import com.example.fall.math.Vec4
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class PlayerRenderer {
    private val vertexShaderCode =
        "attribute vec2 vPosition;" +
                "uniform mat4 MVPMatrix;" +
                "void main() {" +
                "   gl_Position = MVPMatrix * vec4(vPosition.x, vPosition.y, 0, 1);" +
                "}"

    private val fragmentShaderCode =
        "precision highp float;" +
                "uniform vec4 vColor;" +
                "void main() {" +
                "  gl_FragColor = vColor;" +
                "}"

    private val color = floatArrayOf(0f, 0.5f, 0.0f, 1.0f)
    private val FLOAT_SIZE = 4
    private val COORDS_PER_VERTEX = 2
    private var mColorHandle: Int = 0
    private var mPositionHandle: Int = 0

    private val vertexStride: Int = COORDS_PER_VERTEX * FLOAT_SIZE // 4 bytes per vertex
    private var vertexCount: Int = 0 / COORDS_PER_VERTEX

    private val lFloat: MutableList<Float> = arrayListOf()

    private var vertexBuffer: FloatBuffer

    private lateinit var rotMat: Mat4
    private lateinit var translate: Mat4
    private lateinit var ratio: Mat4
    private lateinit var vp: Mat4

    private var nr = 4
    var mat = floatArrayOf(1f, 0f, 0f, 0f,
        0f, 1f, 0f, 0f,
        0f, 0f, 1f, 0f,
        0f, 0f, 0f, 1f)

    fun setCamera(vp: Mat4) {
        this.vp = vp
    }

    fun rotate(angle: Float) {
        rotMat = Mat4(floatArrayOf(     cos(angle), sin(angle), 0.0f, 0.0f,
                                        -sin(angle), cos(angle), 0.0f, 0.0f,
                                        0.0f, 0.0f, 0.0f, 0.0f,
                                        0.0f, 0.0f, 0.0f, 1.0f))
    }

    fun changeData(data: Playah) {
        mat = floatArrayOf( 1.0f / 10.0f, 0f, 0f, 0f,
                            0f, 1.0f / 10.0f, 0f, 0f,
                            0f, 0f, 1f, 0f,
                            0f, 0f, 0f, 1f)

        ratio = Mat4(mat)

        translate = Mat4(floatArrayOf(  1.0f, 0.0f, 0.0f, 0.0f,
                                        0.0f, 1.0f, 0.0f, 0.0f,
                                        0.0f, 0.0f, 1.0f, 0.0f,
                                        data.position.x, data.position.y, 0.0f, 1.0f))
    }

    fun loadShader(type: Int, shaderCode: String): Int {
        return  GLES30.glCreateShader(type).also { shader ->
            GLES30.glShaderSource(shader, shaderCode)
            GLES30.glCompileShader(shader)
        }
    }

    fun changeColor(r: Float, g: Float, b: Float, a: Float) {
        color[0] = r
        color[1] = g
        color[2] = b
        color[3] = a
    }

    private var mProgram: Int

    init {
        rotMat = Mat4(floatArrayOf(1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f))


        for (i in 0..nr) {
            lFloat.add(cos(2* PI /nr * i).toFloat())
            lFloat.add(sin(2* PI /nr * i).toFloat())
        }

        vertexBuffer =
            ByteBuffer.allocateDirect(lFloat.size * FLOAT_SIZE).run {
                order(ByteOrder.nativeOrder())

                asFloatBuffer().apply {
                    put(lFloat.toFloatArray())
                    position(0)
                }
            }

        vertexCount = lFloat.size / COORDS_PER_VERTEX

        val vertexShader: Int = loadShader(GLES30.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader: Int = loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderCode)

        mProgram = GLES30.glCreateProgram().also {
            GLES30.glAttachShader(it, vertexShader)
            GLES30.glAttachShader(it, fragmentShader)
            GLES30.glLinkProgram(it)
        }
    }

    fun draw(data: Playah, color: FloatArray) {
        GLES30.glUseProgram(mProgram)

        vertexCount = lFloat.size / COORDS_PER_VERTEX

        mPositionHandle = GLES30.glGetAttribLocation(mProgram, "vPosition").also {
            GLES30.glEnableVertexAttribArray(it)

            GLES30.glVertexAttribPointer(
                it,
                COORDS_PER_VERTEX,
                GLES30.GL_FLOAT,
                false,
                vertexStride,
                vertexBuffer
            )

            mColorHandle = GLES30.glGetUniformLocation(mProgram, "vColor").also { colorHandle ->
                // Set color for drawing the triangle
                GLES30.glUniform4fv(colorHandle, 1, color, 0)
            }

            GLES30.glGetUniformLocation(mProgram, "MVPMatrix").also { it2 ->
                val r = Mat4.rotMat(0f);
                val t = Mat4.translateMat(Vec4(floatArrayOf(data.position.x, data.position.y, 0f, 1f)))
                val s = Mat4.scaleMat(Vec4(floatArrayOf(0.05f, 0.05f, 0f, 1f)))

                val tr = t.multiplyBy(r)
                val m = tr.multiplyBy(s)
                val mvp = m.multiplyBy(vp).getData()

                GLES30.glUniformMatrix4fv(it2, 1, false, mvp, 0)
            }

            GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, vertexCount)
            GLES30.glDisableVertexAttribArray(it)
        }
    }

    fun draw(data: Playah) {
        GLES30.glUseProgram(mProgram)

        vertexCount = lFloat.size / COORDS_PER_VERTEX

        mPositionHandle = GLES30.glGetAttribLocation(mProgram, "vPosition").also {
            GLES30.glEnableVertexAttribArray(it)

            GLES30.glVertexAttribPointer(
                it,
                COORDS_PER_VERTEX,
                GLES30.GL_FLOAT,
                false,
                vertexStride,
                vertexBuffer
            )

            mColorHandle = GLES30.glGetUniformLocation(mProgram, "vColor").also { colorHandle ->

                // Set color for drawing the triangle
                GLES30.glUniform4fv(colorHandle, 1, color, 0)
            }

            GLES30.glGetUniformLocation(mProgram, "MVPMatrix").also { it2 ->
                val r = Mat4.rotMat(0f);
                val t = Mat4.translateMat(Vec4(floatArrayOf(data.position.x, data.position.y, 0f, 1f)))
                val s = Mat4.scaleMat(Vec4(floatArrayOf(0.05f, 0.05f, 0f, 1f)))

                val tr = t.multiplyBy(r)
                val m = tr.multiplyBy(s)
                val mvp = m.multiplyBy(vp).getData()

                GLES30.glUniformMatrix4fv(it2, 1, false, mvp, 0)
            }

            GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, vertexCount)
            GLES30.glDisableVertexAttribArray(it)
        }
    }

    fun draw() {
        GLES30.glUseProgram(mProgram)

        vertexCount = lFloat.size / COORDS_PER_VERTEX

        mPositionHandle = GLES30.glGetAttribLocation(mProgram, "vPosition").also {
            GLES30.glEnableVertexAttribArray(it)

            GLES30.glVertexAttribPointer(
                it,
                COORDS_PER_VERTEX,
                GLES30.GL_FLOAT,
                false,
                vertexStride,
                vertexBuffer
            )

            mColorHandle = GLES30.glGetUniformLocation(mProgram, "vColor").also { colorHandle ->

                // Set color for drawing the triangle
                GLES30.glUniform4fv(colorHandle, 1, color, 0)
            }

            GLES30.glGetUniformLocation(mProgram, "MVPMatrix").also { it2 ->
                val tr = rotMat.multiplyBy(translate)
                val m = tr.multiplyBy(ratio)
                val mvp = m.multiplyBy(vp).getData()

                GLES30.glUniformMatrix4fv(it2, 1, false, mvp, 0)
            }

            GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, vertexCount)
            GLES30.glDisableVertexAttribArray(it)
        }
    }
}