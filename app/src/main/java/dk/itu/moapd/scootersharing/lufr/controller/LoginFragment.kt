package dk.itu.moapd.scootersharing.lufr.controller

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.scootersharing.lufr.R
import dk.itu.moapd.scootersharing.lufr.databinding.FragmentLoginBinding
import com.google.firebase.auth.ktx.auth
import dk.itu.moapd.scootersharing.lufr.view.MainActivity


class LoginFragment : Fragment() {
    private lateinit var auth: FirebaseAuth

    companion object {
        private val TAG = LoginFragment::class.qualifiedName
    }

    private lateinit var binding: FragmentLoginBinding
    private lateinit var bottomNavBar: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            reload()
        }
    }

    private fun signIn(email: String, password: String) {
        // [START sign_in_with_email]
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this.requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    (activity as MainActivity).showToast("Successfully logged in")
                    sendEmailVerification()
                    (activity as MainActivity).setCurrentFragment(MapsFragment())
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    (activity as MainActivity).showToast("ERROR: Wrong email or password")
                }
            }
        // [END sign_in_with_email]
    }

    private fun sendEmailVerification() {
        // [START send_email_verification]
        val user = auth.currentUser!!
        user.sendEmailVerification()
            .addOnCompleteListener(this.requireActivity()) {
            }
    }

    private fun reload() {

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomNavBar = requireActivity().findViewById(R.id.bottomNavigationView)
        bottomNavBar.visibility = View.GONE

        binding.apply {
            loginButton.setOnClickListener {
                val check = checkCredentials(
                    editTextEmail.text.toString().trim(),
                    editTextPassword.text.toString().trim()
                )
                if (check == "true") {
                    signIn(
                        editTextEmail.text.toString().trim(),
                        editTextPassword.text.toString().trim()
                    )
                } else {
                    Toast.makeText(
                        context, check,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            signUpButton.setOnClickListener{
                (activity as MainActivity).setCurrentFragment(SignupFragment())
            }
        }
    }

    private fun checkCredentials(email: String, password: String): String {
        val check: String
        if (!email.contains('@') || !email.contains('.') || email.length < 8) {
                check = "Email address not valid"
            } else if (password.length < 6) {
            check = "Incorrect password"
        } else {
            check = "true"
        }
        return check
    }
}