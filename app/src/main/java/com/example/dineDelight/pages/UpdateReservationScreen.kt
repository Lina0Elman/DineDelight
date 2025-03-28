package com.example.dineDelight.pages

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.dineDelight.models.Reservation
import com.example.dineDelight.repositories.ReservationRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun UpdateReservationScreen(navController: NavController, reservationId: UUID) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    var reservation by remember { mutableStateOf<Reservation?>(null) }
    var availableSlots by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedSlot by remember { mutableStateOf<String?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Fetch reservation and available slots in a coroutine
    LaunchedEffect(reservationId) {
        coroutineScope.launch {
            try {
                // Fetch the reservation
                reservation = ReservationRepository.getReservationById(reservationId.toString())
                reservation?.let {
                    // Fetch available slots excluding user's reservations
                    availableSlots = ReservationRepository.getAvailableSlotsExcludingUserReservations(userId, it.restaurantId)
                    selectedSlot = it.time // Set initial slot
                }
            } catch (e: Exception) {
                Log.e("UpdateReservationScreen", "Error fetching data: ${e.message}")
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        // Back button
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }

        Text(text = "Update Reservation", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        // Show available slots if data is loaded
        if (reservation != null && availableSlots.isNotEmpty()) {
            LazyColumn {
                items(availableSlots) { slot ->
                    Button(
                        onClick = {
                            selectedSlot = slot
                            showDialog = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(text = "Change to $slot")
                    }
                }
            }
        } else {
            Text(text = "Loading reservation details...", style = MaterialTheme.typography.bodyLarge)
        }
    }

    // Show confirmation dialog for updating the reservation
    if (showDialog && selectedSlot != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirm Update") },
            text = { Text("Are you sure you want to change the reservation to $selectedSlot?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        reservation?.let {
                            val updatedReservation = it.copy(time = selectedSlot!!)
                            coroutineScope.launch {
                                try {
                                    ReservationRepository.updateReservation(updatedReservation)
                                    navController.popBackStack() // Go back after update
                                } catch (e: Exception) {
                                    Log.e("UpdateReservationScreen", "Error updating reservation: ${e.message}")
                                }
                            }
                        }
                        showDialog = false
                    }
                ) {
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