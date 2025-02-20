package com.example.dineDelight.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "images")
data class ImageEntity(
    @PrimaryKey val id: String,
    val uri: String
)