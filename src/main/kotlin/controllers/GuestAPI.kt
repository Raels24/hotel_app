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

    fun add(guest: Guest): Boolean {
        guest.guestID = generateId()
        return guests.add(guest)
    }

    fun delete(id: Int): Boolean {
        val guestToDelete = findGuest(id)
        return if (guestToDelete != null) {
            guests.remove(guestToDelete)
            true
        } else {
            false
        }
    }

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

    fun searchGuestsById(searchGuestId: Int): String =
            searchGuests(searchGuestId) { it.guestID == searchGuestId }

    fun searchGuestsByName(searchGuestName: String): String =
            searchGuests(searchGuestName) { it.guestName == searchGuestName }

    fun searchReservationById(searchString: String): String =
            if (numberOfGuests() == 0) "No guests stored"
            else {
                guests.flatMap { guest ->
                    guest.reservations.filter { it.ReservationId.toString() == searchString }
                            .map { "${guest.guestID}: ${guest.guestName} \n\t$it" }
                }.joinToString("\n")
            }

    private fun <T> searchGuests(searchValue: T, condition: (Guest) -> Boolean): String {
        val matchingGuests = guests.filter(condition)
        return if (matchingGuests.isNotEmpty()) {
            formatListString(matchingGuests)
        } else {
            "Guest not found."
        }
    }

    fun isValidListIndex(index: Int, list: List<Any>): Boolean = index in 0 until list.size

    @Throws(Exception::class)
    fun load() {
        guests.clear()
        guests.addAll(serializer.read() as List<Guest>)
    }

    @Throws(Exception::class)
    fun store() {
        serializer.write(guests)
    }
}
