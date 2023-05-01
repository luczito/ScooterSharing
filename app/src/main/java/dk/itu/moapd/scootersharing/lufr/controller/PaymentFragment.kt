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
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.scootersharing.lufr.R
import dk.itu.moapd.scootersharing.lufr.databinding.FragmentPaymentBinding
import dk.itu.moapd.scootersharing.lufr.view.MainActivity

class PaymentFragment : Fragment() {
    companion object {
        private val TAG = PaymentFragment::class.qualifiedName
    }

    private lateinit var binding: FragmentPaymentBinding
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
        binding = FragmentPaymentBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomNavBar = requireActivity().findViewById(R.id.bottomNavigationView)
        bottomNavBar.visibility = View.VISIBLE

        binding.apply {
            applyButton.setOnClickListener {
                val credentials = EmailAuthProvider.getCredential(
                    user.email!!,
                    editTextPassword.text.toString()
                )
                user.reauthenticate(credentials)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            //authentication successful, update password
                            Log.d(SignupFragment.TAG, "updateInformation:success")
                            (activity as MainActivity).showToast("Successfully updated information")

                            //TODO ADD CARD INFO TO DB HERE

                            (activity as MainActivity).setCurrentFragment(MapsFragment())

                        } else {
                            // if authentication fails, notify the user
                            Log.w(SignupFragment.TAG, "updateInformation:failure", task.exception)
                            (activity as MainActivity).showToast("ERROR: Wrong password")
                        }
                    }
            }
            logoutButton.setOnClickListener {
                auth.signOut()
                (activity as MainActivity).showToast("Successfully logged out")
                (activity as MainActivity).setCurrentFragment(WelcomeFragment())
            }
            settingsButton.setOnClickListener{
                (activity as MainActivity).setCurrentFragment(SettingsFragment())
            }
        }
    }
}