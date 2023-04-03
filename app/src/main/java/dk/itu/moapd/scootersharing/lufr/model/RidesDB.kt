package dk.itu.moapd.scootersharing.lufr

import android.content.Context
import com.google.firebase.database.*
import com.google.firebase.database.DatabaseReference

import dk.itu.moapd.scootersharing.lufr.model.Scooter
import java.util.*

/**
 * RidesDB class. Database class for all scooters in the system.
 * Also holds all methods for scooters.
 */
class RidesDB(context: Context) {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val ridesRef: DatabaseReference = database.child("rides")
    private val rides = ArrayList<Scooter>()

    init {
        // add a listener to the "rides" node to keep the local list up-to-date
        ridesRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val scooter = snapshot.getValue(Scooter::class.java)
                rides.add(scooter!!)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val updatedScooter = snapshot.getValue(Scooter::class.java)
                val index = rides.indexOfFirst { it.name == updatedScooter!!.name }
                if (index >= 0) {
                    rides[index] = updatedScooter!!
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val removedScooter = snapshot.getValue(Scooter::class.java)
                rides.removeIf { it.name == removedScooter!!.name }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // getRidesList function, returns all scooters in the rides list.
    fun getRidesList(): List<Scooter> {
        return rides
    }

    // Function to add a scooter with given inputs. does not allow for dupes.
    fun addScooter(name: String, location: String, timestamp: Long) : String {
        val scooter = Scooter(name, location, timestamp)
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
        ridesRef.child(currentScooter!!.name).child("timestamp").setValue(timestamp)
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