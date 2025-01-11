package com.example.dineDelight.pages

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import coil.compose.AsyncImage
import com.example.dineDelight.models.Meal
import com.example.dineDelight.models.Restaurant
import com.example.dineDelight.models.RestaurantMenu
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

@Composable
fun RestaurantDetailsScreen(navController: NavController, restaurant: Restaurant) {
    var restaurantMenu by remember { mutableStateOf<RestaurantMenu?>(null) }
    var loading by remember { mutableStateOf(true) }

    // Use LaunchedEffect to perform the network call
    LaunchedEffect(restaurant) {
        loading = true
        restaurantMenu = fetchRestaurantMenu(restaurant)
        loading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        // Back button
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }

        Text(text = "Restaurant Details", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Restaurant Name: ${restaurant.name}")
        Text(text = "Description: ${restaurant.description}")
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { navController.navigate("reserve/${restaurant.id}") }, // Navigate to reservation screen
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Reserve a Slot")
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (loading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            restaurantMenu?.let { menu ->
                LazyColumn {
                    items(menu.meals) { meal ->
                        MealCard(meal)
                    }
                }
            } ?: Text(text = "No menu available.")
        }
    }
}

private suspend fun fetchRestaurantMenu(restaurant: Restaurant): RestaurantMenu {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://www.themealdb.com/api/json/v1/1/")
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
                Text(text = meal.strMeal, style = MaterialTheme.typography.titleLarge)
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
