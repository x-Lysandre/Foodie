package com.example.foodie.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Food")
data class Food_Entity (
    @PrimaryKey val Res_id:Int,
    @ColumnInfo(name = "Res_Name") val Res_Name:String,
    @ColumnInfo(name = "Res_Rating") val Res_Rating:String,
    @ColumnInfo(name = "Res_cost_for_one") val Res_cost_for_one:String,
    @ColumnInfo(name = "Res_Image") val Res_Image:String
        )