package com.example.dineDelight.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "restaurants")
data class RestaurantEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val description: String,
    val area: String,
    val rating: Float,
    val availableSlots: String,
    val imageUrl: String
)
