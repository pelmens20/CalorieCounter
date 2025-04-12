package com.example.caloriecounter

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calorie_data")
data class CalorieData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val caloriesPer100g: Float,
    val weightGrams: Float = 0f,
    val dateAdded: Long = System.currentTimeMillis()
)