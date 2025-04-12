package com.example.caloriecounter

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CalorieDao {
    @Insert
    suspend fun insert(calorieData: CalorieData)

    @Query("SELECT * FROM calorie_data ORDER BY dateAdded DESC")
    fun getAll(): Flow<List<CalorieData>>

    @Query("SELECT SUM(caloriesPer100g * weightGrams / 100) FROM calorie_data")
    fun getTotalCalories(): Flow<Float>
}