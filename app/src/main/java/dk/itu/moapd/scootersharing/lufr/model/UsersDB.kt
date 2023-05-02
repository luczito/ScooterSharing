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

    fun addUser(email: String){
        val userKey = usersRef.push().key
        getUserKey(email) {
            foundKey ->
            if(foundKey == null){
                if(userKey != null){
                    usersRef.child(userKey).child("email").setValue(email)
                    Log.d("UsersDB", "Successfully added user $userKey to the UsersDB")
                }else{
                    Log.e("UsersDB", "User key not generated and therefore null")
                }
            }else{
                Log.e("UsersDB", "ERROR: email already exists")
            }
        }
    }

    fun updatePaymentInfo(email: String, cardNumber: Int, cvc: Int, exp: String){
        getUserKey(email) {
            userKey ->
            if(userKey != null){
                usersRef.child(userKey).child("card").setValue(cardNumber)
                usersRef.child(userKey).child("cvc").setValue(cvc)
                usersRef.child(userKey).child("exp").setValue(exp)
                Log.d("UsersDB", "Successfully updated card info for $email")
            }else{
                Log.e("UsersDB", "User with email $email not found")
            }
        }
    }

    fun updateUserInfo(email: String, newEmail: String){
        getUserKey(email){
            userKey ->
            if(userKey != null){
                usersRef.child(userKey).child("email").setValue(newEmail)
                Log.d("UsersDB", "Successfully updated email to $newEmail")
            }else{
                Log.e("UsersDB", "User with email $email not found!")
            }
        }
        }

    fun deletePaymentInfo(email: String){
        getUserKey(email){
                userKey ->
            if(userKey != null) {
                usersRef.child(userKey).child("card").removeValue()
                usersRef.child(userKey).child("cvc").removeValue()
                usersRef.child(userKey).child("exp").removeValue()
                Log.d("UserDB", "Successfully removed card info for user $email")
            }else{
                Log.e("UsersDB", "User with email $email not found!")
            }
        }
    }

    private fun getUserKey(email: String, callback: (String?) -> Unit) {
        val query = database.child("users").orderByChild("email").equalTo(email)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var userkey: String? = null
                if (dataSnapshot.exists()) {
                    for (userSnapshot in dataSnapshot.children) {
                        val userKey = userSnapshot.key
                        if (userKey != null) {
                            userkey = userKey
                            break
                        }
                    }
                }
                callback(userkey)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("UsersDB", "getUserKey() error: $databaseError")
                callback(null)
            }
        })
    }
}