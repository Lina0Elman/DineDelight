package com.example.dineDelight.repositories

import com.example.dineDelight.models.Review
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object ReviewRepository {
    private val db = FirebaseFirestore.getInstance()
    private val reviewsCollection = db.collection("reviews")

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