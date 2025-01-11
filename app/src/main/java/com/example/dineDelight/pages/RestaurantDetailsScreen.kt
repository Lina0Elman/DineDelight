package com.example.dineDelight.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack

@Composable
fun RestaurantDetailsScreen(navController: NavController, restaurantId: String) {
    val restaurantDetails = remember { fetchRestaurantMenu(restaurantId) }

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
        Text(text = "Restaurant Name: ${restaurantDetails.name}")
        Text(text = "Description: ${restaurantDetails.description}")
    }
}

private fun fetchRestaurantMenu(restaurantId: String): RestaurantDetails {
    return RestaurantDetails(name = "Sample Restaurant", description = "This is a sample description.")
}

data class RestaurantDetails(val name: String, val description: String)