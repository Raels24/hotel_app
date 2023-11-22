import controllers.GuestAPI
import controllers.ReservationAPI
import models.Guest
import models.Reservation
import utils.ScannerInput
import utils.ScannerInput.readNextInt
import utils.ScannerInput.readNextLine
import persistence.JSONSerializer
import persistence.XMLSerializer
import persistence.YAMLSerializer

import java.io.File






private val GuestAPI = GuestAPI(XMLSerializer(File("guests.xml")))
private val ReservationAPI = ReservationAPI(XMLSerializer(File("reservations.xml")))

fun main(args: Array<String>) {



}


fun mainMenu() : Int {
    return ScannerInput.readNextInt(""" 
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
         > ==>> """.trimMargin(">"))
}




fun save() {
    try {
        GuestAPI.store()
    } catch (e: Exception) {
        System.err.println("Error writing to file: $e")
    }
}

fun load() {
    try {
        GuestAPI.load()
    } catch (e: Exception) {
        System.err.println("Error reading from file: $e")
    }
}

