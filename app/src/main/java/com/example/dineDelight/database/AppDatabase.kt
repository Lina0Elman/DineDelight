package com.example.dineDelight.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.dineDelight.models.ImageEntity
import com.example.dineDelight.models.RestaurantEntity

@Database(
    entities = [
        ImageEntity::class,
        RestaurantEntity::class
    ],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun restaurantDao(): RestaurantDao
    abstract fun imageDao(): ImageDao
}
