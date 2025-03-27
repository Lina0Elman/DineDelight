package com.example.dineDelight.models

import com.google.firebase.firestore.DocumentId
import java.util.UUID
import android.util.Base64

data class Image(
    @DocumentId val id: String = UUID.randomUUID().toString(), // Firestore Document ID
    val blobBase64String: String? = null, // Store Base64 encoded string
)