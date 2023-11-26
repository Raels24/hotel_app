package utils

import models.Reservation
import models.Guest

object Utilities {



    @JvmStatic
    fun formatListString(guestToFormat: List<Guest>): String =
        guestToFormat
            .joinToString(separator = "\n") { guest ->  "$guest" }

    @JvmStatic
    fun formatSetString(reservationsToFormat: Set<Reservation>): String =
        reservationsToFormat
            .joinToString(separator = "\n") { reservation ->  "\t$reservation" }

}
