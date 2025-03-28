package com.example.dineDelight.repositories

import android.content.Context
import androidx.room.Room
import com.example.dineDelight.database.AppDatabase
import com.example.dineDelight.models.Restaurant
import com.example.dineDelight.models.RestaurantEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object RestaurantRepository {
    // Room database reference
    private lateinit var db: AppDatabase


    // Must be called once to initialize the repository.
    fun initialize(context: Context) {
        db = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "dine_delight_db"
        )
            // If still in development, fallbackToDestructiveMigration is handy
            // so that schema changes just reset data instead of throwing an exception.
            .fallbackToDestructiveMigration()
            .build()
    }

    /**
     * Insert sample data into the database if it's empty.
     */
    suspend fun insertSampleDataIfEmpty() = withContext(Dispatchers.IO) {
        val dao = db.restaurantDao()
        val existing = dao.getAllRestaurants()
        if (existing.isEmpty()) {
            // Convert the static data to entities and insert
            val entities = sampleData.map { it.toEntity() }
            dao.insertAll(entities)
        }
    }

    /**
     * Fetch all restaurants from the local database.
     */
    suspend fun getRestaurants(): List<Restaurant> = withContext(Dispatchers.IO) {
        val dao = db.restaurantDao()
        val entities = dao.getAllRestaurants()
        entities.map { it.toModel() }
    }

    /**
     * Retrieve a single Restaurant by its ID.
     */
    suspend fun getRestaurantById(restaurantId: Int): Restaurant? = withContext(Dispatchers.IO) {
        val dao = db.restaurantDao()
        val entity = dao.getRestaurantById(restaurantId) ?: return@withContext null
        entity.toModel()
    }

    // Convert from your Restaurant model to a Room entity
    private fun Restaurant.toEntity(): RestaurantEntity {
        // Room won't store lists directly. Store as CSV.
        val slotsCsv = availableSlots.joinToString(",")
        return RestaurantEntity(
            id = this.id,
            name = this.name,
            description = this.description,
            area = this.area,
            rating = this.rating,
            availableSlots = slotsCsv,
            imageUrl = this.imageUrl
        )
    }

    // Convert from a Room entity back to your Restaurant model
    private fun RestaurantEntity.toModel(): Restaurant {
        val slotsList = this.availableSlots.split(",")
        return Restaurant(
            id = this.id,
            name = this.name,
            description = this.description,
            area = this.area,
            rating = this.rating,
            availableSlots = slotsList,
            imageUrl = this.imageUrl
        )
    }

    // ----------------------------------------------------------------------
    // Region SAMPLE DATA
    // (These are static entries)
    // ----------------------------------------------------------------------

    private val sampleData = listOf(
        Restaurant(
            id = 1,
            name = "Italian Delight",
            description = "Well known for our special tomato sauce",
            area = "Italian",
            rating = 4.5f,
            availableSlots = listOf("18:00", "19:00", "20:00"),
            imageUrl = "https://t3.ftcdn.net/jpg/06/07/90/92/360_F_607909283_b3ysd6mRICNihTjLwCENgTwP08Zb4koz.jpg"
        ),
        Restaurant(
            id = 2,
            name = "Sushi Paradise",
            description = "Eat the best sushi in town",
            area = "Japanese",
            rating = 4.8f,
            availableSlots = listOf("17:30", "18:30", "19:30"),
            imageUrl = "https://media.architecturaldigest.com/photos/572a34ffe50e09d42bdfb5e0/master/w_1920%2Cc_limit/japanese-restaurants-la-01.jpg"
        ),
        Restaurant(
            id = 3,
            name = "Canadian Bistro",
            description = "Taste the flavors of Canada",
            area = "Canadian",
            rating = 4.2f,
            availableSlots = listOf("12:00", "13:00", "14:00"),
            imageUrl = "https://media.cntraveler.com/photos/5b22cabaf0cc9956e5adca3c/16:9/w_1920%2Cc_limit/Bar-Raval_36361674480_70a3ef47c9_o.jpg"
        ),
        Restaurant(
            id = 4,
            name = "American Grill",
            description = "Enjoy classic American dishes",
            area = "American",
            rating = 4.6f,
            availableSlots = listOf("17:00", "18:00", "19:00"),
            imageUrl = "https://dynamic-media-cdn.tripadvisor.com/media/photo-o/11/5a/c0/04/diner-americano-in-stile.jpg"
        ),
        Restaurant(
            id = 5,
            name = "Mexican Fiesta",
            description = "Spice up your meal with Mexican cuisine",
            area = "Mexican",
            rating = 4.7f,
            availableSlots = listOf("16:00", "17:00", "18:00"),
            imageUrl = "https://margs.com/wp-content/uploads/2024/06/Margs-Woodbridge-Interior.jpg"
        ),
        Restaurant(
            id = 6,
            name = "French Elegance",
            description = "Experience the taste of France",
            area = "French",
            rating = 4.9f,
            availableSlots = listOf("19:00", "20:00", "21:00"),
            imageUrl = "https://thelagirl.com/wp-content/uploads/2017/07/French-Restaurants-in-Los-Angeles-1.jpg"
        ),
        Restaurant(
            id = 7,
            name = "Indian Spice",
            description = "Savor the rich flavors of India",
            area = "Indian",
            rating = 4.4f,
            availableSlots = listOf("18:30", "19:30", "20:30"),
            imageUrl = "https://images.axios.com/lIPBPJ802rSLL98aIQ9FDkQd2Go=/0x0:6687x3761/1920x1080/2023/04/11/1681223212336.jpg"
        ),
        Restaurant(
            id = 8,
            name = "Greek Taverna",
            description = "Enjoy traditional Greek dishes",
            area = "Greek",
            rating = 4.3f,
            availableSlots = listOf("12:30", "13:30", "14:30"),
            imageUrl = "https://www.greektastebeyondborders.com/wp-content/uploads/2020/10/oia-greek-taverna-2.jpg"
        ),
        Restaurant(
            id = 9,
            name = "Moroccan Delight",
            description = "Discover the flavors of Morocco",
            area = "Moroccan",
            rating = 4.5f,
            availableSlots = listOf("17:00", "18:00", "19:00"),
            imageUrl = "https://www.lesjardinsdelakoutoubia.com/en/img/slideshow_xxlarge/524_restaurant-marocain.jpeg"
        )
    )
}
