package com.example.dineDelight.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.dineDelight.models.Restaurant
import com.example.dineDelight.models.Review
import com.example.dineDelight.repositories.ReviewRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantReviewsScreen(navController: NavController, restaurant: Restaurant) {
    var reviews by remember { mutableStateOf<List<Review>>(emptyList()) }
    var showReviewDialog by remember { mutableStateOf(false) }
    var reviewText by remember { mutableStateOf("") }
    val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    val userEmail = FirebaseAuth.getInstance().currentUser?.email.orEmpty()

    // Load reviews for the restaurant
    LaunchedEffect(restaurant) {
        reviews = ReviewRepository.getRestaurantReviews(restaurant.id)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top Bar with Back Button
        TopAppBar(
            title = { Text("Restaurant Reviews") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            items(reviews) { review ->
                ReviewCard(review)
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { showReviewDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Leave a Review")
                }
            }
        }

        // Review Dialog
        if (showReviewDialog) {
            AlertDialog(
                onDismissRequest = { showReviewDialog = false },
                title = { Text("Leave a Review") },
                text = {
                    TextField(
                        value = reviewText,
                        onValueChange = { reviewText = it },
                        label = { Text("Your Review") }
                    )
                },
                confirmButton = {
                    Button(onClick = {
                        val review = Review(
                            userId = userId,
                            userEmail = userEmail,
                            restaurantId = restaurant.id,
                            restaurantName = restaurant.name,
                            text = reviewText
                        )
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                ReviewRepository.addReview(review)
                                reviews = ReviewRepository.getRestaurantReviews(restaurant.id) // Refresh reviews
                                showReviewDialog = false
                                reviewText = "" // Reset review text on successful submission
                            } catch (e: Exception) {
                                e.printStackTrace() // Handle errors
                            }
                        }
                    }) {
                        Text("Submit")
                    }
                },
                dismissButton = {
                    Button(onClick = { showReviewDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun ReviewCard(review: Review) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = review.userEmail, style = MaterialTheme.typography.bodyLarge)
            Text(text = review.text, style = MaterialTheme.typography.bodyMedium)
        }
    }
}