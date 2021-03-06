package com.example.fall.data.persistent_data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "dead_players")
data class PlayerData(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,

    @Ignore var posX: Float,
    @Ignore var posY: Float,
    @Ignore var size: Float,
    @ColumnInfo(name = "resource") var modelResourceId: Int,
    @Ignore var currentState: PlayerStates,
    @Ignore var currentlyShooting: Boolean,
    @Ignore var health: Int,
    @Ignore var lookDirection: Float,
    @ColumnInfo(name = "levels") var nrOfLevelsReached: Int,
    @ColumnInfo(name = "score") var score: Int
) {
    /** Constructor where only the not ignored fields need to be set, so that Room can load data
     * into this class.
     * */
    constructor(
        modelResourceId: Int,
        nrOfLevelsReached: Int,
        score: Int
    ) : this(0,0f,0f,0f,modelResourceId, PlayerStates.Moving, false, 0, 0f, nrOfLevelsReached, score)

    /** Constructor without the need to set the id, which is only needed for database purposes.
     * */
    constructor(
        posX: Float,
        posY: Float,
        size: Float,
        modelResourceId: Int,
        currentState: PlayerStates,
        currentlyShooting: Boolean,
        health: Int,
        lookDirection: Float,
        nrOfLevelsReached: Int,
        score: Int
    ) : this (0, posX, posY, size, modelResourceId, currentState, currentlyShooting, health, lookDirection, nrOfLevelsReached, score)
}

enum class PlayerStates {
    Moving,
    Standing
}
