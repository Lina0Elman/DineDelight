package com.example.dineDelight.pages

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.example.dineDelight.models.Restaurant
import com.example.dineDelight.repositories.ReservationRepository
import com.example.dineDelight.models.Reservation
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ReservationScreen(navController: NavController, restaurant: Restaurant) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    val userReservedSlots = ReservationRepository.getUserReservations(userId).filter { it.restaurantId == restaurant.id }.map { it.time }
    var selectedSlot by remember { mutableStateOf<String?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        // Back button
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }

        Text(text = "Reserve at ${restaurant.name}", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(restaurant.availableSlots) { slot ->
                val isReserved = slot in userReservedSlots
                Button(
                    onClick = {
                        if (!isReserved) {
                            selectedSlot = slot
                            showDialog = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    enabled = !isReserved
                ) {
                    Text(text = if (isReserved) "$slot (Reserved)" else "Reserve $slot")
                }
            }
        }
    }

    if (showDialog && selectedSlot != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirm Reservation") },
            text = { Text("Are you sure you want to reserve the slot at $selectedSlot?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        val newReservation = Reservation(
                            id = UUID.randomUUID(),
                            userId = userId,
                            restaurantId = restaurant.id,
                            restaurantName = restaurant.name,
                            time = selectedSlot!!
                        )
                        ReservationRepository.addReservation(newReservation)
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