package com.example.dineDelight.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.dineDelight.models.MealEntity

@Dao
interface MealDao {
    @Query("SELECT * FROM meals WHERE restaurantId = :restaurantId")
    suspend fun getMealsByRestaurant(restaurantId: String): List<MealEntity>

    @Query("SELECT * FROM meals")
    suspend fun getAllMeals(): List<MealEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeals(meals: List<MealEntity>)
}