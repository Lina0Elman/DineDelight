package com.example.dineDelight.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.dineDelight.models.ImageEntity

@Dao
interface ImageDao {
    @Query("SELECT * FROM images WHERE id = :imageId")
    suspend fun getImageById(imageId: String): ImageEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: ImageEntity)

    @Query("DELETE FROM images WHERE id = :imageId")
    suspend fun deleteImage(imageId: String)
}