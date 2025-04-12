package com.example.caloriecounter

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CalorieData::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun calorieDao(): CalorieDao
}