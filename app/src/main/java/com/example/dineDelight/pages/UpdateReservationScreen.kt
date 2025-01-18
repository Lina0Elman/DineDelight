package com.example.dineDelight.pages

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.dineDelight.repositories.ReservationRepository
import com.google.firebase.auth.FirebaseAuth
import java.util.UUID

@Composable
fun UpdateReservationScreen(navController: NavController, reservationId: UUID) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    val reservation = ReservationRepository.getReservationById(reservationId)
    val availableSlots by ReservationRepository.getAvailableSlotsExcludingUserReservations(
        userId, reservation!!.restaurantId)
        .collectAsState(initial = emptyList())
    var selectedSlot by remember { mutableStateOf<String?>(reservation?.time) }
    var showDialog by remember { mutableStateOf(false) }

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
    }

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
                            ReservationRepository.updateReservation(updatedReservation)
                        }
                        showDialog = false
                        navController.popBackStack()
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