package controllers

import models.Guest
import persistence.Serializer

class GuestAPI(serializerType: Serializer){

    private var serializer: Serializer = serializerType

    private var guests = ArrayList<Guest>()



    fun add(Guest: Guest): Boolean {
        return guests.add(Guest)
    }




    @Throws(Exception::class)
    fun load() {
        guests = serializer.read() as ArrayList<Guest>
    }

    @Throws(Exception::class)
    fun store() {
        serializer.write(guests)
    }}