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
            imageUrl = "https://t3.ftcdn.net/jpg/06/07/90/92/360_F_607909283_b3ysd6mRICNihTjLwCENgTwP08Zb4koz.jpg",
            area = "Italian",
            description = "Well known for our special tomato sauce"
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
            imageUrl = "https://media.cntraveler.com/photos/5b22cabaf0cc9956e5adca3c/16:9/w_1920%2Cc_limit/Bar-Raval_36361674480_70a3ef47c9_o.jpg",
            area = "Canadian",
            description = "Taste the flavors of Canada"
        ),
        Restaurant(
            id = UUID.randomUUID(),
            name = "American Grill",
            rating = 4.6f,
            availableSlots = listOf("17:00", "18:00", "19:00"),
            imageUrl = "https://dynamic-media-cdn.tripadvisor.com/media/photo-o/11/5a/c0/04/diner-americano-in-stile.jpg",
            area = "American",
            description = "Enjoy classic American dishes"
        ),
        Restaurant(
            id = UUID.randomUUID(),
            name = "Mexican Fiesta",
            rating = 4.7f,
            availableSlots = listOf("16:00", "17:00", "18:00"),
            imageUrl = "https://margs.com/wp-content/uploads/2024/06/Margs-Woodbridge-Interior.jpg",
            area = "Mexican",
            description = "Spice up your meal with Mexican cuisine"
        ),
        Restaurant(
            id = UUID.randomUUID(),
            name = "French Elegance",
            rating = 4.9f,
            availableSlots = listOf("19:00", "20:00", "21:00"),
            imageUrl = "https://thelagirl.com/wp-content/uploads/2017/07/French-Restaurants-in-Los-Angeles-1.jpg",
            area = "French",
            description = "Experience the taste of France"
        ),
        Restaurant(
            id = UUID.randomUUID(),
            name = "Indian Spice",
            rating = 4.4f,
            availableSlots = listOf("18:30", "19:30", "20:30"),
            imageUrl = "https://images.axios.com/lIPBPJ802rSLL98aIQ9FDkQd2Go=/0x0:6687x3761/1920x1080/2023/04/11/1681223212336.jpg",
            area = "Indian",
            description = "Savor the rich flavors of India"
        ),
        Restaurant(
            id = UUID.randomUUID(),
            name = "Greek Taverna",
            rating = 4.3f,
            availableSlots = listOf("12:30", "13:30", "14:30"),
            imageUrl = "https://www.greektastebeyondborders.com/wp-content/uploads/2020/10/oia-greek-taverna-2.jpg",
            area = "Greek",
            description = "Enjoy traditional Greek dishes"
        ),
        Restaurant(
            id = UUID.randomUUID(),
            name = "Moroccan Delight",
            rating = 4.5f,
            availableSlots = listOf("17:00", "18:00", "19:00"),
            imageUrl = "https://www.lesjardinsdelakoutoubia.com/en/img/slideshow_xxlarge/524_restaurant-marocain.jpeg",
            area = "Moroccan",
            description = "Discover the flavors of Morocco"
        )
    )

    fun getRestaurants(): List<Restaurant> {
        return restaurants
    }
}