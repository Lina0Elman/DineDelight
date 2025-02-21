package com.example.dineDelight.repositories

import android.content.Context
import android.net.Uri
import androidx.room.Room
import com.example.dineDelight.database.AppDatabase
import com.example.dineDelight.models.ImageEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

object ImageRepository {
    private lateinit var imagesDB: AppDatabase

    fun initialize(context: Context) {
        imagesDB = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java, "dine_delight_db"
        ).build()
    }

    suspend fun saveImageToLocalDatabase(uri: Uri): String {
        return withContext(Dispatchers.IO) {
            val imageId = UUID.randomUUID().toString()
            val imageEntity = ImageEntity(id = imageId, uri = uri.toString())
            imagesDB.imageDao().insertImage(imageEntity)
            imageId
        }
    }

    suspend fun getImageUriById(imageId: String): Uri? {
        return withContext(Dispatchers.IO) {
            imagesDB.imageDao().getImageById(imageId)?.let {
                Uri.parse(it.uri)
            }
        }
    }
}