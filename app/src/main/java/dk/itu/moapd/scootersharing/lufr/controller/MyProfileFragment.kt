package dk.itu.moapd.scootersharing.lufr.controller

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.scootersharing.lufr.R
import dk.itu.moapd.scootersharing.lufr.databinding.FragmentMyProfileBinding
import dk.itu.moapd.scootersharing.lufr.model.UsersDB
import dk.itu.moapd.scootersharing.lufr.view.MainActivity

class MyProfileFragment : Fragment() {
    private lateinit var binding: FragmentMyProfileBinding
    private lateinit var bottomNavBar: BottomNavigationView
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
        super.onCreate(savedInstanceState)
        user = Firebase.auth.currentUser!!
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyProfileBinding.inflate(layoutInflater, container, false)

        bottomNavBar = requireActivity().findViewById(R.id.bottomNavigationView)
        bottomNavBar.visibility = View.VISIBLE
        bottomNavBar.selectedItemId = R.id.profile_nav_button

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.editTextUser.text = user.email

        binding.apply {
            paymentSettingsButton.setOnClickListener {
                (activity as MainActivity).setCurrentFragment(PaymentFragment())
            }
            logoutButton.setOnClickListener {
                auth.signOut()
                (activity as MainActivity).showToast("Successfully logged out")
                (activity as MainActivity).setCurrentFragment(WelcomeFragment())
            }
            applyButton.setOnClickListener {
                var credentials: AuthCredential? = null
                if (!editTextConfirmPassword.text.isNullOrBlank()) {
                    credentials = EmailAuthProvider.getCredential(
                        user.email!!,
                        editTextConfirmPassword.text.toString()
                    )
                    user.reauthenticate(credentials)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                if (!editTextChangePassword.text.isNullOrBlank()){
                                    user.updatePassword(editTextChangePassword.text.toString())
                                }
                                if (!editTextChangeEmail.text.isNullOrBlank()) {
                                    user.updateEmail(editTextChangeEmail.text.toString())
                                    if(UsersDB.updateUserInfo(user.email!!, editTextChangeEmail.text.toString())){
                                        Log.d(SignupFragment.TAG, "updateInformation:success")
                                        (activity as MainActivity).showToast("Successfully updated user information")
                                        (activity as MainActivity).setCurrentFragment(MyRidesFragment())
                                    }else{
                                        Log.e(SignupFragment.TAG, "ERROR: new email is in use or invalid")
                                        (activity as MainActivity).showToast("ERROR: new email is in use or invalid")
                                    }
                                }
                            } else {
                                // if authentication fails, notify the user
                                Log.w(SignupFragment.TAG, "updateInformation:failure", task.exception)
                                (activity as MainActivity).showToast("ERROR: Wrong password")
                            }
                        }
                }else if(editTextChangeEmail.text.isNullOrBlank() && editTextChangePassword.text.isNullOrBlank()){
                    (activity as MainActivity).showToast("ERROR: No valid information given")
                }
            }
        }
    }
}