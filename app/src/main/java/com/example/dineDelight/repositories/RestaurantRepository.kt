package com.example.dineDelight.repositories

import com.example.dineDelight.models.Restaurant
import java.util.UUID

object RestaurantRepository {
    private val restaurants: MutableList<Restaurant> = mutableListOf(
        Restaurant(
            id = UUID.randomUUID(),
            name = "Italian Delight",
            rating = 4.5f,
            availableSlots = listOf("18:00", "19:00", "20:00"),
            imageUrl = "https://example.com/italian.jpg",
            area = "Italian",
            description = "Eat the best pasta in town"
        ),
        Restaurant(
            id = UUID.randomUUID(),
            name = "Sushi Paradise",
            rating = 4.8f,
            availableSlots = listOf("17:30", "18:30", "19:30"),
            imageUrl = "https://example.com/sushi.jpg",
            area = "Japanese",
            description = "Eat the best sushi in town"
        )
    )

    fun getRestaurants(): List<Restaurant> {
        return restaurants
    }
}