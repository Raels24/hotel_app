package models

import utils.Utilities

data class Guest(
    var guestID: Int = 0,
    var guestName: String = "",
    var guestPhone: String = "",
    var guestEmail: String = "",
    var isGuestArchived: Boolean = false,
    var reservations : MutableSet<Reservation> = mutableSetOf()) {

    private var lastReservationId = 0
    private fun getReservationId(): Int = lastReservationId++

    fun addReservation(reservation: Reservation): Boolean {
        reservation.ReservationId = getReservationId()
        return reservations.add(reservation)

    }

    fun numberOfReservations() = reservations.size

    fun findOne(id: Int): Reservation? {
        return reservations.find { reservation -> reservation.ReservationId == id }
    }


    fun delete(id: Int): Boolean {
        return reservations.removeIf { reservation -> reservation.ReservationId == id }
    }

    fun update(id: Int, newReservation: Reservation): Boolean {
        val foundReservation = findOne(id)


        if (foundReservation != null) {
            foundReservation.RoomNum = newReservation.RoomNum
            foundReservation.cost = newReservation.cost
            foundReservation.numPeople = newReservation.numPeople
            return true
        }
        return false
    }

    fun listReservations() =
        if (reservations.isEmpty())  "\tNO RESERVATIONS ADDED"
        else  Utilities.formatSetString(reservations)


    override fun toString(): String {
        val archived = if (isGuestArchived) 'Y' else 'N'
        return "$guestID: ${guestName}st, Priority($guestPhone), Category($guestEmail), Archived($archived) \n${listReservations()}"
    }
}