package com.example.fall.game.logic

// IGraphicalGame interface
//--------------------------------------------------------------------------------------------------
/** An interface through which the OpenGL renderer may communicate with the game.
 * */
interface IGraphicalGame {
    /** Sets the screen size.
     * This is required for every OpenGL draw call, so that everything appears correctly.
     * @param width screen width
     * @param height screen height
     * */
    fun setCameraSize(width: Float, height: Float)

    /** The render function, through which the glRenderer asks the game to make its OpenGL calls.
     * */
    fun render()
}