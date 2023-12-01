package utils

import models.Guest
import models.Reservation

object Utilities {

    @JvmStatic
    fun formatListString(guestToFormat: List<Guest>): String =
        guestToFormat
            .joinToString(separator = "\n") { guest -> "$guest" }

    @JvmStatic
    fun formatSetString(reservationsToFormat: Set<Reservation>): String =
        reservationsToFormat
            .joinToString(separator = "\n") { reservation -> "\t$reservation" }
}
