package com.example.fall.logic

import android.content.Context
import com.example.fall.R
import com.example.fall.data.MonsterData
import com.example.fall.data.MonsterStates
import com.example.fall.graphics.Animation
import com.example.fall.math.Mat4
import com.example.fall.math.Vec4

class MeleeMonster(private var context: Context, posX: Float, posY: Float, lookDirection: Float) : Monster(context) {
    init {
        data = MonsterData(
            posX,
            posY,
            1f,
            R.drawable.cockroach,
            MonsterStates.Standing,
            lookDirection,
            20,
            20
        )

        loadShader()
        loadAnimation()
    }

    //
    //----------------------------------------------------------------------------------------------
    private lateinit var animation: Animation
    private fun loadAnimation() {
        animation = Animation(
            context,
            data.resourceId,
            1,
            4,
            64,
            64
        )

        animation.setTimes(2)
    }

    override fun attack(player: Player) {
        TODO("Not yet implemented")
    }

    override fun move(game: Game, dx: Float, dy: Float) {
        TODO("Not yet implemented")
    }

    override fun draw() {
        shader.useProgram()

        when (data.currentState) {
            MonsterStates.Standing -> animation.setFrame(0)
            MonsterStates.Moving -> animation.setCurrentFrame()
            else -> animation.setFrame(0)
        }

        val r = Mat4.rotMat(data.lookDirection)
        val t = Mat4.translateMat(Vec4(floatArrayOf(data.posX, data.posY, 0f, 1f)))
        val s = Mat4.scaleMat(Vec4(floatArrayOf(data.size, data.size, 0f, 1f)))

        val sr = s.multiplyBy(r)
        val m = sr.multiplyBy(t)

        val vp = v.multiplyBy(p)
        val mvp = m.multiplyBy(vp)

        shader.setUniformMat(mvp, "MVPMatrix")
        shader.drawGeometry()
    }

    override fun update(timeInMs: Long) {
        TODO("Not yet implemented")
    }
}