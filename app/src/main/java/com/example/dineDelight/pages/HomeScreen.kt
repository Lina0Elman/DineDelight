package com.example.dineDelight.pages

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.dineDelight.R
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.lazy.items

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val firebaseAuth = FirebaseAuth.getInstance()
    var currentUser by remember { mutableStateOf(firebaseAuth.currentUser) }
    var searchQuery by remember { mutableStateOf("") }
    
    // Mock restaurant data - In a real app, this would come from the REST API
    val restaurants = remember {
        listOf(
            Restaurant(
                id = "1",
                name = "Italian Delight",
                rating = 4.5f,
                availableSlots = listOf("18:00", "19:00", "20:00"),
                imageUrl = "https://example.com/italian.jpg"
            ),
            Restaurant(
                id = "2",
                name = "Sushi Paradise",
                rating = 4.8f,
                availableSlots = listOf("17:30", "18:30", "19:30"),
                imageUrl = "https://example.com/sushi.jpg"
            )
        )
    }

    // Firebase Authentication state listener
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
        bottomBar = {
            NavigationBar {
                listOf(
                    NavigationItem("Home", Icons.Default.Home),
                    NavigationItem("Reservations", Icons.Default.DateRange),
                    NavigationItem("Profile", Icons.Default.Person)
                ).forEach { item ->
                    NavigationBarItem(
                        selected = when (item.title) {
                            "Home" -> true
                            else -> false
                        },
                        onClick = {
                            when (item.title) {
                                "Home" -> navController.navigate("home")
                                "Reservations" -> navController.navigate("reservations")
                                "Profile" -> navController.navigate("profile")
                            }
                        },
                        icon = { Icon(item.icon, item.title) },
                        label = { Text(item.title) }
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (currentUser != null) {
                    // Show a personalized welcome message if the user is logged in
                    Text(
                        text = "Welcome back, ${currentUser?.email ?: "User"}!",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { firebaseAuth.signOut() }) {
                        Text("Sign Out")
                    }
                }

                // Search input field
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search Restaurants") },
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                )

                // Restaurant cards
                LazyColumn {
                    items(restaurants.filter { it.name.contains(searchQuery, ignoreCase = true) }) { restaurant ->
                        RestaurantCard(restaurant) {
                            // Handle restaurant click
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
            .clickable(onClick = onRestaurantClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Restaurant image would go here
            // AsyncImage(model = restaurant.imageUrl, contentDescription = null)
            
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
    }
}

private data class NavigationItem(
    val title: String,
    val icon: ImageVector
)

private data class Restaurant(
    val id: String,
    val name: String,
    val rating: Float,
    val availableSlots: List<String>,
    val imageUrl: String
)
