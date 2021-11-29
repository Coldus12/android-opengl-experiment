package com.example.fall.game.graphics.opengl

import android.content.Context
import android.opengl.GLES30
import android.util.Log
import com.example.fall.game.math.Mat4
import com.example.fall.game.math.Vec4
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

// Shader class
//--------------------------------------------------------------------------------------------------
/** OpenGL shader class.
 * Loads the shader resources, compiles them, runs them. Capable of displaying a given geometry.
 * */
class Shader(private var context: Context) {

    private var mProgram = -1

    private lateinit var vertexShaderCode: String
    private lateinit var fragmentShaderCode: String

    private var loaded = false
    private lateinit var geometryName: String
    private lateinit var vertexBuffer: FloatBuffer
    private val floatSize = 4
    private var coordsPerVertex = 0
    private var vertexStride = 0
    private var vertexCount = 0

    /** Loads and initializes a shader with the given data.
     * @param context Application context
     * @param vertex the resource id for the vertex shader
     * @param fragment the resource id for the fragment shader
     * @param geometryData a floatArray containing the geometry
     * @param coords_per_vertex how many floats make up a vertex in the given geometryData
     * @param geometryName what attribute (name?) to load the geometry data into
     * */
    constructor(context: Context, vertex: Int, fragment: Int, geometryData: FloatArray, coords_per_vertex: Int, geometryName: String) : this(context) {
        loadShaderCode(vertex, fragment)
        loadGeometry(geometryData, coords_per_vertex, geometryName)
    }
    fun getProgramId(): Int {
        return mProgram
    }

    /** Sets the shader to be used. After this function call uniform calls, and drawGeometry calls,
     * and even texture "set"-s can be called.
     * */
    fun useProgram() {
        GLES30.glUseProgram(mProgram)
    }

    /** Draws the geometry stored in the provided attribute.
     * */
    fun drawGeometry() {
        useProgram()
        GLES30.glGetAttribLocation(mProgram, geometryName).also {
            GLES30.glEnableVertexAttribArray(it)

            GLES30.glVertexAttribPointer(
                it,
                coordsPerVertex,
                GLES30.GL_FLOAT,
                false,
                vertexStride,
                vertexBuffer
            )

            GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, vertexCount)

            GLES30.glDisableVertexAttribArray(it)
        }
    }

    /** Updates the geometry data.
     * @param geometryData the newer geometry data
     * @param coords_per_vertex how many floats make up a vertex in the new geometry data
     * */
    fun updateGeometry(geometryData: FloatArray, coords_per_vertex: Int) {
        this.coordsPerVertex = coords_per_vertex

        vertexBuffer =
            ByteBuffer.allocateDirect(geometryData.size * floatSize).run {
                order(ByteOrder.nativeOrder())

                asFloatBuffer().apply {
                    put(geometryData)
                    position(0)
                }
            }

        vertexCount = geometryData.size / coords_per_vertex
        vertexStride = coords_per_vertex * floatSize
    }

    /** Loads the provided geometry into the provided attribute.
     * @param geometryData geometry data
     * @param coords_per_vertex how many floats make up a vertex
     * @param name name of the attribute
     * */
    fun loadGeometry(geometryData: FloatArray, coords_per_vertex: Int, name: String) {
        this.coordsPerVertex = coords_per_vertex

        vertexBuffer =
            ByteBuffer.allocateDirect(geometryData.size * floatSize).run {
                order(ByteOrder.nativeOrder())

                asFloatBuffer().apply {
                    put(geometryData)
                    position(0)
                }
            }

        vertexCount = geometryData.size / coords_per_vertex
        vertexStride = coords_per_vertex * floatSize

        val vertexShader: Int = loadShader(GLES30.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader: Int = loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderCode)

        mProgram = GLES30.glCreateProgram().also {
            GLES30.glAttachShader(it, vertexShader)
            GLES30.glAttachShader(it, fragmentShader)
            GLES30.glLinkProgram(it)

            Log.i("[Shader]","Program log: ${GLES30.glGetProgramInfoLog(it)}")
            Log.i("[Shader]","VertexShader log: ${GLES30.glGetShaderInfoLog(vertexShader)}")
            Log.i("[Shader]","FragmentShader log: ${GLES30.glGetShaderInfoLog(fragmentShader)}")
        }

        geometryName = name
    }

    /** Loads a vector into the shader onto the GPU to the given id.
     * @param vec vector to be loaded onto the GPU
     * @param id the id of the uniform vector
     * */
    fun setUniformVec(vec: Vec4, id: String) {
        val location = getLocation(id)
        val data = vec.getData()

        if (location != -1)
            GLES30.glUniform4f(location, data[0], data[1], data[2], data[3])
    }

    /** Loads a matrix into the shader onto the GPU to the given id.
     * @param mat the matrix to be loaded onto the GPU
     * @param id the id of the uniform matrix
     * */
    fun setUniformMat(mat: Mat4, id: String) {
        val location = getLocation(id)
        if (location != -1)
            GLES30.glUniformMatrix4fv(location, 1, true, mat.getData(), 0)
    }

    fun getLocation(id: String) : Int {
        val ret = GLES30.glGetUniformLocation(mProgram, id)

        return if (ret >= 0)
            ret
        else -1
    }

    /** Compiles the shader.
     * @param type type of the shader (I.E.: vertex/fragment)
     * @param shaderCode the code of the shader
     * */
    private fun loadShader(type: Int, shaderCode: String): Int {
        return  GLES30.glCreateShader(type).also { shader ->
            GLES30.glShaderSource(shader, shaderCode)
            GLES30.glCompileShader(shader)
        }
    }

    /** Loads the shader's code from resources
     * @param vsResourceId vertex shader resource id
     * @param fsResourceId fragment shdaer resource id
     * */
    private fun loadShaderCode(vsResourceId: Int, fsResourceId: Int) {
        vertexShaderCode = loadString(vsResourceId)
        fragmentShaderCode = loadString(fsResourceId)
        loaded = true
    }

    /** Loads the string with the given resource id
     * @param resourceId id of the string to be loaded
     * */
    private fun loadString(resourceId: Int) : String {
        val inputStream = context.resources.openRawResource(resourceId)
        val reader = InputStreamReader(inputStream)
        val bufferedReader = BufferedReader(reader)

        val builder = StringBuilder()

        try {
            do {
                val line = bufferedReader.readLine()?.also{
                    builder.append(it)
                    builder.append("\n")
                }
            } while (line != null)

        } catch (ex: Exception) {
            Log.e("[Shader]", "Error reading string!")
            Log.e("[Shader]", ex.toString())
            ex.printStackTrace()
        }

        return builder.toString()
    }
}