package com.example.dineDelight.models

import java.util.UUID

data class Review(
    val id: UUID = UUID.randomUUID(),
    val userId: String = "",
    val userName: String = "",
    val restaurantId: UUID = UUID.randomUUID(),
    val restaurantName: String = "",
    val text: String = ""
)