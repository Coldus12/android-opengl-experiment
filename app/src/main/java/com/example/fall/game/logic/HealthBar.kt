package com.example.fall.game.logic

import android.content.Context
import com.example.fall.R
import com.example.fall.game.graphics.opengl.Shader
import com.example.fall.game.math.Vec4

class HealthBar(private var context: Context) {
    private var missing: Float = 0F
    private val ySize: Float = 0.15F

    private var redPart =
        floatArrayOf(
            -1f, 1f,
            1f, 1f,
            1f, 1f - ySize,
            -1f, 1f - ySize
        )

    private var greenPart =
        floatArrayOf(
            -1f, 1f,
            1f - 2*missing, 1f,
            1f - 2*missing, 1f - ySize,
            -1f, 1f - ySize
        )

    private val red = floatArrayOf(1f, 0.2f, 0.2f, 1f)
    private val green = floatArrayOf(0.2f, 1f, 0.2f, 1f)

    private val redV = Vec4(red)
    private val greenV = Vec4(green)

    private var redShader = Shader(context, R.raw.healthbar_vertex_shader, R.raw.healthbar_fragment_shader, redPart, 2, "vPosition")
    private var greenShader = Shader(context, R.raw.healthbar_vertex_shader, R.raw.healthbar_fragment_shader, greenPart, 2, "vPosition")

    fun updateHealth(missingHPpercent: Float) {
        missing = if (missingHPpercent < 1f)
            missingHPpercent
        else 1f

        greenPart =
            floatArrayOf(
                -1f, 1f,
                1f - 2*missing, 1f,
                1f - 2*missing, 1f - ySize,
                -1f, 1f - ySize
            )

        greenShader.updateGeometry(greenPart, 2)
    }

    fun draw() {
        redShader.useProgram()
        redShader.setUniformVec(redV, "vColor")
        redShader.drawGeometry()

        greenShader.useProgram()
        greenShader.setUniformVec(greenV, "vColor")
        greenShader.drawGeometry()
    }
}