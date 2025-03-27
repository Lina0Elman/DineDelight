package com.example.dineDelight.repositories

import android.content.Context
import android.net.Uri
import androidx.room.Room
import com.example.dineDelight.models.Review
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID
import com.example.dineDelight.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.dineDelight.models.ImageEntity

object ReviewRepository {
    private val db = FirebaseFirestore.getInstance()
    private val reviewsCollection = db.collection("reviews")

    private lateinit var imagesDB: AppDatabase

    fun initialize(context: Context) {
        imagesDB = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java, "dine_delight_db"
        ).build()
    }

    suspend fun addReview(review: Review) {
        try {
            reviewsCollection.document(review.id).set(review).await()
        } catch (e: Exception) {
            // Log the error or handle it as needed
            throw e
        }
    }

    suspend fun getUserReviews(userId: String): List<Review> {
        return reviewsCollection.whereEqualTo("userId", userId).get().await()
            .toObjects(Review::class.java)
    }

    suspend fun getRestaurantReviews(restaurantId: Int): List<Review> {
        return reviewsCollection
            .whereEqualTo("restaurantId", restaurantId)
            .get()
            .await()
            .toObjects(Review::class.java)
    }

    suspend fun deleteReview(reviewId: String) {
        try {
            reviewsCollection.document(reviewId).delete().await()
        } catch (e: Exception) {
            // Log the error or handle it as needed
            throw e
        }
    }

    suspend fun getReviewById(reviewId: String): Review? {
        return try {
            val documentSnapshot =
                reviewsCollection.document(reviewId).get().await()
            if (documentSnapshot.exists()) {
                documentSnapshot.toObject(Review::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            // Log the error or handle it as needed
            throw e
        }
    }

    suspend fun updateReview(updatedReview: Review) {
        try {
            reviewsCollection.document(updatedReview.id).set(updatedReview)
                .await()
        } catch (e: Exception) {
            // Log or handle the error as needed
            throw e
        }
    }
}