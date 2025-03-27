package com.example.dineDelight.models

import com.google.firebase.firestore.DocumentId
import java.util.UUID

data class Image(
    @DocumentId val id: String = UUID.randomUUID().toString(), // Firestore Document ID
    val blob: String = "",
)