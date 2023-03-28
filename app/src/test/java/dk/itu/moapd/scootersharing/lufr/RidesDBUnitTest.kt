package dk.itu.moapd.scootersharing.lufr

import dk.itu.moapd.scootersharing.lufr.controller.MainFragment
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class RidesDBUnitTest {

    @Test
    fun testGetRidesList() {
        var ridesDB = MainFragment.ridesDB

        val ridesList = ridesDB.getRidesList()
        assertNotNull(ridesList)
        assertEquals(3, ridesList.size)
    }

    @Test
    fun testAddScooter() {
        var ridesDB = MainFragment.ridesDB

        val returnString = ridesDB.addScooter("CPH004", "Tivoli", 1234567890)
        assertEquals("[1234567890] - Ride on scooter: 'CPH004', started at location: 'Tivoli'.", returnString)
        assertEquals(4, ridesDB.getRidesList().size)

        val duplicateString = ridesDB.addScooter("CPH004", "Nyhavn", 1234567890)
        assertEquals("Scooter with name: CPH004, already exists!", duplicateString)
        assertEquals(4, ridesDB.getRidesList().size)
    }

    @Test
    fun testUpdateCurrentScooter() {
        var ridesDB = MainFragment.ridesDB

        ridesDB.updateCurrentScooter("Nyhavn", 1234567890)
        val currentScooter = ridesDB.getCurrentScooter()
        assertEquals("CPH003", currentScooter.name)
        assertEquals("Nyhavn", currentScooter.location)
        assertEquals(1234567890, currentScooter.timestamp)
    }

    @Test
    fun testGetCurrentScooter() {
        var ridesDB = MainFragment.ridesDB

        val currentScooter = ridesDB.getCurrentScooter()
        assertEquals("CPH003", currentScooter.name)
        assertEquals("Lufthavn", currentScooter.location)
    }

    @Test
    fun testDeleteScooter() {
        var ridesDB = MainFragment.ridesDB

        val returnString = ridesDB.deleteScooter("CPH001")
        assertEquals("Removed scooter: CPH001", returnString)
        assertEquals(2, ridesDB.getRidesList().size)

        val nonExistentString = ridesDB.deleteScooter("CPH001")
        assertEquals("Removed scooter: ", nonExistentString)
        assertEquals(2, ridesDB.getRidesList().size)
    }
}