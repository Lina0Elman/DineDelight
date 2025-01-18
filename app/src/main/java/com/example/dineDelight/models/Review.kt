package com.example.dineDelight.models

import com.google.firebase.firestore.DocumentId
import java.util.UUID

data class Review(
    @DocumentId val id: String = UUID.randomUUID().toString(), // Firestore Document ID
    val userId: String = "",
    val userEmail: String = "",
    val restaurantId: Int = 0,
    val restaurantName: String = "",
    val text: String = ""
)