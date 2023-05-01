package dk.itu.moapd.scootersharing.lufr.model

import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

object UsersDB {
    private val usersRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")

    init {
        Log.d("UsersDB", "Users reference: ${usersRef.key}")
    }

    fun addUser(email: String){
        val userKey = usersRef.push().key
        usersRef.child(userKey!!).child("email").setValue(email)
        Log.d("UsersDB", "Successfully added user $userKey to the UsersDB")
    }

    fun updatePaymentInfo(email: String, cardNumber: Int, cvc: Int, exp: String){
        val username = email.replace(".", "").replace("@", "")
        usersRef.child(username).child("card").setValue(cardNumber)
        usersRef.child(username).child("cvc").setValue(cvc)
        usersRef.child(username).child("exp").setValue(exp)
    }

    fun updateUserInfo(email: String, newEmail: String){
        val username = email.replace(".", "").replace("@", "")
        val newUsername = newEmail.replace(".", "").replace("@", "")
        usersRef.child(username).setValue(newUsername)
    }

    fun deletePaymentInfo(email: String){
        val username = email.replace(".", "").replace("@", "")
        usersRef.child(username).child("card").removeValue()
        usersRef.child(username).child("cvc").removeValue()
        usersRef.child(username).child("exp").removeValue()
    }
}