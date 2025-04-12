package com.example.caloriecounter

object FoodDatabase {
    fun loadFoodList(): List<FoodItem> {
        return listOf(
            FoodItem("Хлеб", 250f),
            FoodItem("Мясо", 300f),
            FoodItem("Мёд", 320f)
            // Добавьте другие
        )
    }
}

data class FoodItem(val name: String, val caloriesPer100g: Float)