package com.example.dineDelight.pages


import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.dineDelight.models.Image
import com.example.dineDelight.models.Review
import com.example.dineDelight.repositories.ImageRepository
import com.example.dineDelight.repositories.ReviewRepository
import com.example.dineDelight.utils.BlobUtils.toBase64String
import com.example.dineDelight.utils.BlobUtils.toBitmap
import com.example.dineDelight.utils.BlobUtils.toBlob
import com.example.dineDelight.utils.BlobUtils.toUri
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun UpdateReviewScreen(navController: NavController, reviewId: String) {
    var review by remember { mutableStateOf<Review?>(null) }

    var originalText by remember { mutableStateOf("") }
    var originalImageUri by remember { mutableStateOf<Uri?>(null) }

    var updatedText by remember { mutableStateOf("") }
    var updatedImageUri by remember { mutableStateOf<Uri?>(null) }

    var showDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Fetch the review when the screen is first loaded
    LaunchedEffect(reviewId) {
        coroutineScope.launch {
            review = ReviewRepository.getReviewById(reviewId)
            review?.let { rev ->
                originalText = rev.text
                updatedText = rev.text
                // Convert existing image to Uri, if present
                originalImageUri = rev.imageId?.let { imageId ->
                    ImageRepository.getImageById(imageId)
                        ?.blobBase64String
                        ?.toBlob()
                        ?.toBitmap()
                        ?.toUri(context)
                }
                updatedImageUri = originalImageUri
            }
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let { selectedUri ->
                val blob = selectedUri.toBlob(context)?.toBase64String() ?: ""
                if (blob.length > 1048487) {
                    errorMessage = "Image size exceeds the limit."
                } else {
                    updatedImageUri = selectedUri
                    errorMessage = null
                }
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }

        Text(text = "Update Review", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        if (review != null) {
            TextField(
                value = updatedText,
                onValueChange = { updatedText = it },
                label = { Text("Review", style = MaterialTheme.typography.bodyLarge) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            updatedImageUri?.let { uri ->
                AsyncImage(
                    model = uri,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { imagePickerLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Change Image", style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { showDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Changes", style = MaterialTheme.typography.bodyLarge)
            }

            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        } else {
            Text(text = "Loading review...", style = MaterialTheme.typography.bodyLarge)
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirm Update", style = MaterialTheme.typography.bodyLarge) },
            text = { Text("Are you sure you want to update this review?", style = MaterialTheme.typography.bodyLarge) },
            confirmButton = {
                TextButton(
                    onClick = {
                        review?.let { oldReview ->
                            coroutineScope.launch {
                                try {
                                    // If the user selected a new image, re-upload it
                                    var newImageId = oldReview.imageId
                                    if (updatedImageUri != originalImageUri && updatedImageUri != null) {
                                        val blob = updatedImageUri?.toBlob(context)
                                        val base64String = blob?.toBase64String()
                                        if (!base64String.isNullOrEmpty()) {
                                            val newImage = Image(
                                                id = oldReview.imageId ?: UUID.randomUUID().toString(),
                                                blobBase64String = base64String
                                            )
                                            ImageRepository.addImage(newImage)
                                            newImageId = newImage.id
                                        }
                                    }

                                    // Only update the text if it changed
                                    val newText = if (updatedText != originalText) {
                                        updatedText
                                    } else {
                                        originalText
                                    }

                                    // Build the updated review
                                    val updatedReview = oldReview.copy(
                                        text = newText,
                                        imageId = newImageId
                                    )

                                    ReviewRepository.updateReview(updatedReview)
                                    navController.popBackStack()
                                } catch (e: Exception) {
                                    errorMessage = e.message
                                } finally {
                                    showDialog = false
                                }
                            }
                        }
                    }
                ) {
                    Text("Yes", style = MaterialTheme.typography.bodyLarge)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("No", style = MaterialTheme.typography.bodyLarge)
                }
            }
        )
    }
}