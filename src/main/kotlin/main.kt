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
            5 -> archiveGuest()
            6 -> listGuests()
            20 -> save()
            21 -> load()
        }
    } while (choice != 0)
}

fun mainMenu(): Int {
    return ScannerInput.readNextInt(
        """
        > --------------------------------------------------
        > |         Hotel Trivago                          |
        > --------------------------------------------------
        > | Guest MENU                                     |
        > |   1) Add a Guest                               |
        > |   2) Update a guest                            |
        > |   3) Delete a guest                            |
        > |   4) Search guest                              |
        > |   5) Archive guest                             |
        > |   6) list guests                               |
        > |------------------------------------------------|
        > | Reservation Menu                               |
        > |   7) add reservation to guest                  |
        > |   8) update reservation contents in guest      |
        > |   9) delete reservation from guest             |
        > --------------------------------------------------
        > |   20) Save Guests                              |
        > |   21) Load Guests                              |
        > --------------------------------------------------
        > |   0) Exit                                      |
        > --------------------------------------------------
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
    listGuests()
    if (guestAPI.numberOfGuests() > 0) {
        // only ask the user to choose the guest if guests exist
        val id = ScannerInput.readNextInt("Enter the id of the guest to update: ")
        if (guestAPI.findGuest(id) != null) {
            val guestID = ScannerInput.readNextInt("Enter guest id: ")
            val guestName = ScannerInput.readNextLine("Enter guest name: ")
            val guestPhone = ScannerInput.readNextLine("Enter guest's phone number: ")
            val guestEmail = ScannerInput.readNextLine("Enter guest's email: ")

            // Create a Guest object with the new details
            val updatedGuest = Guest(guestID, guestName, guestPhone, guestEmail, false)

            // Pass the index of the guest and the new guest details to GuestAPI for updating and check for success.
            if (guestAPI.update(id, updatedGuest)) {
                println("Update Successful")
            } else {
                println("Update Failed")
            }
        } else {
            println("There are no guests for this index number")
        }
    }
}


fun listGuests() {
    if (guestAPI.numberOfGuests() > 0) {
        val option = ScannerInput.readNextInt(
            """
                  > --------------------------------
                  > |   1) View ALL guests         |
                  > |   2) View ACTIVE guests      |
                  > |   3) View ARCHIVED guests    |
                  > --------------------------------
         > ==>> """.trimMargin(">")
        )

        when (option) {
            1 -> listAllGuests()
            2 -> listActiveGuests()
            3 -> listArchivedGuests()
            else -> println("Invalid option entered: $option")
        }
    } else {
        println("Option Invalid - No Guests stored")
    }
}

fun listAllGuests() = println(guestAPI.listAllGuests())
fun listActiveGuests() = println(guestAPI.listActiveGuests())
fun listArchivedGuests() = println(guestAPI.listArchivedGuests())

fun archiveGuest() {
    listActiveGuests()
    if (guestAPI.numberOfActiveGuests() > 0) {
        // only ask the user to choose the guest to archive if active guest exist
        val id = ScannerInput.readNextInt("Enter the id of the guest to archive: ")
        // pass the index of the guest to guestAPI for archiving and check for success.
        if (guestAPI.archiveGuest(id)) {
            println("Archive Successful!")
        } else {
            println("Archive NOT Successful")
        }
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
