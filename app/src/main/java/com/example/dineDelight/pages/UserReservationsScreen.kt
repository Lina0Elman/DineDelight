package com.example.dineDelight.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.dineDelight.models.Reservation
import com.example.dineDelight.repositories.ReservationRepository
import com.example.dineDelight.views.BottomNavigationBar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserReservationsScreen(navController: NavController) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    var reservations by remember { mutableStateOf<List<Reservation>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    // Fetch reservations in a coroutine when the screen is first launched
    LaunchedEffect(userId) {
        coroutineScope.launch {
            reservations = ReservationRepository.getUserReservations(userId)
        }
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("My Reservations") },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = { BottomNavigationBar(navController, "My Reservations") }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (reservations.isEmpty()) {
                Text(text = "You have no reservations yet", style = MaterialTheme.typography.bodyLarge)
            } else {
                LazyColumn {
                    items(reservations) { reservation ->
                        ReservationCard(
                            reservation,
                            onDelete = {
                                coroutineScope.launch {
                                    ReservationRepository.deleteReservation(reservation.id)
                                    reservations = ReservationRepository.getUserReservations(userId)
                                }
                            },
                            onUpdate = {
                                navController.navigate("update_reservation/${reservation.id}")
                            }
                        )
                    }
                }
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