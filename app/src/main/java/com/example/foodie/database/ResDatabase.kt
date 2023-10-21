package com.example.foodie.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Food_Entity::class], version = 1)
abstract  class ResDatabase :RoomDatabase() {
    abstract fun resDao():Res_Dao
}