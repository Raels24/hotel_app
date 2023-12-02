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
class GuestAPI(serializerType: Serializer) {

    private var serializer: Serializer = serializerType
    private var guests = ArrayList<Guest>()

    // ----------------------------------------------
    //  For Managing the id internally in the program
    // ----------------------------------------------
    private var lastId = 0


    /**
     * Generates a new unique ID for guests.
     *
     * @return A new unique ID.
     */
    private fun getId(): Int {
        lastId++
        return lastId
    }

    /**
     * Gets the number of guests currently stored.
     *
     * @return The number of guests.
     */
    fun numberOfGuests() = guests.size

    // ----------------------------------------------
    //  CRUD METHODS FOR guest ArrayList
    // ----------------------------------------------

    /**
     * Adds a new guest to the guest list.
     *
     * @param guest The guest to be added.
     * @return `true` if the guest was added successfully, `false` otherwise.
     */
    fun add(guest: Guest): Boolean {
        guest.guestID = getId()
        val added = guests.add(guest)
        lastId = guest.guestID // Update lastId after adding the guest
        return added
    }

    /**
     * Deletes a guest based on their ID.
     *
     * @param id The ID of the guest to be deleted.
     * @return `true` if the guest was deleted successfully, `false` otherwise.
     */

    fun delete(id: Int): Boolean {
        val guestToDelete = findGuest(id)
        return if (guestToDelete != null) {
            guests.removeIf { guest -> guest.guestID == id }
            true
        } else {
            false
        }
    }

    /**
     * Updates the details of an existing guest.
     *
     * @param id The ID of the guest to be updated.
     * @param updatedGuest The updated guest details.
     * @return `true` if the update was successful, `false` otherwise.
     */
    fun update(id: Int, updatedGuest: Guest?): Boolean {
        // find the guest object by the index number
        val foundGuest = findGuest(id)

        // if the guest exists, use the guest details passed as parameters to update the found guest in the ArrayList.
        if ((foundGuest != null) && (updatedGuest != null)) {
            foundGuest.guestID = updatedGuest.guestID
            foundGuest.guestName = updatedGuest.guestName
            foundGuest.guestPhone = updatedGuest.guestPhone
            foundGuest.guestEmail = updatedGuest.guestEmail
            return true
        }

        // if the guest was not found, return false, indicating that the update was not successful
        return false
    }


    /**
     * Finds a guest based on their ID.
     *
     * @param id The ID of the guest to be found.
     * @return The found guest or `null` if not found.
     */
    fun findGuest(id: Int): Guest? {
        return guests.find { it.guestID == id }
    }


    /**
     * Searches for guests by their ID and returns a formatted string.
     *
     * @param searchGuestId The ID to search for.
     * @return A formatted string of matching guests or an error message.
     */
    fun searchGuestsById(searchGuestId: Int): String {
        val matchingGuests = guests.filter { it.guestID == searchGuestId }
        if (matchingGuests.isNotEmpty()) {
            return matchingGuests.joinToString(separator = "\n") { guest ->
                guests.indexOf(guest).toString() + ": " + guest.toString()
            }
        } else {
            return "Guest not found."
        }
    }

    /**
     * Searches for guests by their name and returns a formatted string.
     *
     * @param searchGuestName The name to search for.
     * @return A formatted string of matching guests or an error message.
     */
    fun searchGuestsByName(searchGuestName: String): String {
        val matchingGuests = guests.filter { it.guestName == searchGuestName }
        if (matchingGuests.isNotEmpty()) {
            return matchingGuests.joinToString(separator = "\n") { guest ->
                guests.indexOf(guest).toString() + ": " + guest.toString()
            }
        } else {
            return "Guest not found."
        }
    }

    /**
     * Archives an active guest, making them inactive.
     *
     * @param indexToArchive The index of the guest to archive.
     * @return `true` if the archiving was successful, `false` otherwise.
     */
    fun archiveGuest(indexToArchive: Int): Boolean {
        if (isValidIndex(indexToArchive)) {
            val guestToArchive = guests[indexToArchive]
            if (!guestToArchive.isGuestArchived) {
                guestToArchive.isGuestArchived = true
                return true
            }
        }
        return false
    }

    // ----------------------------------------------
    //  LISTING METHODS FOR Guest ArrayList
    // ----------------------------------------------

    /**
     * Lists all guests and their details.
     *
     * @return A formatted string of all guests or an error message if none are stored.
     */
    fun listAllGuests() =
        if (guests.isEmpty()) {
            "No guests stored"
        } else {
            formatListString(guests)
        }


    /**
     * Lists active guests and their details.
     *
     * @return A formatted string of active guests or an error message if none are stored.
     */
    fun listActiveGuests() =
        if (numberOfActiveGuests() == 0) {
            "No active Guest stored"
        } else {
            formatListString(guests.filter { guest -> !guest.isGuestArchived })
        }


    /**
     * Lists archived guests and their details.
     *
     * @return A formatted string of archived guests or an error message if none are stored.
     */
    fun listArchivedGuests() =
        if (numberOfArchivedGuests() == 0) {
            "No archived Guest stored"
        } else {
            formatListString(guests.filter { guest -> guest.isGuestArchived })
        }

    // ----------------------------------------------
    //  COUNTING METHODS FOR Guest ArrayList
    // ----------------------------------------------

    /**
     * Gets the number of archived guests.
     *
     * @return The number of archived guests.
     */

    fun numberOfArchivedGuests(): Int = guests.count { guest: Guest -> guest.isGuestArchived }

    /**
     * Gets the number of active guests.
     *
     * @return The number of active guests.
     */
    fun numberOfActiveGuests(): Int = guests.count { guest: Guest -> !guest.isGuestArchived }

    /**
     * Checks if the given index is within the valid range for the guests list.
     *
     * @param index The index to be validated.
     * @return `true` if the index is valid, `false` otherwise.
     */
    fun isValidListIndex(index: Int, list: List<Any>): Boolean {
        return (index >= 0 && index < list.size)
    }


    /**
     * Searches for reservations by ID and returns a formatted string.
     *
     * @param searchString The ID to search for.
     * @return A formatted string of matching reservations or an error message.
     */
    fun searchReservationById(searchString: String): String {
        return if (numberOfGuests() == 0) {
            "No guests stored"
        } else {
            var listOfGuests = ""
            for (guest in guests) {
                for (reservation in guest.reservations) {
                    if (reservation.ReservationId.toString() == searchString) {
                        listOfGuests += "${guest.guestID}: ${guest.guestName} \n\t${reservation}\n"
                    }
                }
            }
            if (listOfGuests == "") {
                "No reservation found for ID: $searchString"
            } else {
                listOfGuests
            }
        }
    }

    /**
     * Checks if the given index is within the valid range for the provided list.
     *
     * @param index The index to be validated.
     * @param list The list to be checked against.
     * @return `true` if the index is valid, `false` otherwise.
     */
    fun isValidIndex(index: Int): Boolean {
        return isValidListIndex(index, guests)
    }

    @Throws(Exception::class)
    fun load() {
        guests = serializer.read() as ArrayList<Guest>
    }

    @Throws(Exception::class)
    fun store() {
        serializer.write(guests)
    }
}
