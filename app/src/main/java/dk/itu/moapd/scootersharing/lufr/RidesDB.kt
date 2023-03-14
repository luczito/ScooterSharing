package dk.itu.moapd.scootersharing.lufr

import android.content.Context
import java.sql.Timestamp
import java.util.Random
import kotlin.collections.ArrayList

/**
 * RidesDB class. Database class for all scooters in the system.
 * Also holds all methods for scooters.
 * @property rides, List of all scooters in the system.
 */
class RidesDB private constructor (context:Context) {
    private val rides = ArrayList<Scooter>()

    companion object : RidesDBHolder<RidesDB, Context>(::RidesDB)
    //init method adds 3 default scooters
    init {
        rides.add(
            Scooter(
                name = "CPH001",
                location = "ITU",
                timestamp = randomDate()
            )
        )
        rides.add(
            Scooter(
                name = "CPH002",
                location = "Fields",
                timestamp = randomDate()
            )
        )
        rides.add(
            Scooter(
                name = "CPH003",
                location = "Lufthavn",
                timestamp = randomDate()
            )
        )
    }
    //getRidesList function, returns all scooters in the rides list.
    fun getRidesList():List <Scooter> {
        return rides
    }
    //Function to add a scooter with given inputs. does not allow for dupes.
    fun addScooter(name: String, location: String, timestamp: Long):String {
        var returnString = ""
        var found = false
        for(s in rides) {
            if (s.name == name) found = true
        }
        returnString = if(found) "Scooter with name: $name, already exists!"
        else {
            rides.add(Scooter(name, location, timestamp))
            "[$timestamp] - Ride on scooter: '$name', started at location: '$location'."
        }
        return returnString
    }
    //updates current scooter with a new location and timestamp
    fun updateCurrentScooter(location:String, timestamp:Long) {
        getCurrentScooter().location = location
        getCurrentScooter().timestamp = timestamp
    }
    //returns the current scooter (the newest in the list)
    fun getCurrentScooter():Scooter{
        return rides[rides.size - 1]
    }
    //returns the current scooters info
    fun getCurrentScooterInfo():String {
        return getCurrentScooter().toString()
    }

    fun deleteScooter(name:String):String{
        rides.removeIf{it.name == name}
        return "Removed scooter: $name"
    }
    /**
     * Generate a random timestamp in the last 365 days .
     * @return A random timestamp in the last year .
     */
    private fun randomDate():Long {
        val random = Random()
        val now = System.currentTimeMillis()
        val year = random.nextDouble()*1000 * 60 * 60 * 24 * 365
        return (now-year).toLong()
    }
}

open class RidesDBHolder<out T : Any, in A>(creator: (A) -> T) {
    private var creator: ((A) -> T)? = creator

    @Volatile
    private var instance: T? = null
    fun get(arg: A): T {
        val checkInstance = instance
        if (checkInstance != null)
            return checkInstance
        return synchronized(this) {
            val checkInstanceAgain = instance
            if (checkInstanceAgain != null)
                checkInstanceAgain
            else {
                val created = creator!!(arg)
                instance = created
                creator = null
                created
            }
        }
    }
}