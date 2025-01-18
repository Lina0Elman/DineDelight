package com.example.dineDelight.repositories

import com.example.dineDelight.models.Review
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID

object ReviewRepository {
    private val db = FirebaseFirestore.getInstance()
    private val reviewsCollection = db.collection("reviews")

    suspend fun addReview(review: Review) {
        try {
            reviewsCollection.document(review.id.toString()).set(review).await()
        } catch (e: Exception) {
            // Log the error or handle it as needed
            throw e
        }
    }

    suspend fun getUserReviews(userId: String): List<Review> {
        return reviewsCollection.whereEqualTo("userId", userId).get().await().toObjects(Review::class.java)
    }

    suspend fun getRestaurantReviews(restaurantId: UUID): List<Review> {
        return reviewsCollection
            .whereEqualTo("restaurantId", restaurantId)
            .get()
            .await()
            .toObjects(Review::class.java)
    }
}