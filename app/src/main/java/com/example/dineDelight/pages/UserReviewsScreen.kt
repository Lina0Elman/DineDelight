package com.example.dineDelight.pages


import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.example.dineDelight.R
import com.example.dineDelight.models.Reservation
import com.example.dineDelight.models.Review
import com.example.dineDelight.repositories.ImageRepository
import com.example.dineDelight.repositories.ReservationRepository
import com.example.dineDelight.repositories.ReviewRepository
import com.example.dineDelight.utils.BlobUtils.toBitmap
import com.example.dineDelight.utils.BlobUtils.toBlob
import com.example.dineDelight.utils.BlobUtils.toUri
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
    var showDeleteDialog by remember { mutableStateOf(false) }
    var reviewToDelete by remember { mutableStateOf<Review?>(null) }

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
                                reviewToDelete = review
                                showDeleteDialog = true
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

    if (showDeleteDialog && reviewToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to delete this review?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                ReviewRepository.deleteReview(reviewToDelete!!.id)
                                reviews = ReviewRepository.getUserReviews(userId)
                            } catch (e: Exception) {
                                // Handle error (e.g., show an error message)
                            } finally {
                                showDeleteDialog = false
                                reviewToDelete = null
                            }
                        }
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("No")
                }
            }
        )
    }
}

@Composable
fun ReviewCard(review: Review, onDelete: () -> Unit, onUpdate: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }

    val imageId = review.imageId
    LaunchedEffect(imageId) {
        if (imageId != null) {
            coroutineScope.launch {
                val image = ImageRepository.getImageById(imageId)
                imageUri = image?.blobBase64String?.toBlob()?.toBitmap()?.toUri(context)
            }
        }
    }

    LaunchedEffect(review.userId) {
        coroutineScope.launch {
            val profileImage = ImageRepository.getImageById(review.userId)
            profileImageUri = profileImage?.blobBase64String?.toBlob()?.toBitmap()?.toUri(context)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = "Restaurant: " + review.restaurantName, style = MaterialTheme.typography.bodyLarge)
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
            AsyncImage(
                model = profileImageUri ?: R.drawable.default_profile_image,
                contentDescription = "User Profile Picture",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .align(Alignment.TopEnd)
            )
        }
    }
}