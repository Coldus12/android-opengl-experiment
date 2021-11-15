package com.example.fall.data

data class Block(
    var posX: Float,
    var posY: Float,
    var blockSize: Float,
    var type: BlockTextureTypes,
    var passable: Boolean
)

enum class BlockTextureTypes {
    Wall1,
    Wall2,
    Wall3,
    Floor1,
    Floor2,
    Floor3
}