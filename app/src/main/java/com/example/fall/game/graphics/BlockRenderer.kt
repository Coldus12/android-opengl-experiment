package com.example.fall.game.graphics

import android.content.Context
import com.example.fall.game.graphics.opengl.Texture
import com.example.fall.R
import com.example.fall.data.game_data.Block
import com.example.fall.data.game_data.BlockTextureTypes
import com.example.fall.game.math.Mat4
import com.example.fall.game.math.Vec4
import com.example.fall.game.graphics.opengl.Shader
import kotlin.math.PI

// BulletRenderer
//--------------------------------------------------------------------------------------------------
/** The class responsible for rendering the blocks of the map
 * @param context context - required for loading texture resources
 * */
class BlockRenderer(private var context: Context) {
    private lateinit var wall1: Texture
    private lateinit var wall2: Texture
    private lateinit var wall3: Texture
    private lateinit var floor1: Texture
    private lateinit var floor2: Texture
    private lateinit var floor3: Texture

    private fun loadTextures() {
        wall1 = Texture(context, R.drawable.wall1)
        wall2 = Texture(context, R.drawable.wall2)
        wall3 = Texture(context, R.drawable.wall3)
        floor1 = Texture(context, R.drawable.floor1)
        floor2 = Texture(context, R.drawable.floor2)
        floor3 = Texture(context, R.drawable.floor3)
    }

    private lateinit var p: Mat4
    private lateinit var v: Mat4

    private val blockGeometry =
        floatArrayOf(
            -0.5f, 0.5f,
            0.5f, 0.5f,
            0.5f, -0.5f,
            -0.5f, -0.5f
        )

    private var shader = Shader(context, R.raw.block_vertex_shader, R.raw.block_fragment_shader, blockGeometry, 2, "vPosition")

    fun setViewProj(v: Mat4, p: Mat4) {
        this.v = v
        this.p = p
    }

    init {
        loadTextures()
    }

    fun draw(data: Block) {
        shader.useProgram()

        when (data.type) {
            BlockTextureTypes.Wall1 -> wall1.setTexture()
            BlockTextureTypes.Wall2 -> wall2.setTexture()
            BlockTextureTypes.Wall3 -> wall3.setTexture()
            BlockTextureTypes.Floor1 -> floor1.setTexture()
            BlockTextureTypes.Floor2 -> floor2.setTexture()
            BlockTextureTypes.Floor3 -> floor3.setTexture()
            else -> {floor1.setTexture()}
        }

        val r = Mat4.rotMat(PI.toFloat())
        val t = Mat4.translateMat(Vec4(floatArrayOf(data.posX, data.posY, 0f, 1f)))
        val s = Mat4.scaleMat(Vec4(floatArrayOf(data.blockSize, data.blockSize, 0f, 1f)))

        val sr = s.multiplyBy(r)
        val m = sr.multiplyBy(t)

        val vp = v.multiplyBy(p)
        val mvp = m.multiplyBy(vp)

        shader.setUniformMat(mvp, "MVPMatrix")
        shader.drawGeometry()
    }
}