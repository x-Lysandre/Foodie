package com.example.foodie.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import java.util.concurrent.Flow

@Dao
interface Res_Dao {

    @Insert
    fun insert_Res(Food_Entity:Food_Entity)

    @Delete
    fun delete_Res(Food_Entity: Food_Entity)

    @Query("SELECT * FROM Food")
    fun getAllRes(): List<Food_Entity>

    @Query("SELECT * FROM Food WHERE Res_id = :ResId ")
    fun getResById( ResId : String): Food_Entity
}