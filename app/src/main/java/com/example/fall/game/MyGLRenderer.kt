package com.example.fall.game

import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.util.Log
import com.example.fall.game.logic.IGraphicalGame
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

// OpenGL renderer
//--------------------------------------------------------------------------------------------------
/** OpenGL renderer extended to render the game.
 * */
class MyGLRenderer() : GLSurfaceView.Renderer {

    private var game: IGraphicalGame? = null
    private var eglContextInitialized = false

    private var width: Int = -1
    private var height: Int = -1

    /** Called once the surface is created, and it sets the initial values
     * (such as the "background color")
     * */
    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        GLES30.glClearColor(0.5f, 0.3f, 1.0f, 1.0f)
        game?.render()
        eglContextInitialized = true
    }

    /** Gets called everytime the surfaceView gets a "requestRender" (if the rendermode is
     * set to RENDER_WHEN_DIRTY) or everytime something changes (if the rendermode is set to
     * RENDER_CONTINOUSLY)
     *
     * Clears the screen and rerenders the game
     * */
    override fun onDrawFrame(unused: GL10) {
        // Redraw background color
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
        game?.render()
    }

    /** Changes the cameras screenRatio so everything is drawn correctly.
     * */
    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)

        this.width = width
        this.height = height

        game?.setCameraSize(width.toFloat(), height.toFloat())
        game?.render()
    }

    /** OpenGL calls should only be made after the application has an OpenGL context.
     * This function helps determine whether or not the renderer initialized, and if the application
     * has an OpenGL context.
     * @return Does the application have an OpenGL context
     * */
    fun getContextInitialized(): Boolean {
        return eglContextInitialized
    }

    fun setGraphicalGameInterface(game: IGraphicalGame) {
        this.game = game
        game.setCameraSize(width.toFloat(), height.toFloat())
        game.render()
        Log.i("[LOOG]","$width and $height")
    }
}