package models

data class Reservation(
    var ReservationId: Int = 0,
    var RoomNum: Int = 0,
    var cost: Int = 0,
    var numPeople: Int = 0,
    var isPaid: Boolean = false
)
