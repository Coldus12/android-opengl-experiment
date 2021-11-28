package com.example.fall.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PlayerData::class], version = 1)
abstract class DeadPlayersDB : RoomDatabase() {
    abstract fun playerDao() : PlayerDAO

    companion object {
        fun getDatabase(applicationContext: Context): DeadPlayersDB {
            return Room.databaseBuilder(
                applicationContext,
                DeadPlayersDB::class.java,
                "dead_players"
            ).build();
        }
    }
}