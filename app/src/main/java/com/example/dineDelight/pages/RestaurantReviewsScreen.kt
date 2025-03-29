package com.example.dineDelight.pages

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.example.dineDelight.R
import com.example.dineDelight.models.Image
import com.example.dineDelight.models.Restaurant
import com.example.dineDelight.models.Review
import com.example.dineDelight.repositories.ImageRepository
import com.example.dineDelight.repositories.ReviewRepository
import com.example.dineDelight.utils.BlobUtils.toBase64String
import com.example.dineDelight.utils.BlobUtils.toBlob
import com.example.dineDelight.utils.BlobUtils.toBitmap
import com.example.dineDelight.utils.BlobUtils.toUri
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantReviewsScreen(navController: NavController, restaurant: Restaurant) {
    var reviews by remember { mutableStateOf<List<Review>>(emptyList()) }
    var showReviewDialog by remember { mutableStateOf(false) }
    var reviewText by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    val userEmail = FirebaseAuth.getInstance().currentUser?.email.orEmpty()
    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(restaurant) {
        reviews = ReviewRepository.getRestaurantReviews(restaurant.id).sortedByDescending { it.createdAt }
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

        Spacer(modifier = Modifier.height(8.dp))
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
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            IconButton(onClick = { showReviewDialog = false }) {
                                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                            Spacer(modifier = Modifier.weight(1f)) // Spacer to push title to the center
                            Text("Leave a Review")
                            Spacer(modifier = Modifier.width(48.dp)) // Spacer to maintain space on the right
                        }
                    }
                },
                onDismissRequest = { showReviewDialog = false },
                text = {
                    Column {
                        TextField(
                            value = reviewText,
                            onValueChange = { reviewText = it },
                            label = { Text("Your Review") }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Button(onClick = { imagePickerLauncher.launch("image/*") }) {
                                Text("Select Image")
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        selectedImageUri?.let { uri ->
                            AsyncImage(
                                model = uri,
                                contentDescription = "Selected Image Preview",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                                    .clip(MaterialTheme.shapes.medium),
                                contentScale = ContentScale.Crop
                            )
                        }
                        errorMessage?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                },
                confirmButton = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(onClick = {
                            coroutineScope.launch {
                                try {
                                    val imageId = UUID.randomUUID().toString()
                                    withContext(Dispatchers.IO) {
                                        selectedImageUri?.let { uri ->
                                            val blobBase64String = uri.toBlob(context)!!.toBase64String()
                                            ImageRepository.addImage(Image(
                                                id = imageId,
                                                blobBase64String = blobBase64String
                                            ))
                                        }
                                    }
                                    val review = Review(
                                        userId = userId,
                                        userEmail = userEmail,
                                        restaurantId = restaurant.id,
                                        restaurantName = restaurant.name,
                                        text = reviewText,
                                        imageId = imageId
                                    )
                                    ReviewRepository.addReview(review)
                                    reviews = ReviewRepository.getRestaurantReviews(restaurant.id).sortedByDescending { it.createdAt }
                                    withContext(Dispatchers.Main) {
                                        showReviewDialog = false
                                        reviewText = ""
                                        selectedImageUri = null
                                        errorMessage = null
                                    }
                                } catch (e: IllegalArgumentException) {
                                    withContext(Dispatchers.Main) {
                                        errorMessage = e.message
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }) {
                            Text("Submit")
                        }
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