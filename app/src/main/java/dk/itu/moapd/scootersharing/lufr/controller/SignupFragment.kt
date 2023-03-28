package dk.itu.moapd.scootersharing.lufr.controller

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.scootersharing.lufr.R
import dk.itu.moapd.scootersharing.lufr.databinding.FragmentSignupBinding

class SignupFragment : Fragment() {
    private lateinit var auth: FirebaseAuth

    companion object {
        private val TAG = SignupFragment::class.qualifiedName
    }

    private lateinit var binding: FragmentSignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View? {
        binding = FragmentSignupBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            signupButton.setOnClickListener {
                var check = checkCredentials(editTextEmail.text.toString().trim(),
                    editTextPassword.text.toString().trim(),
                    editTextConfirmPassword.text.toString().trim()
                )
                if(check == "true"){
                    createAccount(editTextEmail.text.toString().trim(), editTextPassword.text.toString().trim())
                }else{
                    Toast.makeText(context, check,
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun createAccount(email: String, password: String) {
        // [START create_user_with_email]
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this.requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    Toast.makeText(context, "Succesfully signed up",
                        Toast.LENGTH_SHORT).show()
                    sendEmailVerification()
                    updateUI(user!!)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(context, "Unable to sign up",
                        Toast.LENGTH_SHORT).show()
                }
            }
        // [END create_user_with_email]
    }

    private fun sendEmailVerification() {
        // [START send_email_verification]
        val user = auth.currentUser!!
        user.sendEmailVerification()
            .addOnCompleteListener(this.requireActivity()) { task ->
                // Email Verification sent
            }
        // [END send_email_verification]
    }

    private fun updateUI(user: FirebaseUser) {
        val fragment = LoginFragment()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun reload() {

    }

    fun checkCredentials(email: String, password: String, confPassword: String) : String {
        var check: String
        if (!email.contains('@') || !email.contains('.')) {
            check = "Email address not valid"
        }
        else if (password.length < 6) {
            check = "Password needs to be 6 or more characters"
        }
        else if (password != confPassword){
            check = "Passwords do not match"
        }
        else {
            check = "true"
        }
        return check
    }
}