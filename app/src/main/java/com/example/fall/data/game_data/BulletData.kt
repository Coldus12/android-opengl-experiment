package com.example.fall.data.game_data

data class BulletData(
    var posX: Float,
    var posY: Float,
    var direction: Float,
    val size: Float,
    val speed: Float,
    val dmg: Int,
    val texture: BulletTextures,
    var exists: Boolean
)

enum class BulletTextures {
    Standard,
    Pellet
}
