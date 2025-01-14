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
}