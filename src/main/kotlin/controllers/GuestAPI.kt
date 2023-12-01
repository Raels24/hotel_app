package controllers

import models.Guest
import persistence.Serializer
import utils.Utilities.formatListString

class GuestAPI(serializerType: Serializer) {

    private var serializer: Serializer = serializerType
    private var guests = ArrayList<Guest>()

    // ----------------------------------------------
    //  For Managing the id internally in the program
    // ----------------------------------------------
    private var lastId = 0
    private fun getId(): Int {
        lastId++
        return lastId
    }


    fun numberOfGuests() = guests.size



    // ----------------------------------------------
    //  CRUD METHODS FOR guest ArrayList
    // ----------------------------------------------
    fun add(guest: Guest): Boolean {
        guest.guestID = getId()
        val added = guests.add(guest)
        lastId = guest.guestID  // Update lastId after adding the guest
        return added
    }




    fun delete(id: Int): Boolean {
        val guestToDelete = findGuest(id)
        return if (guestToDelete != null) {
            guests.removeIf { guest -> guest.guestID == id }
            true
        } else {
            false
        }
    }



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

    fun findGuest(id: Int): Guest? {
        return guests.find { it.guestID == id }
    }





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
    fun listAllGuests() =
        if (guests.isEmpty()) "No guests stored"
        else formatListString(guests)

    fun listActiveGuests() =
        if (numberOfActiveGuests() == 0) "No active Guest stored"
        else formatListString(guests.filter { guest -> !guest.isGuestArchived })

    fun listArchivedGuests() =
        if (numberOfArchivedGuests() == 0) "No archived Guest stored"
        else formatListString(guests.filter { guest -> guest.isGuestArchived })

    // ----------------------------------------------
    //  COUNTING METHODS FOR Guest ArrayList
    // ----------------------------------------------
    fun numberOfGuest() = guests.size
    fun numberOfArchivedGuests(): Int = guests.count { guest: Guest -> guest.isGuestArchived }
    fun numberOfActiveGuests(): Int = guests.count { guest: Guest -> !guest.isGuestArchived }

    fun isValidListIndex(index: Int, list: List<Any>): Boolean {
        return (index >= 0 && index < list.size)
    }

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
     * Checks if the given index is within the valid range for the notes list.
     *
     * @param index The index to be validated.
     * @return `true` if the index is valid, `false` otherwise.
     */
    fun isValidIndex(index: Int) :Boolean{
        return isValidListIndex(index, guests);
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

