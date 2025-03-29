package com.example.dineDelight.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import com.example.dineDelight.database.AppDatabase
import com.example.dineDelight.models.Meal
import com.example.dineDelight.models.Restaurant
import com.example.dineDelight.models.RestaurantMenu
import com.example.dineDelight.utils.MealsApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import androidx.room.Room
import com.example.dineDelight.models.MealEntity

@Composable
fun RestaurantDetailsScreen(navController: NavController, restaurant: Restaurant) {
    var restaurantMenu by remember { mutableStateOf<RestaurantMenu?>(null) }
    var loading by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val db = remember {
        Room
            .databaseBuilder(context, AppDatabase::class.java, "dine_delight_db")
            .fallbackToDestructiveMigration()
            .build()
    }

    LaunchedEffect(restaurant) {
        loading = true
        val meals = db.mealDao().getMealsByRestaurant(restaurant.id.toString())
        if (meals.isEmpty()) {
            val fetchedMenu = fetchRestaurantMenu(restaurant)
            fetchedMenu?.let { menu ->
                val mealEntities = menu.meals.map { meal ->
                    MealEntity(
                        idMeal = meal.idMeal,
                        strMeal = meal.strMeal,
                        strMealThumb = meal.strMealThumb,
                        restaurantId = restaurant.id.toString()
                    )
                }
                db.mealDao().insertMeals(mealEntities)
                restaurantMenu = fetchedMenu
            }
        } else {
            restaurantMenu = RestaurantMenu( meals.map { meal -> Meal(meal.strMeal, meal.strMealThumb, meal.idMeal) })
        }
        loading = false
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        item {
            // Top Section: Restaurant Details
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }

            Text(text = "Restaurant Details", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Restaurant Name: ${restaurant.name}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Description: ${restaurant.description}", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { navController.navigate("reserve/${restaurant.id}") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Reserve a Slot", style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { navController.navigate("restaurant_reviews/${restaurant.id}") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Default.Star, contentDescription = "Leave a Review")
                Spacer(modifier = Modifier.width(8.dp))
                Text("View/Leave a Review", style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Loading or Menu Content Section
        if (loading) {
            item {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        } else {
            restaurantMenu?.let { menu ->
                items(menu.meals) { meal -> MealCard(meal) }
            } ?: item {
                Text(text = "No menu available.", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

private suspend fun fetchRestaurantMenu(restaurant: Restaurant): RestaurantMenu {
    val retrofit = Retrofit.Builder()
        .baseUrl(MealsApi.URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val mealService = retrofit.create(MealService::class.java)
    return mealService.getMealsByArea(restaurant.area)
}

interface MealService {
    @GET("filter.php")
    suspend fun getMealsByArea(@Query("a") area: String): RestaurantMenu
}

@Composable
fun MealCard(meal: Meal) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                Text(text = meal.strMeal, style = MaterialTheme.typography.titleMedium)
            }
            AsyncImage(
                model = meal.strMealThumb,
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .clip(MaterialTheme.shapes.medium)
            )
        }
    }
}
