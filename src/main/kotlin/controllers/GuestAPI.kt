package controllers

import models.Guest
import persistence.Serializer
import utils.Utilities.formatListString

/**
 * The GuestAPI class manages the guests in a hotel. It provides methods for adding, updating,
 * deleting, and searching for guests, as well as handling reservations. The class uses a Serializer
 * for persistence and supports different serialization formats (XML, JSON, YAML).
 *
 * @property serializer The serializer used for reading/writing guest data.
 * @property guests The list of guests managed by the GuestAPI.
 * @property lastId The last used ID for assigning unique IDs to guests.
 */
class GuestAPI(private val serializer: Serializer) {

    private val guests = mutableListOf<Guest>()
    private var lastId = 0

    /**
     * Generates a new unique ID for guests.
     *
     * @return A new unique ID.
     */
    private fun generateId(): Int {
        lastId++
        return lastId
    }

    /**
     * Gets the number of guests currently stored.
     *
     * @return The number of guests.
     */
    fun numberOfGuests(): Int = guests.size

    // CRUD METHODS FOR guest ArrayList

    /**
     * Adds a [Guest] to the list of guests managed by the GuestAPI.
     *
     * @param guest The guest to be added.
     * @return `true` if the guest was added successfully, `false` otherwise.
     */
    fun add(guest: Guest): Boolean {
        guest.guestID = generateId()
        return guests.add(guest)
    }

    /**
     * Deletes a guest with the specified ID from the list of guests.
     *
     * @param id The ID of the guest to be deleted.
     * @return `true` if the guest was deleted successfully, `false` if the guest was not found.
     */
    fun delete(id: Int): Boolean {
        val guestToDelete = findGuest(id)
        return if (guestToDelete != null) {
            guests.remove(guestToDelete)
            true
        } else {
            false
        }
    }

    /**
     * Updates the details of a guest with the specified ID.
     *
     * @param id The ID of the guest to be updated.
     * @param updatedGuest The updated details for the guest.
     * @return `true` if the guest was updated successfully, `false` if the guest was not found.
     */
    fun update(id: Int, updatedGuest: Guest?): Boolean {
        val foundGuest = findGuest(id)
        if (foundGuest != null && updatedGuest != null) {
            foundGuest.apply {
                guestID = updatedGuest.guestID
                guestName = updatedGuest.guestName
                guestPhone = updatedGuest.guestPhone
                guestEmail = updatedGuest.guestEmail
            }
            return true
        }
        return false
    }

    /**
     * Finds a guest with the specified ID in the list of guests.
     *
     * @param id The ID of the guest to be found.
     * @return The found guest or `null` if no guest is found with the specified ID.
     */
    fun findGuest(id: Int): Guest? = guests.find { it.guestID == id }

    // Listing Methods for Guest ArrayList

    fun listAllGuests(): String =
        if (guests.isEmpty()) "No guests stored" else formatListString(guests)

    fun listActiveGuests(): String =
        if (numberOfActiveGuests() == 0) "No active guests stored"
        else formatListString(guests.filter { !it.isGuestArchived })

    fun listArchivedGuests(): String =
        if (numberOfArchivedGuests() == 0) "No archived guests stored"
        else formatListString(guests.filter { it.isGuestArchived })

    // Counting Methods for Guest ArrayList

    fun numberOfArchivedGuests(): Int = guests.count { it.isGuestArchived }

    fun numberOfActiveGuests(): Int = guests.count { !it.isGuestArchived }

    // Other methods

    fun archiveGuest(indexToArchive: Int): Boolean {
        if (isValidListIndex(indexToArchive, guests)) {
            val guestToArchive = guests[indexToArchive]
            if (!guestToArchive.isGuestArchived) {
                guestToArchive.isGuestArchived = true
                return true
            }
        }
        return false
    }


    /**
     * Searches for guests by their ID in the list of guests.
     *
     * @param searchGuestId The ID to search for.
     * @return A formatted string containing matching guests or a message indicating no guests found.
     */
    fun searchGuestById(searchGuestId: Int): String {
        val matchingGuests = guests.filter { it.guestID == searchGuestId }
        return if (matchingGuests.isNotEmpty()) {
            formatListString(matchingGuests)
        } else {
            "Guest not found."
        }
    }

    /**
     * Searches for guests by their name in a case-insensitive manner.
     *
     * @param searchGuestName The name to search for.
     * @return A formatted string containing matching guests or a message indicating no guests found.
     */
    fun searchGuestByName(searchGuestName: String): String {
        val matchingGuests = guests.filter { it.guestName.contains(searchGuestName, ignoreCase = true) }
        return if (matchingGuests.isNotEmpty()) {
            formatListString(matchingGuests)
        } else {
            "Guest not found."
        }
    }


    /**
     * Searches for reservations by their ID across all guests.
     *
     * @param searchReservationId The ID to search for.
     * @return A formatted string containing matching reservations or a message indicating no reservations found.
     */
    fun searchReservationById(searchReservationId: Int): String {
        val matchingReservations = guests.flatMap { guest ->
            guest.reservations.filter { it.ReservationId == searchReservationId }
                .map { "${guest.guestID}: ${guest.guestName} \n\t$it" }
        }

        return if (matchingReservations.isNotEmpty()) {
            matchingReservations.joinToString("\n")
        } else {
            "No reservations found for the provided ID."
        }
    }

    /**
     * Checks whether the given index is a valid index in the specified list.
     *
     * @param index The index to check.
     * @param list The list to check against.
     * @return `true` if the index is valid, `false` otherwise.
     */
    fun isValidListIndex(index: Int, list: List<Any>): Boolean = index in 0 until list.size


    /**
     * Loads guest data from the serializer and replaces the current list of guests.
     *
     * @throws Exception If there is an error while loading the guest data.
     */
    @Throws(Exception::class)
    fun load() {
        guests.clear()
        guests.addAll(serializer.read() as List<Guest>)
    }


    /**
     * Writes the current list of guests to the serializer for persistence.
     *
     * @throws Exception If there is an error while storing the guest data.
     */
    @Throws(Exception::class)
    fun store() {
        serializer.write(guests)
    }
}
