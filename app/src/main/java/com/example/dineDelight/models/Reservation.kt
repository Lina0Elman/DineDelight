package com.example.dineDelight.models

import java.util.UUID

data class Reservation(
    val id: UUID,
    val restaurantId: UUID,
    val restaurantName: String,
    val time: String,
    val userId: String
)
