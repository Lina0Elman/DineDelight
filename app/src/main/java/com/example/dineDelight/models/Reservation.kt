package com.example.dineDelight.models

import com.google.firebase.firestore.DocumentId
import java.util.UUID

data class Reservation(
    @DocumentId val id: String = UUID.randomUUID().toString(), // Firestore Document ID
    val restaurantId: Int = 0,
    val restaurantName: String = "",
    val time: String = "",
    val userId: String = ""
)