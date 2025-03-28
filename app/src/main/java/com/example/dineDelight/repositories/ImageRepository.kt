package com.example.dineDelight.repositories

import android.content.Context
import androidx.room.Room
import com.example.dineDelight.database.AppDatabase
import com.example.dineDelight.database.ImageDao
import com.example.dineDelight.models.Image
import com.example.dineDelight.utils.ImageUtils.toImage
import com.example.dineDelight.utils.ImageUtils.toImageEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await

object ImageRepository {
    private lateinit var imageDao: ImageDao
    private val firestore = FirebaseFirestore.getInstance()
    private val imagesCollection = firestore.collection("images")

    private val _images = MutableStateFlow<List<Image>>(emptyList())
    val images: StateFlow<List<Image>> get() = _images

    fun initialize(context: Context) {
        val db = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "dine_delight_db"
        )
            .fallbackToDestructiveMigration()
            .build()

        imageDao = db.imageDao()
    }

    suspend fun addImage(image: Image) {
        try {
            val imageEntity = image.toImageEntity()
            imageDao.insertImage(imageEntity)
            // Add image to Firestore
            imagesCollection.document(image.id).set(image).await()
            // Update local state flow after adding
            _images.value += image
        } catch (e: Exception) {
            // Handle any errors
            throw e
        }
    }

    suspend fun deleteImage(imageId: String) {
        try {
            imageDao.deleteImage(imageId)
            // Delete image from Firestore
            imagesCollection.document(imageId).delete().await()
            // Update local state flow after deleting
            _images.value = _images.value.filter { it.id != imageId }
        } catch (e: Exception) {
            // Handle any errors
            throw e
        }
    }

    suspend fun updateImage(updatedImage: Image) {
        try {
            val imageEntity = updatedImage.toImageEntity()
            imageDao.insertImage(imageEntity)
            // Update image in Firestore
            imagesCollection.document(updatedImage.id).set(updatedImage).await()
            // Update local state flow after updating
            _images.value = _images.value.map {
                if (it.id == updatedImage.id) updatedImage else it
            }
        } catch (e: Exception) {
            // Handle any errors
            throw e
        }
    }

    suspend fun getImageById(imageId: String): Image? {
        try {
            // Check Room first
            val imageEntity = imageDao.getImageById(imageId)
            if (imageEntity != null) {
                return imageEntity.toImage()
            }
            // If not found in Room, fetch from Firestore
            val documentSnapshot = imagesCollection.document(imageId).get().await()
            if (documentSnapshot.exists()) {
                val image = documentSnapshot.toObject(Image::class.java)
                image?.let {
                    val imageE = it.toImageEntity()
                    imageDao.insertImage(imageE)
                }
                return image
            } else {
                return null
            }
        } catch (e: Exception) {
            // Handle any errors
            throw e
        }
    }
}