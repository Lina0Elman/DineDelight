package com.example.dineDelight.pages

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.dineDelight.models.Restaurant
import com.example.dineDelight.models.Review
import com.example.dineDelight.repositories.ImageRepository
import com.example.dineDelight.repositories.ReviewRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantReviewsScreen(navController: NavController, restaurant: Restaurant) {
    var reviews by remember { mutableStateOf<List<Review>>(emptyList()) }
    var showReviewDialog by remember { mutableStateOf(false) }
    var reviewText by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    val userEmail = FirebaseAuth.getInstance().currentUser?.email.orEmpty()
    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(restaurant) {
        reviews = ReviewRepository.getRestaurantReviews(restaurant.id)
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { Text("Restaurant Reviews") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { showReviewDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text("Leave a Review")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (reviews.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "This restaurant has no reviews yet",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
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
            }
        }

        if (showReviewDialog) {
            AlertDialog(
                onDismissRequest = { showReviewDialog = false },
                title = { Text("Leave a Review") },
                text = {
                    Column {
                        TextField(
                            value = reviewText,
                            onValueChange = { reviewText = it },
                            label = { Text("Your Review") }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { imagePickerLauncher.launch("image/*") }) {
                            Text("Select Image")
                        }
                        selectedImageUri?.let {
                            Text("Image selected: ${it.lastPathSegment}")
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        coroutineScope.launch {
                            try {
                                val imageId = withContext(Dispatchers.IO) { selectedImageUri?.let { ImageRepository.saveImageToLocalDatabase(it) } ?: "" }
                                val review = Review(
                                    userId = userId,
                                    userEmail = userEmail,
                                    restaurantId = restaurant.id,
                                    restaurantName = restaurant.name,
                                    text = reviewText,
                                    imageUrl = imageId
                                )
                                ReviewRepository.addReview(review)
                                reviews = ReviewRepository.getRestaurantReviews(restaurant.id)
                                withContext(Dispatchers.Main) {
                                    showReviewDialog = false
                                    reviewText = ""
                                    selectedImageUri = null
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
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
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = review.userEmail, style = MaterialTheme.typography.bodyLarge)
            Text(text = review.text, style = MaterialTheme.typography.bodyMedium)
            imageUri?.let {
                AsyncImage(
                    model = it,
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
}