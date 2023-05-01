package dk.itu.moapd.scootersharing.lufr.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import dk.itu.moapd.scootersharing.lufr.R
import dk.itu.moapd.scootersharing.lufr.databinding.FragmentWelcomeBinding
import dk.itu.moapd.scootersharing.lufr.view.MainActivity


class WelcomeFragment : Fragment() {
    companion object {
        private val TAG = WelcomeFragment::class.qualifiedName
    }

    private lateinit var binding: FragmentWelcomeBinding
    private lateinit var bottomNavBar: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View? {
        binding = FragmentWelcomeBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomNavBar = requireActivity().findViewById(R.id.bottomNavigationView)
        bottomNavBar.visibility = View.GONE

        binding.apply {
            loginButton.setOnClickListener {
                (activity as MainActivity).setCurrentFragment(LoginFragment())
            }
            signupButton.setOnClickListener {
                (activity as MainActivity).setCurrentFragment(SignupFragment())
            }
        }
    }
}