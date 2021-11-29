package com.example.fall.game

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import com.example.fall.game.logic.IGraphicalGame

// A surface view made to be used with OpenGL
//--------------------------------------------------------------------------------------------------
/** A surface view made to be used with OpenGL
 * */
class MyGLSurfaceView(context: Context, attrs: AttributeSet) : GLSurfaceView(context,attrs) {
    private val renderer: MyGLRenderer

    init {
        setEGLContextClientVersion(3)

        renderer = MyGLRenderer()
        setRenderer(renderer)
        renderMode = RENDERMODE_WHEN_DIRTY
    }

    /** Sets the game interface of the renderer.
     *  All the rendering calls must be made inside the onDraw function of the renderer,
     *  and this way if a game implements this interface the renderer may call the appropiate
     *  render functions of the game when necessary.
     *  @param game The game which implements the interface
     * */
    fun setGraphicalGameInterface(game: IGraphicalGame) {
        renderer.setGraphicalGameInterface(game)
    }

    fun getEglContInitialized(): Boolean {
        return renderer.getContextInitialized()
    }
}