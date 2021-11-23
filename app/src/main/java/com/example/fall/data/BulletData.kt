package com.example.fall.data

data class BulletData(
    var posX: Float,
    var posY: Float,
    val direction: Float,
    val size: Float,
    val speed: Float,
    val texture: BulletTextures,
    var exists: Boolean
)

enum class BulletTextures {
    standard,
    pellet
}
