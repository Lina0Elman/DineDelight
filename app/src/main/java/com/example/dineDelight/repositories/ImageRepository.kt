package com.example.dineDelight.repositories

import com.example.dineDelight.models.Image
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await

object ImageRepository {
    private val db = FirebaseFirestore.getInstance()
    private val imagesCollection = db.collection("images")

    private val _images = MutableStateFlow<List<Image>>(emptyList())
    val images: StateFlow<List<Image>> get() = _images

    suspend fun addImage(image: Image) {
        try {
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
        return try {
            // Retrieve image by ID from Firestore
            val documentSnapshot = imagesCollection.document(imageId).get().await()
            if (documentSnapshot.exists()) {
                documentSnapshot.toObject(Image::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            // Handle any errors
            throw e
        }
    }
}