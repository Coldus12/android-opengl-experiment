package com.example.fall.data

import android.graphics.PointF

data class Playah(
    var position: PointF,
    var model: String,
    var currentState: PlayerStates,
    var health: Int,
    var lookDirection: Float
)

enum class PlayerStates {
    moving1,
    moving2,
    shooting1,
    shooting2,
    standing
}