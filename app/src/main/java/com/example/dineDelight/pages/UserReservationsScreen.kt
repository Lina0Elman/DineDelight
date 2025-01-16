package com.example.dineDelight.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.dineDelight.models.Reservation
import com.example.dineDelight.repositories.ReservationRepository
import com.google.firebase.auth.FirebaseAuth

@Composable
fun UserReservationsScreen(navController: NavController) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    val reservations = ReservationRepository.getUserReservations(userId)

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        // Back button
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }

        Text(text = "My Reservations", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(reservations) { reservation ->
                ReservationCard(reservation,
                    onDelete = {
                    ReservationRepository.deleteReservation(reservation.id)
                }, onUpdate = {
                    navController.navigate("update_reservation/${reservation.id}")
                })
            }
        }
    }
}




@Composable
fun ReservationCard(reservation: Reservation, onDelete: () -> Unit, onUpdate: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Restaurant: ${reservation.restaurantName}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Time: ${reservation.time}", style = MaterialTheme.typography.bodyMedium)
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
