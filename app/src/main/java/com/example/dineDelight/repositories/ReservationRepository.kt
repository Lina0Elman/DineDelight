package com.example.dineDelight.repositories

import com.example.dineDelight.models.Reservation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

object ReservationRepository {
    private val _reservations = MutableStateFlow<List<Reservation>>(emptyList())
    val reservations: StateFlow<List<Reservation>> get() = _reservations

    fun addReservation(reservation: Reservation) {
        _reservations.value += reservation
    }

    fun deleteReservation(reservationId: UUID) {
        _reservations.value = _reservations.value.filter { it.id != reservationId }
    }

    fun updateReservation(updatedReservation: Reservation) {
        _reservations.value = _reservations.value.map {
            if (it.id == updatedReservation.id) updatedReservation else it
        }
    }

    fun getUserReservations(userId: String): List<Reservation> {
        return _reservations.value.filter { it.userId == userId }
    }

    fun getReservationById(reservationId: UUID): Reservation? {
        return _reservations.value.find { it.id == reservationId }
    }


    fun getAvailableSlotsExcludingUserReservations(userId: String, restaurantId: Int): StateFlow<List<String>> {
        val userReservations = getUserReservations(userId).filter { it.restaurantId == restaurantId }.map { it.time }
        val restaurant = RestaurantRepository.getRestaurants().find { it.id == restaurantId }
        val availableSlots = restaurant?.availableSlots ?: emptyList()
        val filteredSlots = availableSlots.filterNot { it in userReservations }
        return MutableStateFlow(filteredSlots)
    }
}