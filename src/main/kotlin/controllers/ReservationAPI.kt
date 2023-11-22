package controllers

import models.Reservation
import persistence.Serializer

class ReservationAPI(serializerType: Serializer){

    private var serializer: Serializer = serializerType

    private var reservations = ArrayList<Reservation>()



    fun add(Reservation: Reservation): Boolean {
        return reservations.add(Reservation)
    }




    @Throws(Exception::class)
    fun load() {
        reservations = serializer.read() as ArrayList<Reservation>
    }

    @Throws(Exception::class)
    fun store() {
        serializer.write(reservations)
    }}