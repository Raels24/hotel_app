import controllers.GuestAPI
import models.Guest
import persistence.XMLSerializer
import utils.ScannerInput
import java.io.File

private val guestAPI = GuestAPI(XMLSerializer(File("guests.xml")))

fun main(args: Array<String>) {
    var choice: Int
    do {
        choice = mainMenu()
        when (choice) {
            1 -> addGuest()
            2 -> updateGuest()
            3 -> deleteGuest()
            4 -> searchGuest()
            20 -> save()
            21 -> load()
        }
    } while (choice != 0)
}

fun mainMenu(): Int {
    return ScannerInput.readNextInt(
        """
        > -------------------------------------
        > |         Hotel Trivago             |
        > -------------------------------------
        > | NOTE MENU                         |
        > |   1) Add a Guest                  |
        > |   2) Update a guest               |
        > |   3) Delete a guest               |
        > |   4) Search guest                 |
        > -------------------------------------
        > |   20) Save Guests                 |
        > |   21) Load Guests                 |
        > -------------------------------------
        > |   0) Exit                         |
        > -------------------------------------
        > ==>> """.trimMargin(">")
    )
}

fun addGuest() {
    val guestID = ScannerInput.readNextInt("Enter ID for a guest: ")
    val guestName = ScannerInput.readNextLine("Enter name of guest: ")
    val guestPhone = ScannerInput.readNextLine("Enter guest phone number: ")
    val guestEmail = ScannerInput.readNextLine("Enter guest email: ")
    val isAdded = guestAPI.add(Guest(guestID = guestID, guestName = guestName, guestPhone = guestPhone, guestEmail = guestEmail))

    if (isAdded) {
        println("Added Successfully")
    } else {
        println("Add Failed")
    }
}

fun updateGuest() {
    val guestID = ScannerInput.readNextInt("Enter ID of the guest to update: ")
    val updatedGuest = Guest(
        guestID = guestID,
        guestName = ScannerInput.readNextLine("Enter updated name of guest: "),
        guestPhone = ScannerInput.readNextLine("Enter updated guest phone number: "),
        guestEmail = ScannerInput.readNextLine("Enter updated guest email: ")
    )
    val isUpdated = guestAPI.update(guestID, updatedGuest)

    if (isUpdated) {
        println("Updated Successfully")
    } else {
        println("Update Failed. Guest not found.")
    }
}

fun deleteGuest() {
    val guestID = ScannerInput.readNextInt("Enter ID of the guest to delete: ")
    val isDeleted = guestAPI.delete(guestID)

    if (isDeleted) {
        println("Deleted Successfully")
    } else {
        println("Delete Failed. Guest not found.")
    }
}

fun searchGuest() {
    val guestID = ScannerInput.readNextInt("Enter guest ID to search: ")
    val result = guestAPI.searchGuestsById(guestID)

    if (result != "Guest not found.") {
        println("Guest found:\n$result")
    } else {
        println("Guest not found.")
    }
}




fun save() {
    try {
        guestAPI.store()
        println("Guests saved successfully.")
    } catch (e: Exception) {
        println("Error saving guests: $e")
    }
}

fun load() {
    try {
        guestAPI.load()
        println("Guests loaded successfully.")
    } catch (e: Exception) {
        println("Error loading guests: $e")
    }
}
