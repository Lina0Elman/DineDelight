package com.example.dineDelight.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.dineDelight.models.ImageEntity

@Database(entities = [ImageEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun imageDao(): ImageDao
}