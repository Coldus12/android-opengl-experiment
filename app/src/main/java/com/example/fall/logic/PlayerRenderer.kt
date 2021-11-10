package com.example.fall.logic

import android.graphics.PointF
import android.opengl.GLES30
import android.util.Log
import com.example.fall.data.Playah
import com.example.fall.math.Mat4
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
        "precision mediump float;" +
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

    private var nr = 4
    var mat = floatArrayOf(1f, 0f, 0f, 0f,
        0f, 1f, 0f, 0f,
        0f, 0f, 1f, 0f,
        0f, 0f, 0f, 1f)

    companion object {
        public var screenRatio: Float = 1f
    }

    fun changeData(data: Playah) {
        mat = floatArrayOf( 1/screenRatio * 1.0f / 3.0f, 0f, 0f, 0f,
                            0f, 1.0f / 3.0f, 0f, 0f,
                            0f, 0f, 1f, 0f,
                            0f, 0f, 0f, 1f)

        val mat2 = Mat4(floatArrayOf(   1.0f, 0.0f, 0.0f, data.position.x,
                                        0.0f, 1.0f, 0.0f, data.position.y,
                                        0.0f, 0.0f, 1.0f, 0.0f,
                                        0.0f, 0.0f, 0.0f, 1.0f))

        val mat1 = Mat4(mat)

        mat = mat2.multiplyBy(mat1).getArray()
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

            //Log.i("[LOOKOUT]","${lFloat[2*i]} and ${lFloat[2*i+1]}")
        }

        vertexBuffer =
            ByteBuffer.allocateDirect(lFloat.size * FLOAT_SIZE).run {
                order(ByteOrder.nativeOrder())

                asFloatBuffer().apply {
                    put(lFloat.toFloatArray())
                    //put(rectCoords)
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

            Log.d("[LOOG]", "FUUUUUUUUUUUU $it")
            Log.i("[LOOG]", GLES30.glGetProgramInfoLog(it))
            Log.d("[LOOG]", "done")
        }
        Log.d("[LOOG]", "LINKING COMPLETE $mProgram")
    }

    fun draw() {
        GLES30.glUseProgram(mProgram)

        vertexCount = lFloat.size / COORDS_PER_VERTEX

        mPositionHandle = GLES30.glGetAttribLocation(mProgram, "vPosition").also {
            Log.i("[LOG]","OH MY FUCKING GOD $it $mProgram")
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

            GLES30.glGetUniformLocation(mProgram, "MVPMatrix").also { ratio ->
                GLES30.glUniformMatrix4fv(ratio, 1, false, mat, 0)
            }

            GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, vertexCount)
            GLES30.glDisableVertexAttribArray(it)
        }
    }
}