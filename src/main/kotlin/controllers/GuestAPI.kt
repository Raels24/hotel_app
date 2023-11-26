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
    private fun getId(): Int = lastId



    // ----------------------------------------------
    //  CRUD METHODS FOR guest ArrayList
    // ----------------------------------------------
    fun add(guest: Guest): Boolean {
        guest.guestID = getId()
        return guests.add(guest)  // Use guests instead of guest
    }

    fun delete(id: Int) = guests.removeIf { guest -> guest.guestID == id }

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
        val matchingGuests = guests.filter { guest -> guest.guestID == searchGuestId }
        if (matchingGuests.isNotEmpty()) {
            return matchingGuests.joinToString(separator = "\n") { guest ->
                guests.indexOf(guest).toString() + ": " + guest.toString()
            }
        } else {
            return "Guest not found."
        }
    }

    fun listAllGuests() =
        if (guests.isEmpty()) "No guest stored"
        else formatListString(guests)

    @Throws(Exception::class)
    fun load() {
        guests = serializer.read() as ArrayList<Guest>
    }

    @Throws(Exception::class)
    fun store() {
        serializer.write(guests)
    }
}

