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
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.dineDelight.utils.BlobUtils.toBase64String
import com.example.dineDelight.utils.BlobUtils.toBlob
import java.io.ByteArrayOutputStream

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

    suspend fun addImage(image: Image): Image {
        try {
            // Compress the image before adding
            val compressedImage = compressImage(image)
            val imageEntity = compressedImage.toImageEntity()
            imageDao.insertImage(imageEntity)
            // Add image to Firestore
            imagesCollection.document(compressedImage.id).set(compressedImage).await()
            // Update local state flow after adding
            _images.value += compressedImage

            return compressedImage
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

    private fun compressImage(image: Image): Image {
        val maxSize = 1048487 // 1 MB in bytes
        var compressedImage = image // Start with the original image

        // Check the size of the image and compress if necessary
        while (getImageSize(compressedImage) > maxSize) {
            // Reduce the quality of the image
            compressedImage = reduceImageQuality(compressedImage)
        }

        return compressedImage // Return the compressed image
    }

    private fun getImageSize(image: Image): Int {
        // Convert the image to a byte array and return its size
        val byteArrayOutputStream = ByteArrayOutputStream()
        val blob = image.blobBase64String!!.toBlob()
        val bitmap = BitmapFactory.decodeByteArray(blob, 0, blob!!.size)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream) // Compress to JPEG format
        return byteArrayOutputStream.size() // Return the size in bytes
    }

    private fun reduceImageQuality(image: Image): Image {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val blob = image.blobBase64String!!.toBlob()
        val bitmap = BitmapFactory.decodeByteArray(blob, 0, blob!!.size)

        // Start with a quality of 100 and reduce until the size is acceptable
        var quality = 100
        do {
            byteArrayOutputStream.reset() // Clear the output stream
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
            quality -= 10 // Reduce quality by 10 each iteration
        } while (getImageSize(image) > 1048487 && quality > 0)

        // Create a new Image object from the compressed byte array
        val compressedImageBytes = byteArrayOutputStream.toByteArray()
        return Image(id = image.id, blobBase64String = compressedImageBytes.toBase64String())
    }
}