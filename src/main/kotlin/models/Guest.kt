package models

import utils.Utilities


/**
 * The Guest data class represents a hotel guest, including their ID, name, contact details,
 * archived status, and a set of reservations. It provides methods for managing reservations,
 * including adding, updating, and deleting reservations.
 *
 * @property guestID The unique ID of the guest.
 * @property guestName The name of the guest.
 * @property guestPhone The phone number of the guest.
 * @property guestEmail The email address of the guest.
 * @property isGuestArchived Indicates whether the guest is archived (inactive).
 * @property reservations A set containing the reservations made by the guest.
 */
data class Guest(
    var guestID: Int = 0,
    var guestName: String = "",
    var guestPhone: String = "",
    var guestEmail: String = "",
    var isGuestArchived: Boolean = false,
    var reservations: MutableSet<Reservation> = mutableSetOf()
) {


    /**
     * Adds a reservation to the guest's set of reservations.
     *
     * @param reservation The reservation to be added.
     * @return `true` if the reservation was added successfully, `false` otherwise.
     */
    fun addReservation(reservation: Reservation): Boolean {
        reservations.add(reservation)
        return true
    }


    /**
     * Gets the number of reservations made by the guest.
     *
     * @return The number of reservations.
     */
    fun numberOfReservations() = reservations.size


    /**
     * Finds a reservation in the guest's set based on its ID.
     *
     * @param reservationId The ID of the reservation to be found.
     * @return The found reservation or `null` if not found.
     */
    fun findOne(reservationId: Int): Reservation? {
        return reservations.find { it.ReservationId == reservationId }
    }


    /**
     * Deletes a reservation from the guest's set based on its ID.
     *
     * @param id The ID of the reservation to be deleted.
     * @return `true` if the reservation was deleted successfully, `false` otherwise.
     */
    fun delete(id: Int): Boolean {
        return reservations.removeIf { reservation -> reservation.ReservationId == id }
    }


    /**
     * Updates the details of an existing reservation in the guest's set.
     *
     * @param reservationId The ID of the reservation to be updated.
     * @param updatedReservation The updated reservation details.
     * @return `true` if the update was successful, `false` otherwise.
     */
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


    /**
     * Formats the list of reservations as a string.
     *
     * @return A formatted string of reservations or a message indicating none are present.
     */
    fun listReservations() =
        if (reservations.isEmpty()) {
            "\tNO RESERVATIONS ADDED"
        } else {
            Utilities.formatSetString(reservations)
        }


    /**
     * Converts the guest object to a string representation, including guest details
     * and a list of reservations.
     *
     * @return The string representation of the guest.
     */
    override fun toString(): String {
        val archived = if (isGuestArchived) 'Y' else 'N'
        return "id $guestID: name $guestName, Phone($guestPhone), Email($guestEmail), Archived($archived) \n${listReservations()}"
    }
}