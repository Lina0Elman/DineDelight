package com.example.dineDelight.pages


import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.dineDelight.models.Reservation
import com.example.dineDelight.models.Review
import com.example.dineDelight.repositories.ImageRepository
import com.example.dineDelight.repositories.ReservationRepository
import com.example.dineDelight.repositories.ReviewRepository
import com.example.dineDelight.views.BottomNavigationBar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserReviewsScreen(navController: NavController) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    var reviews by remember { mutableStateOf(listOf<Review>()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(userId) {
        try {
            reviews = ReviewRepository.getUserReviews(userId)
        } catch (e: Exception) {
            // Handle error (e.g., show an error message)
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("My Reviews") },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = { BottomNavigationBar(navController, "My Reviews") }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else if (reviews.isEmpty()) {
                Text(text = "You have no reviews yet", style = MaterialTheme.typography.bodyLarge)
            } else {
                LazyColumn {
                    items(reviews) { review ->
                        ReviewCard(
                            review = review,
                            onDelete = {
                                // Trigger the deletion directly inside a coroutine scope
                                CoroutineScope(Dispatchers.IO).launch {
                                    try {
                                        ReviewRepository.deleteReview(review.id)
                                        val updatedReviews = ReviewRepository.getUserReviews(userId)
                                        // Update the reviews state on the main thread
                                        withContext(Dispatchers.Main) {
                                            reviews = updatedReviews
                                        }
                                    } catch (e: Exception) {
                                        // Handle error (e.g., show an error message)
                                    }
                                }
                            },
                            onUpdate = {
                                navController.navigate("update_review/${review.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewCard(review: Review, onDelete: () -> Unit, onUpdate: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(review.imageUrl) {
        coroutineScope.launch {
            imageUri = ImageRepository.getImageUriById(review.imageUrl)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Restaurant: ${review.restaurantName}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Review: ${review.text}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "By: ${review.userEmail}", style = MaterialTheme.typography.bodySmall)
            imageUri?.let {
                AsyncImage(
                    model = imageUri,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )
            }
            Row {
                Button(onClick = onUpdate) {
                    Text("Update")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onDelete) {
                    Text("Delete")
                }
            }
        }
    }
}