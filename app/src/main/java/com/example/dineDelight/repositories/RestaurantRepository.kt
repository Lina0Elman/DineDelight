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
            imageUrl = "https://s6.ezgif.com/tmp/ezgif-6-6ddeaf195d.jpg",
            area = "Italian",
            description = "Eat the best pasta in town"
        ),
        Restaurant(
            id = UUID.randomUUID(),
            name = "Sushi Paradise",
            rating = 4.8f,
            availableSlots = listOf("17:30", "18:30", "19:30"),
            imageUrl = "https://media.architecturaldigest.com/photos/572a34ffe50e09d42bdfb5e0/master/w_1920%2Cc_limit/japanese-restaurants-la-01.jpg",
            area = "Japanese",
            description = "Eat the best sushi in town"
        )
    )

    fun getRestaurants(): List<Restaurant> {
        return restaurants
    }
}