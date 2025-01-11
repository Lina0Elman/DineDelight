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
        ),
        Restaurant(
            id = UUID.randomUUID(),
            name = "Canadian Bistro",
            rating = 4.2f,
            availableSlots = listOf("12:00", "13:00", "14:00"),
            imageUrl = "https://example.com/canadian.jpg",
            area = "Canadian",
            description = "Taste the flavors of Canada"
        ),
        Restaurant(
            id = UUID.randomUUID(),
            name = "American Grill",
            rating = 4.6f,
            availableSlots = listOf("17:00", "18:00", "19:00"),
            imageUrl = "https://example.com/american.jpg",
            area = "American",
            description = "Enjoy classic American dishes"
        ),
        Restaurant(
            id = UUID.randomUUID(),
            name = "Mexican Fiesta",
            rating = 4.7f,
            availableSlots = listOf("16:00", "17:00", "18:00"),
            imageUrl = "https://example.com/mexican.jpg",
            area = "Mexican",
            description = "Spice up your meal with Mexican cuisine"
        ),
        Restaurant(
            id = UUID.randomUUID(),
            name = "French Elegance",
            rating = 4.9f,
            availableSlots = listOf("19:00", "20:00", "21:00"),
            imageUrl = "https://example.com/french.jpg",
            area = "French",
            description = "Experience the taste of France"
        ),
        Restaurant(
            id = UUID.randomUUID(),
            name = "Indian Spice",
            rating = 4.4f,
            availableSlots = listOf("18:30", "19:30", "20:30"),
            imageUrl = "https://example.com/indian.jpg",
            area = "Indian",
            description = "Savor the rich flavors of India"
        ),
        Restaurant(
            id = UUID.randomUUID(),
            name = "Greek Taverna",
            rating = 4.3f,
            availableSlots = listOf("12:30", "13:30", "14:30"),
            imageUrl = "https://example.com/greek.jpg",
            area = "Greek",
            description = "Enjoy traditional Greek dishes"
        ),
        Restaurant(
            id = UUID.randomUUID(),
            name = "Moroccan Delight",
            rating = 4.5f,
            availableSlots = listOf("17:00", "18:00", "19:00"),
            imageUrl = "https://example.com/moroccan.jpg",
            area = "Moroccan",
            description = "Discover the flavors of Morocco"
        )
    )

    fun getRestaurants(): List<Restaurant> {
        return restaurants
    }
}