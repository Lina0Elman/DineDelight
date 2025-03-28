package com.example.dineDelight.database

import androidx.room.*
import com.example.dineDelight.models.RestaurantEntity

@Dao
interface RestaurantDao {
    @Query("SELECT * FROM restaurants")
    suspend fun getAllRestaurants(): List<RestaurantEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(restaurants: List<RestaurantEntity>)

    @Query("SELECT * FROM restaurants WHERE id = :restaurantId LIMIT 1")
    suspend fun getRestaurantById(restaurantId: Int): RestaurantEntity?

    @Query("DELETE FROM restaurants")
    suspend fun deleteAll()
}
