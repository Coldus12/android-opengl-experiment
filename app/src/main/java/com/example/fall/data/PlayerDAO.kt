package com.example.fall.data

import androidx.room.*

@Dao
interface PlayerDAO {
    @Query("SELECT * FROM dead_players")
    fun getAll() : List<PlayerData>

    @Insert
    fun insert(data: PlayerData) : Long

    @Update
    fun update(data: PlayerData)

    @Delete
    fun deleteItem(data: PlayerData)
}