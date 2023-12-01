package models

import utils.Utilities

data class Guest(
    var guestID: Int = 0,
    var guestName: String = "",
    var guestPhone: String = "",
    var guestEmail: String = "",
    var isGuestArchived: Boolean = false,
    var reservations: MutableSet<Reservation> = mutableSetOf()) {




    fun addReservation(reservation: Reservation): Boolean {
        reservations.add(reservation)
        return true
    }

    fun numberOfReservations() = reservations.size

    fun findOne(reservationId: Int): Reservation? {
        return reservations.find { it.ReservationId == reservationId }
    }


    fun delete(id: Int): Boolean {
        return reservations.removeIf { reservation -> reservation.ReservationId == id }
    }

    fun update(reservationId: Int, updatedReservation: Reservation): Boolean {
        val foundReservation = findOne(reservationId)

        return if (foundReservation != null) {
            foundReservation.apply {
                RoomNum = updatedReservation.RoomNum
                cost = updatedReservation.cost
                numPeople = updatedReservation.numPeople
                isPaid = updatedReservation.isPaid
            }
            true
        } else {
            false
        }
    }


    fun listReservations() =
        if (reservations.isEmpty())  "\tNO RESERVATIONS ADDED"
        else  Utilities.formatSetString(reservations)



    override fun toString(): String {
        val archived = if (isGuestArchived) 'Y' else 'N'
        return "id $guestID: name $guestName, Phone($guestPhone), Email($guestEmail), Archived($archived) \n${listReservations()}"
    }
}