package dk.itu.moapd.scootersharing.lufr.model

import android.util.Log
import com.google.firebase.database.*
import com.google.firebase.database.DatabaseReference
import dk.itu.moapd.scootersharing.lufr.view.MainActivity

import java.util.*

/**
 * RidesDB class. Database class for all scooters in the system.
 * Also holds all methods for scooters.
 */
object RidesDB {
    private val database = MainActivity.getDatabaseReference()
    private val ridesRef: DatabaseReference = database.child("rides")
    private val rides = ArrayList<Scooter>()
    private var timer: Timer? = null
    private var timerValue = 0

    init{
        Log.d("RidesDB", "Database ref: ${RidesDB.database.key}")
        Log.d("RidesDB", "Rides reference: ${RidesDB.ridesRef.key}")
    }

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
    fun startRide(name: String, user: String){
        ridesRef.child(name).child("user").setValue(user)
        rides.last {it.name == name}.user = user
        timerValue = 0
        timer = Timer()
        timer!!.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                timerValue++
                ridesRef.child(name).child("timer").setValue(timerValue)
                rides.last {it.name == name}.timer = timerValue
            }
        }, 0, 999)
    }

    fun endRide(name: String): Int{
        timer?.cancel()
        timer = null
        val timeSpent = rides.last{it.name == name}.timer
        ridesRef.child(name).child("timer").setValue(0)
        rides.last {it.name == name}.timer = 0
        ridesRef.child(name).child("reserved").setValue("")
        rides.last {it.name == name}.reserved = ""
        ridesRef.child(name).child("user").setValue("")
        rides.last {it.name == name}.user = ""

        return timeSpent - 3600
    }

    // Function to add a scooter with given inputs. does not allow for dupes.
    fun addScooter(name: String, location: String, timestamp: Long, lat: Double, long: Double, image: String) : String {
        val scooter = Scooter(name, location, timestamp, lat, long, image, "", "", 0)
        for(s in rides){
            if (s.name == name){
                return "Error: Scooter already exists!"
            }
        }
        ridesRef.child(name).setValue(scooter)
        rides.add(scooter)
        return "Successfully added scooter: ${scooter.name} at ${scooter.location}, at ${scooter.getFormatTimestamp()}"
    }

    fun updateScooter(name: String, location: String, timestamp: Long, lat: Double, long: Double, image: String = ""){
        val currentScooter = getScooter(name)
        ridesRef.child(currentScooter.name).child("location").setValue(location)
        ridesRef.child(currentScooter.name).child("timestamp").setValue(timestamp)
        ridesRef.child(currentScooter.name).child("lat").setValue(lat)
        ridesRef.child(currentScooter.name).child("long").setValue(long)
        if(image.isNotEmpty()){
            ridesRef.child(currentScooter.name).child("image").setValue(image)
        }
    }

    fun getScooter(name: String): Scooter{
        return rides.last {it.name == name}
    }

    fun updateScooterImage(name: String, imageName: String) {
        ridesRef.child(name).child("image").setValue(imageName)
        rides.last {it.name == name}.image = imageName
    }

    fun reserveScooter(name: String, user: String): String{
        ridesRef.child(name).child("reserved").setValue(user)
        rides.last {it.name == name}.reserved = user
        return "Successfully reserved scooter: $name"
    }

    fun cancelReservation(name: String, user: String): String{
        val scooter = rides.last{it.name == name}
        if (scooter.reserved == user){
            rides.last {it.name == name}.reserved = ""
            ridesRef.child(name).child("reserved").setValue("")
            return "Successfully cancelled the reservation of $name"
        }else{
            return "This scooter is not reserved by you!"
        }
    }

    fun deleteScooter(name: String) {
        ridesRef.child(name).removeValue()
        rides.removeIf{it.name == name}
    }
}