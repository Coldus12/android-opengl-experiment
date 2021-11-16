package com.example.fall.logic

import android.opengl.GLES30
import com.example.fall.data.Player
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
                "   gl_Position = vec4(vPosition.x, vPosition.y, 0, 1) * MVPMatrix;" +
                "}"

    private val fragmentShaderCode =
        "precision highp float;" +
                "uniform vec4 vColor;" +
                "void main() {" +
                "  gl_FragColor = vColor;" +
                "}"

    private val color = floatArrayOf(0f, 0.5f, 1.0f, 1.0f)
    private val FLOAT_SIZE = 4
    private val COORDS_PER_VERTEX = 2
    private var mColorHandle: Int = 0
    private var mPositionHandle: Int = 0
    private val vertexStride: Int = COORDS_PER_VERTEX * FLOAT_SIZE // 4 bytes per vertex
    private var vertexCount: Int = 0 / COORDS_PER_VERTEX
    private val lFloat: MutableList<Float> = arrayListOf()
    private var vertexBuffer: FloatBuffer

    private lateinit var p: Mat4
    private lateinit var v: Mat4

    private var nr = 4

    fun setViewProj(v: Mat4, p: Mat4) {
        this.v = v;
        this.p = p;
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

    fun draw(data: Player, color: FloatArray) {
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
                val r = Mat4.rotMat(data.lookDirection)
                val t = Mat4.translateMat(Vec4(floatArrayOf(data.posX, data.posY, 0f, 1f)))
                val s = Mat4.scaleMat(Vec4(floatArrayOf(1f, 1f, 0f, 1f)))

                val sr = s.multiplyBy(r)
                val m = sr.multiplyBy(t)

                val vp = v.multiplyBy(p)
                val mvp = m.multiplyBy(vp)

                GLES30.glUniformMatrix4fv(it2, 1, true, mvp.getData(), 0)
            }

            GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, vertexCount)
            GLES30.glDisableVertexAttribArray(it)
        }
    }

    fun draw(data: Player) {
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
                val r = Mat4.rotMat(data.lookDirection);
                val t = Mat4.translateMat(Vec4(floatArrayOf(data.posX, data.posY, 0f, 1f)))
                val vt = Mat4.translateMat(Vec4(floatArrayOf(-data.posX, -data.posY, 0f, 1f)))
                val s = Mat4.scaleMat(Vec4(floatArrayOf(1f, 1f, 0f, 1f)))

                val sr = s.multiplyBy(r)
                val m = sr.multiplyBy(t)

                val vp = vt.multiplyBy(p)
                val mvp = m.multiplyBy(vp)

                GLES30.glUniformMatrix4fv(it2, 1, true, mvp.getData(), 0)
            }

            GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, vertexCount)
            GLES30.glDisableVertexAttribArray(it)
        }
    }
}