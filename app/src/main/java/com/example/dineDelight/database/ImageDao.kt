package com.example.dineDelight.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.dineDelight.models.ImageEntity

@Dao
interface ImageDao {
    @Insert
    suspend fun insertImage(image: ImageEntity)

    @Query("SELECT * FROM images WHERE id = :id")
    suspend fun getImageById(id: String): ImageEntity?
}