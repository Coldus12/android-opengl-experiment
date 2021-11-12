package com.example.fall.logic

import android.graphics.PointF
import android.opengl.GLES30
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class Rectangle(centerX: Float, centerY: Float, radius: Float, nr: Int) {
    var center: PointF = PointF(centerX, centerY)
    var nrOfVertices = nr
    var r = radius

    companion object {
        var mat = floatArrayOf(1f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f,
            0f, 0f, 1f, 0f,
            0f, 0f, 0f, 1f)
    }

    private val vertexShaderCode =
        "attribute vec2 vPosition;" +
                "uniform vec2 translation;" +
                "uniform float radius;" +
                "uniform mat4 MVPMatrix;" +
                "void main() {" +
                "  gl_Position = MVPMatrix * vec4(radius * vPosition.x + translation.x, radius * vPosition.y + translation.y, 0, 1);" +
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
            lFloat.add(cos(2*PI/nr * i).toFloat())
            lFloat.add(sin(2*PI/nr * i).toFloat())

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

        //GLES30.glGenVertexArrays

        mProgram = GLES30.glCreateProgram().also {
            GLES30.glAttachShader(it, vertexShader)
            GLES30.glAttachShader(it, fragmentShader)
            GLES30.glLinkProgram(it)

            Log.i("[LINKSTUFF]", GLES30.glGetProgramInfoLog(it))
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

            GLES30.glGetUniformLocation(mProgram, "translation").also { tran ->
                GLES30.glUniform2f(tran, center.x, center.y)
            }

            GLES30.glGetUniformLocation(mProgram, "radius").also { rad ->
                GLES30.glUniform1f(rad, r)
            }

            GLES30.glGetUniformLocation(mProgram, "MVPMatrix").also { ratio ->
                GLES30.glUniformMatrix4fv(ratio, 1, false, mat, 0)
            }

            GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, vertexCount)
            GLES30.glDisableVertexAttribArray(it)
        }
    }
}