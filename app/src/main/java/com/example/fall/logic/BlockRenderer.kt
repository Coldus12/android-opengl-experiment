package com.example.fall.logic

import android.content.Context
import android.opengl.GLES30
import android.util.Log
import com.example.fall.R
import com.example.fall.data.Block
import com.example.fall.data.BlockTextureTypes
import com.example.fall.math.Mat4
import com.example.fall.math.Vec4
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.math.PI

class BlockRenderer {
    private lateinit var wall1: Texture
    private lateinit var wall2: Texture
    private lateinit var wall3: Texture
    private lateinit var floor: Texture

    fun loadTextures(context: Context) {
        wall1 = Texture(context, R.drawable.one)
        wall2 = Texture(context, R.drawable.two)
        wall3 = Texture(context, R.drawable.three)
        floor = Texture(context, R.drawable.floor_t)
    }

    private val vertexShaderCode =
        "attribute vec2 vPosition;" +
                "uniform mat4 MVPMatrix;" +
                "varying vec2 texPos;" +
                "void main() {" +
                "   texPos = vec2(vPosition.x + 0.5, vPosition.y + 0.5);" +
                "   gl_Position = vec4(vPosition.x, vPosition.y, 0, 1) * MVPMatrix;" +
                "}"

    private val fragmentShaderCode =
        "precision highp float;" +
                "uniform sampler2D sampler;" +
                "varying vec2 texPos;" +
                "void main() {" +
                "   vec4 texColor = texture2D(sampler, texPos);" +
                "   if (texColor.a < 0.1)" +
                "       discard;" +
                "  gl_FragColor = texColor;" +
                "}"

    private val FLOAT_SIZE = 4
    private val COORDS_PER_VERTEX = 2
    private var mColorHandle: Int = 0
    private var mPositionHandle: Int = 0
    private val vertexStride: Int = COORDS_PER_VERTEX * FLOAT_SIZE // 4 bytes per vertex
    private var vertexCount: Int = 0 / COORDS_PER_VERTEX
    private var vertexBuffer: FloatBuffer

    private lateinit var p: Mat4
    private lateinit var v: Mat4

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

    private var mProgram: Int

    init {
        val lFloat = floatArrayOf(-0.5f, 0.5f,
                                    0.5f, 0.5f,
                                    0.5f, -0.5f,
                                    -0.5f, -0.5f)

        vertexBuffer =
            ByteBuffer.allocateDirect(lFloat.size * FLOAT_SIZE).run {
                order(ByteOrder.nativeOrder())

                asFloatBuffer().apply {
                    put(lFloat)
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

            Log.i("[LOOG]","Program log: ${GLES30.glGetProgramInfoLog(it)}")
            Log.i("[LOOG]","VertexShader log: ${GLES30.glGetShaderInfoLog(vertexShader)}")
            Log.i("[LOOG]","FragmentShader log: ${GLES30.glGetShaderInfoLog(fragmentShader)}")
        }
    }

    fun draw(data: Block) {
        GLES30.glUseProgram(mProgram)

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

            when (data.type) {
                BlockTextureTypes.Wall1 -> wall1.setTexture(GLES30.glGetUniformLocation(mProgram, "sampler"))
                BlockTextureTypes.Wall2 -> wall2.setTexture(GLES30.glGetUniformLocation(mProgram, "sampler"))
                BlockTextureTypes.Wall3 -> wall3.setTexture(GLES30.glGetUniformLocation(mProgram, "sampler"))
                else -> {floor.setTexture(GLES30.glGetUniformLocation(mProgram, "sampler"))}
            }

            GLES30.glGetUniformLocation(mProgram, "MVPMatrix").also { it2 ->
                val r = Mat4.rotMat(0f)
                val t = Mat4.translateMat(Vec4(floatArrayOf(data.posX, data.posY, 0f, 1f)))
                val s = Mat4.scaleMat(Vec4(floatArrayOf(data.blockSize, data.blockSize, 0f, 1f)))

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
}