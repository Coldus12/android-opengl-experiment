package com.example.fall.data

data class PlayerData(
    var posX: Float,
    var posY: Float,
    var size: Float,
    var modelResourceId: Int,
    var currentState: PlayerStates,
    var currentlyShooting: Boolean,
    var health: Int,
    var lookDirection: Float
)

enum class PlayerStates {
    Moving,
    Standing
}
