package dk.itu.moapd.scootersharing.lufr.model

import android.util.Log
import com.google.firebase.database.*
import com.google.firebase.database.DatabaseReference

import java.util.*

/**
 * RidesDB class. Database class for all scooters in the system.
 * Also holds all methods for scooters.
 */
object RidesDB {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val ridesRef: DatabaseReference = database.child("rides")
    private val rides = ArrayList<Scooter>()

    fun initialize(completion: () -> Unit) {
        // add a listener to the "rides" node to keep the local list up-to-date
        ridesRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val updatedRides = ArrayList<Scooter>()
                for(child in snapshot.children){
                    val scooter = child.getValue(Scooter::class.java) ?: continue
                    updatedRides.add(scooter)
                }
                rides.clear()
                rides.addAll(updatedRides)
                completion()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle cancelled event
                Log.e("RidesDB", "Failed to retrieve ride IDs from database", error.toException())
            }
        })
    }

    // getRidesList function, returns all scooters in the rides list.
    fun getRidesList(): List<Scooter> {
        return rides
    }

    fun getRidesAsCards() : List<Card>{
        var card: Card
        val cardList = ArrayList<Card>()
        for(ride in rides){
            card = Card(ride.name, ride.location, ride.getFormatTimestamp(), 0)
            cardList.add(card)
        }
        return cardList
    }

    // Function to add a scooter with given inputs. does not allow for dupes.
    fun addScooter(name: String, location: String, timestamp: Long, lat: Double, long: Double, image: String) : String {
        val scooter = Scooter(name, location, timestamp, lat, long, image)
        for(s in rides){
            if (s.name == name){
                return "Error: Scooter already exists!"
            }
        }
        ridesRef.child(name).setValue(scooter)
        rides.add(scooter)
        return "Successfully added scooter: ${scooter.name} at ${scooter.location}, at ${scooter.getFormatTimestamp()}"
    }

    // updates current scooter with a new location and timestamp
    fun updateCurrentScooter(location: String, timestamp: Long): String {
        val currentScooter = getCurrentScooter()
        ridesRef.child(currentScooter!!.name).child("location").setValue(location)
        ridesRef.child(currentScooter.name).child("timestamp").setValue(timestamp)
        rides[rides.size-1].location = location
        rides[rides.size-1].timestamp = timestamp
        return "Updated scooter: ${currentScooter.name} with location: $location, at ${rides[rides.size-1].getFormatTimestamp()}"
    }

    // retrieves the last scooter from the ridesRef database reference
    fun getCurrentScooter(): Scooter? {
       return rides.lastOrNull()
    }

    // returns the current scooters info
    fun getCurrentScooterInfo(): String {
        return getCurrentScooter().toString()
    }

    fun deleteScooter(name: String) {
        ridesRef.child(name).removeValue()
        rides.removeIf{it.name == name}
    }
}