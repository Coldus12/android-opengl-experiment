package com.example.fall.data

data class MonsterData(
    var posX: Float,
    var posY: Float,
    var size: Float,
    var resourceId: Int,
    var currentState: MonsterStates,
    var lookDirection: Float,
    var health: Int,
    var dmg: Int
)

enum class MonsterStates {
    Standing,
    Moving,
    Attacking
}
