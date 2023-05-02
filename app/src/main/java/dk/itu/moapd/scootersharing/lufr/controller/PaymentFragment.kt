package dk.itu.moapd.scootersharing.lufr.controller

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import dk.itu.moapd.scootersharing.lufr.model.UsersDB
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

        binding.editTextUser.text = user.email

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
                            if (
                                editTextCardNumber.length() != 16
                                || editTextCvc.length() != 3
                                || editTextExpDate.length() != 5
                            ) {
                                (activity as MainActivity).showToast("ERROR: Invalid card information")
                            } else {
                                UsersDB.updatePaymentInfo(
                                    email = user.email!!,
                                    cardNumber = editTextCardNumber.text.toString()
                                        .toLong(),
                                    cvc = editTextCvc.text.toString().toInt(),
                                    exp = editTextExpDate.text.toString()
                                )

                                (activity as MainActivity).showToast("Successfully updated card information")
                                (activity as MainActivity).setCurrentFragment(MapsFragment())
                            }
                        } else {
                            // if authentication fails, notify the user
                            Log.w(SignupFragment.TAG, "updateInformation:failure", task.exception)
                            (activity as MainActivity).showToast("ERROR: Wrong password")
                        }
                    }
            }
            deleteInfoButton.setOnClickListener {
                if(editTextPassword.text.toString() != ""){
                    val credentials = EmailAuthProvider.getCredential(
                        user.email!!,
                        editTextPassword.text.toString()
                    )
                    user.reauthenticate(credentials)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                UsersDB.deletePaymentInfo(user.email!!)
                                (activity as MainActivity).showToast("Successfully deleted card information")
                                (activity as MainActivity).setCurrentFragment(MapsFragment())
                            } else {
                                (activity as MainActivity).showToast("ERROR: Wrong password")
                            }
                        }
                }else{
                    (activity as MainActivity).showToast("ERROR: Enter your password to make changes!")
                }
            }
            logoutButton.setOnClickListener {
                auth.signOut()
                (activity as MainActivity).showToast("Successfully logged out")
                (activity as MainActivity).setCurrentFragment(WelcomeFragment())
            }
        }
    }
}