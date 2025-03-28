package com.example.dineDelight.repositories

import com.example.dineDelight.models.Reservation
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await

object ReservationRepository {
    private val db = FirebaseFirestore.getInstance()
    private val reservationsCollection = db.collection("reservations")

    private val _reservations = MutableStateFlow<List<Reservation>>(emptyList())
    val reservations: StateFlow<List<Reservation>> get() = _reservations

    init {
        // Fetch initial reservations from Firestore in a coroutine
        CoroutineScope(Dispatchers.IO).launch {
            fetchInitialReservations()
        }
    }

    private suspend fun fetchInitialReservations() {
        try {
            val initialReservations = reservationsCollection.get().await().toObjects(Reservation::class.java)
            _reservations.value = initialReservations
        } catch (e: Exception) {
            // Handle any errors
            throw e
        }
    }

    suspend fun addReservation(reservation: Reservation) {
        try {
            // Add reservation to Firestore
            reservationsCollection.document(reservation.id).set(reservation).await()
            // Update local state flow after adding
            _reservations.value += reservation
        } catch (e: Exception) {
            // Handle any errors
            throw e
        }
    }

    suspend fun deleteReservation(reservationId: String) {
        try {
            // Delete reservation from Firestore
            reservationsCollection.document(reservationId).delete().await()
            // Update local state flow after deleting
            _reservations.value = _reservations.value.filter { it.id != reservationId }
        } catch (e: Exception) {
            // Handle any errors
            throw e
        }
    }

    suspend fun updateReservation(updatedReservation: Reservation) {
        try {
            // Update reservation in Firestore
            reservationsCollection.document(updatedReservation.id).set(updatedReservation).await()
            // Update local state flow after updating
            _reservations.value = _reservations.value.map {
                if (it.id == updatedReservation.id) updatedReservation else it
            }
        } catch (e: Exception) {
            // Handle any errors
            throw e
        }
    }

    suspend fun getUserReservations(userId: String): List<Reservation> {
        return try {
            // Retrieve reservations from Firestore for a specific user
            reservationsCollection.whereEqualTo("userId", userId).get().await()
                .toObjects(Reservation::class.java)
        } catch (e: Exception) {
            // Handle any errors
            throw e
        }
    }

    suspend fun getReservationById(reservationId: String): Reservation? {
        return try {
            // Retrieve reservation by ID from Firestore
            val documentSnapshot = reservationsCollection.document(reservationId).get().await()
            if (documentSnapshot.exists()) {
                documentSnapshot.toObject(Reservation::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            // Handle any errors
            throw e
        }
    }

    suspend fun getAvailableSlotsExcludingUserReservations(userId: String, restaurantId: Int): List<String> {
        val userReservations = getUserReservations(userId).filter { it.restaurantId == restaurantId }.map { it.time }
        val restaurant = RestaurantRepository.getRestaurants().find { it.id == restaurantId }
        val availableSlots = restaurant?.availableSlots ?: emptyList()
        return availableSlots.filterNot { it in userReservations }
    }
}