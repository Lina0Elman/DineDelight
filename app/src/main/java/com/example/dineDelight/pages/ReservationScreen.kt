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

        Text(text = "Available Slots", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(restaurant.availableSlots) { slot ->
                Button(
                    onClick = {
                        selectedSlot = slot
                        showDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(text = "Reserve at $slot")
                }
            }
        }
    }

    if (showDialog && selectedSlot != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirm Reservation") },
            text = { Text("Are you sure you want to reserve for time $selectedSlot?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        val reservation = Reservation(
                            id = UUID.randomUUID(),
                            restaurantId = restaurant.id,
                            restaurantName = restaurant.name,
                            time = selectedSlot!!,
                            userId = userId
                        )
                        ReservationRepository.addReservation(reservation)
                        showDialog = false
                        navController.navigate("home")
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