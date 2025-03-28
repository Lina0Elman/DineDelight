package com.example.dineDelight.models

import java.util.UUID

data class Restaurant(
    val id: Int,
    val name: String,
    val description: String,
    val area: String,
    val rating: Float,
    val availableSlots: List<String>,
    val imageUrl: String
)
