package dk.itu.moapd.scootersharing.lufr.controller

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.scootersharing.lufr.R
import dk.itu.moapd.scootersharing.lufr.databinding.FragmentPaymentBinding

class PaymentFragment : Fragment() {
    companion object {
        private val TAG = PaymentFragment::class.qualifiedName
    }

    private lateinit var binding: FragmentPaymentBinding
    private lateinit var bottomNav: BottomNavigationView

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
    ): View? {
        binding = FragmentPaymentBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            applyButton.setOnClickListener {
                var creds = EmailAuthProvider.getCredential(
                    user.email!!,
                    editTextPassword.text.toString()
                )
                user.reauthenticate(creds)
                    .addOnCompleteListener() { task ->
                        if (task.isSuccessful) {
                            //authentication successful, update password
                            Log.d(SignupFragment.TAG, "updateInformation:success")
                            Toast.makeText(
                                context, "Sucessfully updated information",
                                Toast.LENGTH_SHORT
                            ).show()

                            //TODO ADD CARD INFO TO DB HERE

                            val fragment = MyRidesFragment()
                            requireActivity().supportFragmentManager.beginTransaction()
                                .replace(R.id.fragment_container, fragment)
                                .addToBackStack(null)
                                .commit()

                        } else {
                            // if authentication fails, notify the user
                            Log.w(SignupFragment.TAG, "updateInformation:failure", task.exception)
                            Toast.makeText(
                                context, "Wrong password.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
            logoutButton.setOnClickListener {
                val fragment = WelcomeFragment()
                auth.signOut()
                Toast.makeText(context, "Successfully logged out",
                    Toast.LENGTH_LONG).show()
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
        bottomNav = view.findViewById(R.id.bottom_navigation) as BottomNavigationView
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.start_nav_button -> {
                    loadFragment(Start_Ride_Fragment())
                    true
                }
                R.id.update_nav_button -> {
                    loadFragment(Update_Ride_Fragment())
                    true
                }
                R.id.all_rides_nav_button -> {
                    loadFragment((AllRidesFragment()))
                    true
                }
                R.id.my_rides_nav_button -> {
                    loadFragment(MyRidesFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment){
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}