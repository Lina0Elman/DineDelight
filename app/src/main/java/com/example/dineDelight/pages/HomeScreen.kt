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
import com.example.dineDelight.models.Review
import com.example.dineDelight.repositories.ReservationRepository
import com.example.dineDelight.repositories.RestaurantRepository
import com.example.dineDelight.repositories.ReviewRepository
import com.example.dineDelight.repositories.ReviewRepository.addReview
import com.example.dineDelight.views.BottomNavigationBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID


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
                    Spacer(modifier = Modifier.height(16.dp))
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
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onRestaurantClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = restaurant.name,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = restaurant.description,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Rating: ${restaurant.rating}",
                style = MaterialTheme.typography.bodySmall
            )
            AsyncImage(
                model = restaurant.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )
        }
    }
}