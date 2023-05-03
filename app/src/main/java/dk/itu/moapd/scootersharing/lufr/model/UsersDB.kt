package dk.itu.moapd.scootersharing.lufr.model

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import dk.itu.moapd.scootersharing.lufr.view.MainActivity

object UsersDB {

    private val database = MainActivity.getDatabaseReference()
    private val usersRef: DatabaseReference = database.child("users")

    fun addUser(email: String) {
        val userKey = usersRef.push().key
        getUserKey(email) { foundKey ->
            if (foundKey == null) {
                if (userKey != null) {
                    usersRef.child(userKey).child("email").setValue(email)
                    Log.d("UsersDB", "Successfully added user $userKey to the UsersDB")
                } else {
                    Log.e("UsersDB", "User key not generated and therefore null")
                }
            } else {
                Log.e("UsersDB", "ERROR: email already exists")
            }
        }
    }

    fun updatePaymentInfo(email: String, cardNumber: Long, cvc: Int, exp: String) {
        getUserKey(email) { userKey ->
            if (userKey != null) {
                usersRef.child(userKey).child("card").setValue(cardNumber)
                usersRef.child(userKey).child("cvc").setValue(cvc)
                usersRef.child(userKey).child("exp").setValue(exp)
                Log.d("UsersDB", "Successfully updated card info for $email")
            } else {
                Log.e("UsersDB", "User with email $email not found")
            }
        }
    }

    fun updateUserInfo(email: String, newEmail: String): Boolean {
        var success = false
        getUserKey(email) { userKey ->
            if (userKey != null) {
                usersRef.child(userKey).child("email").setValue(newEmail)
                Log.d("UsersDB", "Successfully updated email to $newEmail")
                success = true
            } else {
                Log.e("UsersDB", "User with email $email not found!")
                success = false
            }
        }
        return success
    }

    fun deletePaymentInfo(email: String) {
        getUserKey(email) { userKey ->
            if (userKey != null) {
                usersRef.child(userKey).child("card").removeValue()
                usersRef.child(userKey).child("cvc").removeValue()
                usersRef.child(userKey).child("exp").removeValue()
                Log.d("UserDB", "Successfully removed card info for user $email")
            } else {
                Log.e("UsersDB", "User with email $email not found!")
            }
        }
    }

    private fun getUserKey(email: String, callback: (String?) -> Unit) {
        val query = database.child("users").orderByChild("email").equalTo(email)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var foundKey: String? = null
                if (dataSnapshot.exists()) {
                    for (userSnapshot in dataSnapshot.children) {
                        val userKey = userSnapshot.key
                        if (userKey != null) {
                            foundKey = userKey
                            break
                        }
                    }
                }
                callback(foundKey)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("UsersDB", "getUserKey() error: $databaseError")
                callback(null)
            }
        })
    }

    fun checkCardIsAdded(email: String, completionHandler: (Boolean) -> Unit) {
        getUserKey(email) { userKey ->
            if (userKey != null) {
                usersRef.child(userKey).child("card")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            completionHandler(dataSnapshot.exists())
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            // Handle error
                        }
                    })
            }
        }
    }

    fun addRide(email: String, ride: PreviousRide) {
        getUserKey(email) { userKey ->
            if (userKey != null) {
                val key = usersRef.child(userKey).child("rides").push().key
                usersRef.child(userKey).child("rides").child(key!!).setValue(ride)
            }
        }
    }

    fun getMyRides(email: String, completionHandler: (List<PreviousRide>) -> Unit) {
        getUserKey(email) { userKey ->
            if (userKey != null) {
                val usersRef = usersRef.child(userKey).child("rides")
                usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val rides = mutableListOf<PreviousRide>()
                        for (rideSnapshot in dataSnapshot.children) {
                            val ride = rideSnapshot.getValue(PreviousRide::class.java)
                            ride?.let { rides.add(it) }
                        }
                        Log.d("UsersDB", "Completed loading rides $rides")
                        completionHandler(rides)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.e("UsersDB", "ERROR: Couldn't load rides from DB")
                    }
                })
            }
        }
    }

    fun getCardInfo(email: String, callback: (Long?, Int?, String?) -> Unit) {
        getUserKey(email) { userKey ->
            if (userKey != null) {
                usersRef.child(userKey).get().addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        val card = snapshot.child("card").getValue(Long::class.java)
                        val cvc = snapshot.child("cvc").getValue(Int::class.java)
                        val exp = snapshot.child("exp").getValue(String::class.java)

                        callback(card, cvc, exp)
                    } else {
                        callback(null, null, null)
                    }
                }.addOnFailureListener {
                    callback(null, null, null)
                }
            }
        }
    }
}