package dk.itu.moapd.scootersharing.lufr

import android.content.Context
import android.util.Log
import com.google.firebase.database.*
import com.google.firebase.database.DatabaseReference

import dk.itu.moapd.scootersharing.lufr.model.Scooter
import java.util.*

/**
 * RidesDB class. Database class for all scooters in the system.
 * Also holds all methods for scooters.
 */
import com.google.firebase.database.*
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CountDownLatch

class RidesDB {

    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("rides")

    fun addScooter(name: String, location: String, timestamp: Long) : String {
        val scooter = Scooter(name, location, timestamp)
        databaseReference.child(name).setValue(scooter)
        return "Succesfully added scooter: $name"
    }

    fun updateCurrentScooter(name: String, location: String, timestamp: Long) {
        val scooter = Scooter(name, location, timestamp)
        databaseReference.child(name).setValue(scooter)
    }

    fun deleteRide(name: String) {
        databaseReference.child(name).removeValue()
    }

    fun getRide(scooterId: String, valueEventListener: ValueEventListener) {
        databaseReference.child(scooterId).addListenerForSingleValueEvent(valueEventListener)
    }

    fun getRides(valueEventListener: ValueEventListener) {
        databaseReference.addListenerForSingleValueEvent(valueEventListener)
    }

    fun getCurrentScooter(): Scooter? {
        var latestRide: Scooter? = null
        val latch = CountDownLatch(1)
        databaseReference.limitToLast(1).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (rideSnapshot in dataSnapshot.children) {
                    latestRide = rideSnapshot.getValue(Scooter::class.java)
                }
                latch.countDown()
            }
            override fun onCancelled(databaseError: DatabaseError) {
                latch.countDown()
            }
        })
        latch.await()
        return latestRide
    }

    fun getRidesList(): List<Scooter> {
        val ridesList = ArrayList<Scooter>()
        getRides(object : ValueEventListener{
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (rideSnapshot in dataSnapshot.children) {
                val ride = rideSnapshot.getValue(Scooter::class.java)
                if (ride != null) {
                    ridesList.add(ride)
                }
            }
        }
        override fun onCancelled(databaseError: DatabaseError) {
            Log.e("RidesDB", "Error getting rides", databaseError.toException())
        }
        })
        return ridesList
    }
    /**
     * Generate a random timestamp in the last 365 days .
     * @return A random timestamp in the last year .
     */
    private fun randomDate(): Long {
        val random = Random()
        val now = System.currentTimeMillis()
        val year = random.nextDouble() * 1000 * 60 * 60 * 24 * 365
        return (now - year).toLong()
    }
}