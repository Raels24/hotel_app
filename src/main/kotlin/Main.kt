import controllers.GuestAPI
import models.Guest
import models.Reservation
import persistence.XMLSerializer
import utils.ScannerInput
import java.io.File

private val guestAPI = GuestAPI(XMLSerializer(File("guests.xml")))
// private val guestAPI = GuestAPI(JSONSerializer(File("guests.json")))
// val guestAPI = GuestAPI(YAMLSerializer(File("guests.yaml")))

/**
 * Entry point of the program. Displays the main menu and handles user input.
 *
 * @param args Command-line arguments (not used in this application).
 */
fun main(args: Array<String>) {
    var choice: Int
    do {
        choice = mainMenu()
        when (choice) {
            1 -> addGuest()
            2 -> updateGuest()
            3 -> deleteGuest()
            4 -> searchGuests()
            5 -> archiveGuest()
            6 -> listGuests()
            7 -> addReservationToGuest()
            8 -> updateReservationInGuest()
            9 -> deleteAnReservation()
            10 -> searchReservations()
            20 -> save()
            21 -> load()
        }
    } while (choice != 0)
}

/**
 * Displays the main menu and reads the user's choice.
 *
 * @return The user's menu choice.
 */
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
        > |   4) Search guests                             |
        > |   5) Archive guest                             |
        > |   6) list guests                               |
        > |------------------------------------------------|
        > | Reservation Menu                               |
        > |   7) add reservation to guest                  |
        > |   8) update reservation contents in guest      |
        > |   9) delete reservation from guest             |
        > |   10) search reservations                      |
        > --------------------------------------------------
        > |   20) Save Guests                              |
        > |   21) Load Guests                              |
        > --------------------------------------------------
        > |   0) Exit                                      |
        > --------------------------------------------------
        > ==>> """.trimMargin(">")
    )
}

/**
 * Adds a new guest by taking input from the user.
 */
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

/**
 * Updates an existing guest by taking input from the user.
 */
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

/**
 * Displays a list of guests based on the user's choice.
 */
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

/**
 * Archives an active guest, making them inactive.
 */
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

/**
 * Deletes a guest based on user input.
 */
fun deleteGuest() {
    val guestID = ScannerInput.readNextInt("Enter ID of the guest to delete: ")
    val isDeleted = guestAPI.delete(guestID)

    if (isDeleted) {
        println("Deleted Successfully")
    } else {
        println("Delete Failed. Guest not found.")
    }
}

/**
 * Searches for guests based on user input.
 */
fun searchGuests() {
    if (guestAPI.numberOfGuests() > 0) {
        val option = ScannerInput.readNextInt(
            """
                  > --------------------------------
                  > |   1) search guest by ID      |
                  > |   2) search guest by name    |
                  > --------------------------------
         > ==>> """.trimMargin(">")
        )

        when (option) {
            1 -> searchGuestByID()
            2 -> searchGuestByName()
            else -> println("Invalid option entered: $option")
        }
    } else {
        println("Option Invalid - No Guests stored")
    }
}

/**
 * Searches for a guest by their ID.
 */
fun searchGuestByID() {
    val searchID = ScannerInput.readNextInt("Enter the guestID to search by: ")
    val searchResults = guestAPI.searchGuestById(searchID)
    if (searchResults.isEmpty()) {
        println("No guests found")
    } else {
        println(searchResults)
    }
}

/**
 * Searches for a guest by their name.
 */
fun searchGuestByName() {
    val searchName = ScannerInput.readNextLine("Enter the guest Name to search by: ")
    val searchResults = guestAPI.searchGuestByName(searchName)
    if (searchResults.isEmpty()) {
        println("No guests found")
    } else {
        println(searchResults)
    }
}

/**
 * Adds a reservation to an active guest based on user input.
 */
private fun addReservationToGuest() {
    val guest: Guest? = askUserToChooseActiveGuest()
    if (guest != null) {
        val reservationId = ScannerInput.readNextInt("\t Reservation id ")
        val roomNum = ScannerInput.readNextInt("\t Room number ")
        val cost = ScannerInput.readNextInt("\t cost ")
        val numPeople = ScannerInput.readNextInt("\t Number of people ")
        val isPaid = ScannerInput.readNextBoolean("\t Is the bill paid? ")

        val reservation = Reservation(reservationId, roomNum, cost, numPeople, isPaid)

        if (guest.addReservation(reservation)) {
            println("Add Successful!")
        } else {
            println("Add NOT Successful")
        }
    }
}

/**
 * Updates a reservation for an active guest based on user input.
 */
private fun updateReservationInGuest() {
    val guest: Guest? = askUserToChooseActiveGuest()
    if (guest != null) {
        val reservation: Reservation? = askUserToChooseReservation(guest)
        if (reservation != null) {
            val newID = ScannerInput.readNextInt("Enter new Reservation ID: ")
            val newRoomNum = ScannerInput.readNextInt("Enter new Room Number: ")
            val newCost = ScannerInput.readNextInt("Enter new Cost: ")
            val newNumPeople = ScannerInput.readNextInt("Enter new Number of People: ")
            val newIsPaid = ScannerInput.readNextBoolean("Is the bill paid? ")

            val updatedReservation = Reservation(newID, newRoomNum, newCost, newNumPeople, newIsPaid)

            if (guest.update(reservation.ReservationId, updatedReservation)) {
                println("Reservation updated successfully")
            } else {
                println("Failed to update Reservation")
            }
        } else {
            println("Invalid Reservation Id")
        }
    }
}

/**
 * Deletes a reservation for an active guest based on user input.
 */
fun deleteAnReservation() {
    val guest: Guest? = askUserToChooseActiveGuest()
    if (guest != null) {
        val reservation: Reservation? = askUserToChooseReservation(guest)
        if (reservation != null) {
            val isDeleted = guest.delete(reservation.ReservationId)
            if (isDeleted) {
                println("Delete Successful!")
            } else {
                println("Delete NOT Successful")
            }
        }
    }
}

/**
 * Searches for reservations based on user input.
 */
fun searchReservations() {
    val searchReservationId = ScannerInput.readNextInt("Enter the reservation ID: ")
    val searchResults = guestAPI.searchReservationById(searchReservationId)
    if (searchResults.isEmpty()) {
        println("No items found")
    } else {
        println(searchResults)
    }
}

// ------------------------------------
// HELPER FUNCTIONS
// --
private fun askUserToChooseActiveGuest(): Guest? {
    listActiveGuests()
    if (guestAPI.numberOfActiveGuests() > 0) {
        val guest = guestAPI.findGuest(ScannerInput.readNextInt("\nEnter the id of the guest: "))
        if (guest != null) {
            if (guest.isGuestArchived) {
                println("guest is NOT Active, it is Archived")
            } else {
                return guest // chosen guest is active
            }
        } else {
            println("guest id is not valid")
        }
    }
    return null // selected guest is not active
}

private fun askUserToChooseReservation(guest: Guest): Reservation? {
    if (guest.numberOfReservations() > 0) {
        print(guest.listReservations())
        return guest.findOne(ScannerInput.readNextInt("\nEnter the id of reservation: "))
    } else {
        println("No reservations for chosen guest")
        return null
    }
}

/**
 * Saves guest data to a file.
 */
fun save() {
    try {
        guestAPI.store()
        println("Guests saved successfully.")
    } catch (e: Exception) {
        println("Error saving guests: $e")
    }
}

/**
 * Loads guest data from a file.
 */
fun load() {
    try {
        guestAPI.load()
        println("Guests loaded successfully.")
    } catch (e: Exception) {
        println("Error loading guests: $e")
    }
}
