package com.example.dineDelight.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meals")
data class MealEntity(
    @PrimaryKey val idMeal: String,
    val strMeal: String,
    val strMealThumb: String,
    val restaurantId: String
)