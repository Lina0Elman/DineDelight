package com.example.dineDelight.pages


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.dineDelight.models.Reservation
import com.example.dineDelight.models.Review
import com.example.dineDelight.repositories.ReservationRepository
import com.example.dineDelight.repositories.ReviewRepository
import com.example.dineDelight.views.BottomNavigationBar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun UpdateReviewScreen(navController: NavController, reviewId: String) {
    var review by remember { mutableStateOf<Review?>(null) }
    var updatedText by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Fetch the review when the screen is first loaded
    LaunchedEffect(reviewId) {
        coroutineScope.launch {
            review = ReviewRepository.getReviewById(reviewId)
            updatedText = review?.text.orEmpty()
        }
    }

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
                label = { Text("Review") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { showDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Changes")
            }
        } else {
            Text(text = "Loading review...", style = MaterialTheme.typography.bodyLarge)
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirm Update") },
            text = { Text("Are you sure you want to update this review?") },
            confirmButton = {
                TextButton(onClick = {
                    review?.let {
                        val updatedReview = it.copy(text = updatedText)
                        coroutineScope.launch {
                            ReviewRepository.updateReview(updatedReview)
                        }
                    }
                    showDialog = false
                    navController.popBackStack()
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("No")
                }
            }
        )
    }
}