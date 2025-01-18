package com.example.dineDelight.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.example.dineDelight.models.Reservation
import com.example.dineDelight.models.Restaurant
import com.example.dineDelight.repositories.ReservationRepository
import com.example.dineDelight.repositories.RestaurantRepository
import com.example.dineDelight.views.BottomNavigationBar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val firebaseAuth = FirebaseAuth.getInstance()
    var currentUser by remember { mutableStateOf(firebaseAuth.currentUser) }
    var searchQuery by remember { mutableStateOf("") }
    val restaurants = RestaurantRepository.getRestaurants()

    if (currentUser !== null) {
        LaunchedEffect(currentUser?.uid) {
            ReservationRepository.getUserReservations(currentUser!!.uid)
        }
    }
    DisposableEffect(Unit) {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            currentUser = auth.currentUser
            if (auth.currentUser == null) {
                navController.navigate("login") {
                    popUpTo("home") { inclusive = true }
                }
            }
        }
        firebaseAuth.addAuthStateListener(authStateListener)
        onDispose {
            firebaseAuth.removeAuthStateListener(authStateListener)
        }
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("DineDelight") },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = { BottomNavigationBar(navController, "Home") }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (currentUser != null) {
                    Text(
                        text = "Hello, ${currentUser?.email ?: "User"}!",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { firebaseAuth.signOut() }) {
                        Text("Logout")
                    }
                }

                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search Restaurants") },
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                )

                LazyColumn {
                    items(restaurants.filter { it.name.contains(searchQuery, ignoreCase = true) }) { restaurant ->
                        RestaurantCard(restaurant) {
                            navController.navigate("restaurant/${restaurant.id}")
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun RestaurantCard(
    restaurant: Restaurant,
    onRestaurantClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onRestaurantClick)
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = restaurant.name,
                    style = MaterialTheme.typography.titleLarge
                )

                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = restaurant.rating.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                Text(
                    text = "Available slots: ${restaurant.availableSlots.joinToString(", ")}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Restaurant image on the right side with border radius
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                AsyncImage(
                    model = restaurant.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 0.dp)
                )
            }
        }
    }
}


@Composable
fun ReservationCard(reservation: Reservation) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Restaurant: ${reservation.restaurantName}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Time: ${reservation.time}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
