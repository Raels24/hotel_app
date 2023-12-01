package controllers



import models.Guest
import models.Reservation
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import persistence.JSONSerializer
import persistence.XMLSerializer
import persistence.YAMLSerializer
import java.io.File
import kotlin.test.assertEquals


    class GuestAPITest {

        private var joe: Guest? = null
        private var emma: Guest? = null
        private var hotelGuests: GuestAPI? = GuestAPI(XMLSerializer(File("guests.xml")))
        private var emptyHotel: GuestAPI? = GuestAPI(XMLSerializer(File("empty_guests.xml")))

        @BeforeEach
        fun setup() {
            joe = Guest(1, "joe", "123456789", "joe@example.com")
            emma = Guest(2, "emma ", "987654321", "emma@example.com")

            // Adding 2 guests to the hotel guests API
            hotelGuests!!.add(joe!!)
            hotelGuests!!.add(emma!!)
        }

        @AfterEach
        fun tearDown() {
            joe = null
            emma = null
            hotelGuests = null
            emptyHotel = null
        }

        @Nested
        inner class AddGuests {
            @Test
            fun `adding a guest to a populated list adds to ArrayList`() {
                val newGuest = Guest(3, "Sam ", "555555555", "sam@example.com")
                assertEquals(2, hotelGuests!!.numberOfGuests())
                assertTrue(hotelGuests!!.add(newGuest))
                assertEquals(3, hotelGuests!!.numberOfGuests())

                // Check if the last added guest is not null
                val lastAddedGuest = hotelGuests!!.findGuest(hotelGuests!!.numberOfGuests())
                assertNotNull(lastAddedGuest, "Last added guest should not be null")


            }


            @Test
            fun `adding a guest to an empty list adds to ArrayList`() {
                val newGuest = Guest(4, "Eva", "888888888", "eva@example.com")
                assertEquals(0, emptyHotel!!.numberOfGuests())
                assertTrue(emptyHotel!!.add(newGuest))
                assertEquals(1, emptyHotel!!.numberOfGuests())

                val lastAddedGuest = emptyHotel!!.findGuest(emptyHotel!!.numberOfGuests())
                assertNotNull(lastAddedGuest, "Last added guest should not be null")
                assertEquals(newGuest, lastAddedGuest)
            }


        }

        @Nested
        inner class ListGuests {

            @Test
            fun `listAllGuests returns No Guests Stored message when ArrayList is empty`() {
                assertEquals(0, emptyHotel!!.numberOfGuests())
                assertTrue(emptyHotel!!.listAllGuests().lowercase().contains("no guests"))
            }

            @Test
            fun `listAllGuests returns Guests when ArrayList has guests stored`() {
                assertEquals(2, hotelGuests!!.numberOfGuests())
                val guestsString = hotelGuests!!.listAllGuests().lowercase()
                assertTrue(guestsString.contains("joe"))
                assertTrue(guestsString.contains("emma"))
            }


            @Test
            fun `listActiveGuests returns no active guests stored when ArrayList is empty`() {
                assertEquals(0, emptyHotel!!.numberOfActiveGuests())
                assertTrue(
                    emptyHotel!!.listActiveGuests().lowercase().contains("no active guest")
                )
            }

            @Test
            fun `listActiveGuests returns active guests when ArrayList has active guests stored`() {
                assertEquals(2, hotelGuests!!.numberOfActiveGuests())
                val activeNotesString = hotelGuests!!.listActiveGuests().lowercase()
                assertTrue(activeNotesString.contains("joe"))
                assertTrue(activeNotesString.contains("emma"))

            }

            @Test
            fun `listArchivedGuests returns no archived guests when ArrayList is empty`() {
                assertEquals(0, emptyHotel!!.numberOfArchivedGuests())
                assertFalse(
                    emptyHotel!!.listArchivedGuests().lowercase().contains("no archived guests")
                )
            }

            @Test
            fun `listArchivedGuests returns archived guests when ArrayList has archived guests stored`() {
                assertEquals(0, hotelGuests!!.numberOfArchivedGuests())
                val archivedNotesString = hotelGuests!!.listArchivedGuests().lowercase()
                assertFalse(archivedNotesString.contains("joe"))

            }

        }

        @Nested
        inner class DeleteGuests {

            @Test
            fun `deleting a guest that does not exist returns false`() {
                assertFalse(emptyHotel!!.delete(0))
                assertFalse(hotelGuests!!.delete(-1))
                assertFalse(hotelGuests!!.delete(3))
            }

            @Test
            fun `deleting a guest that exists deletes and returns deleted object`() {
                assertEquals(2, hotelGuests!!.numberOfGuests())
                assertTrue(hotelGuests!!.delete(1))
                assertFalse(hotelGuests!!.delete(0))
                assertEquals(1, hotelGuests!!.numberOfGuests())
            }
        }

        @Nested
        inner class UpdateGuests {

            @Test
            fun `updating a  guest that does not exist returns false`() {

                assertFalse(hotelGuests!!.update(6, Guest(6, "Updated Guest", "123456789", "updated@email.com")))
                assertFalse(hotelGuests!!.update(-1, Guest(-1, "Updated Guest", "123456789", "updated@email.com")))
                assertFalse(emptyHotel!!.update(0, Guest(0, "Updated Guest", "123456789", "updated@email.com")))
            }

            @Test
            fun `updating an existing guest returns true and updates`() {
                // Assuming joe is the guest with ID 1
                assertEquals(joe, hotelGuests!!.findGuest(1))
                assertEquals("joe", joe!!.guestName)
                assertEquals("123456789", joe!!.guestPhone)
                assertEquals("joe@example.com", joe!!.guestEmail)

                assertTrue(hotelGuests!!.update(1, Guest(1, "Updated Joe", "Updated Phone", "updated@email.com")))

                val updatedJoe = hotelGuests!!.findGuest(1)
                assertEquals("Updated Joe", updatedJoe!!.guestName)
                assertEquals("Updated Phone", updatedJoe.guestPhone)
                assertEquals("updated@email.com", updatedJoe.guestEmail)
            }
        }


        @Nested
        inner class ArchiveGuests {

            @Test
            fun `archiving a guest that does not exist returns false`() {
                assertFalse(hotelGuests!!.archiveGuest(6))
                assertFalse(hotelGuests!!.archiveGuest(-1))
                assertFalse(emptyHotel!!.archiveGuest(0))
            }

            @Test
            fun `archiving an already archived guest returns false`() {
                assertFalse(hotelGuests!!.findGuest(1)!!.isGuestArchived)
                assertTrue(hotelGuests!!.archiveGuest(1))
            }

            @Test
            fun `archiving an active guest that exists returns true and archives`() {
                // Assuming janeSmith is the guest with ID 1
                assertFalse(hotelGuests!!.findGuest(1)!!.isGuestArchived)
                assertTrue(hotelGuests!!.archiveGuest(1))
                assertFalse(hotelGuests!!.findGuest(1)!!.isGuestArchived)
            }
        }

        @Nested
        inner class AddReservation {
            @Test
            fun `adding a reservation to an existing guest returns true`() {
                val guest = Guest(0, "joe", "123456789", "joe@example.com")
                hotelGuests!!.add(guest)

                val reservation = Reservation(1, 101, 150, 2, true)
                assertTrue(guest.addReservation(reservation))
                assertEquals(1, guest.numberOfReservations())
                assertEquals(reservation, guest.findOne(1))
            }
        }

        @Nested
        inner class UpdateReservation {
            @Test
            fun `updating a reservation in an existing guest returns true`() {
                val guest = Guest(0, "joe", "123456789", "joe@example.com")
                hotelGuests!!.add(guest)

                val reservation = Reservation(1, 101, 150, 2, true)
                guest.addReservation(reservation)

                val updatedReservation = Reservation(1, 102, 200, 3, false)
                assertTrue(guest.update(reservation.ReservationId, updatedReservation))
                assertEquals(1, guest.numberOfReservations())
                assertEquals(updatedReservation, guest.findOne(1))
            }
        }

        @Nested
        inner class DeleteReservation {
            @Test
            fun `deleting a reservation from an existing guest returns true`() {
                val guest = Guest(0, "joe", "123456789", "joe@example.com")
                hotelGuests!!.add(guest)

                val reservation = Reservation(1, 101, 150, 2, true)
                guest.addReservation(reservation)

                assertTrue(guest.delete(reservation.ReservationId))
                assertEquals(0, guest.numberOfReservations())
                assertNull(guest.findOne(1))

            }
        }

        @Nested
        inner class PersistenceTests {
            @Test
            fun `saving and loading an empty collection in XML doesn't crash app`() {
                val storingReservations = GuestAPI(XMLSerializer(File("reservations.xml")))
                storingReservations.store()

                val loadedReservations = GuestAPI(XMLSerializer(File("reservations.xml")))
                loadedReservations.load()

                assertEquals(0, storingReservations.numberOfGuests())
                assertEquals(0, loadedReservations.numberOfGuests())
                assertEquals(storingReservations.numberOfGuests(), loadedReservations.numberOfGuests())
            }

            @Test
            fun `saving and loading a loaded collection in XML doesn't lose data`() {
                val storingReservations = GuestAPI(XMLSerializer(File("reservations.xml")))
                storingReservations.add(Guest(0, "joe", "123456789", "joe@example.com"))

                val reservation = Reservation(1, 101, 150, 2, true)
                storingReservations.findGuest(0)?.addReservation(reservation)

                storingReservations.store()

                val loadedReservations = GuestAPI(XMLSerializer(File("reservations.xml")))
                loadedReservations.load()

                assertEquals(1, storingReservations.numberOfGuests())
                assertEquals(1, loadedReservations.numberOfGuests())
                assertEquals(storingReservations.numberOfGuests(), loadedReservations.numberOfGuests())
                assertEquals(storingReservations.findGuest(0), loadedReservations.findGuest(0))
            }

            @Test
            fun `saving and loading an empty collection in JSON doesn't crash app`() {
                val storingReservations = GuestAPI(JSONSerializer(File("reservations.json")))
                storingReservations.store()

                val loadedReservations = GuestAPI(JSONSerializer(File("reservations.json")))
                loadedReservations.load()

                assertEquals(0, storingReservations.numberOfGuests())
                assertEquals(0, loadedReservations.numberOfGuests())
                assertEquals(storingReservations.numberOfGuests(), loadedReservations.numberOfGuests())
            }

            @Test
            fun `saving and loading a loaded collection in JSON doesn't lose data`() {
                val storingReservations = GuestAPI(JSONSerializer(File("reservations.json")))
                storingReservations.add(Guest(0, "joe", "123456789", "joe@example.com"))

                val reservation = Reservation(1, 101, 150, 2, true)
                storingReservations.findGuest(0)?.addReservation(reservation)

                storingReservations.store()

                val loadedReservations = GuestAPI(JSONSerializer(File("reservations.json")))
                loadedReservations.load()

                assertEquals(1, storingReservations.numberOfGuests())
                assertEquals(1, loadedReservations.numberOfGuests())
                assertEquals(storingReservations.numberOfGuests(), loadedReservations.numberOfGuests())
                assertEquals(storingReservations.findGuest(0), loadedReservations.findGuest(0))
            }

            @Test
            fun `saving and loading an empty collection in YAML doesn't crash app`() {
                val storingReservations = GuestAPI(YAMLSerializer(File("reservations.yaml")))
                storingReservations.store()

                val loadedReservations = GuestAPI(YAMLSerializer(File("reservations.yaml")))
                loadedReservations.load()

                assertEquals(0, storingReservations.numberOfGuests())
                assertEquals(0, loadedReservations.numberOfGuests())
                assertEquals(storingReservations.numberOfGuests(), loadedReservations.numberOfGuests())
            }

            @Test
            fun `saving and loading a loaded collection in YAML doesn't lose data`() {
                val storingReservations = GuestAPI(YAMLSerializer(File("reservations.yaml")))
                storingReservations.add(Guest(0, "joe", "123456789", "joe@example.com"))

                val reservation = Reservation(1, 101, 150, 2, true)
                storingReservations.findGuest(0)?.addReservation(reservation)

                storingReservations.store()

                val loadedReservations = GuestAPI(YAMLSerializer(File("reservations.yaml")))
                loadedReservations.load()

                assertEquals(1, storingReservations.numberOfGuests())
                assertEquals(1, loadedReservations.numberOfGuests())
                assertEquals(storingReservations.numberOfGuests(), loadedReservations.numberOfGuests())
                assertEquals(storingReservations.findGuest(0), loadedReservations.findGuest(0))
            }
        }


        @Nested
        inner class SearchMethods {

            @Test
            fun `search guests by ID returns no guests when no guests with that ID exist`() {

                assertEquals(2, hotelGuests!!.numberOfGuests())
                val searchResults = hotelGuests!!.searchGuestsById(100)
                assertFalse(searchResults.isEmpty())


                assertEquals(2, hotelGuests!!.numberOfGuests())
                assertFalse(hotelGuests!!.searchGuestsById(1).isEmpty())
            }

            @Test
            fun `search guests by ID returns guests when guests with that ID exist`() {
                assertEquals(2, hotelGuests!!.numberOfGuests())

                // Searching a populated collection for an ID that exists
                var searchResults = hotelGuests!!.searchGuestsById(1)
                assertFalse(searchResults.contains("John Doe"))
                assertFalse(searchResults.contains("Jane Doe"))


                searchResults = hotelGuests!!.searchGuestsById(3)
                assertFalse(searchResults.contains("Johnny Bravo"))
                assertFalse(searchResults.contains("Jane Doe"))
            }


        }


    }
