package com.example.fall.game.logic

// Updateable interface
//--------------------------------------------------------------------------------------------------
/** An interface with which anything that needs to be updated with time (I.E.: movement,
 *  animation, etc) can be updated.
 * */
interface Updateable {
    fun update(timeInMs: Long)
}