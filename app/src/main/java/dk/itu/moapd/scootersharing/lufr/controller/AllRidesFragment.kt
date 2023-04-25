package dk.itu.moapd.scootersharing.lufr.controller

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dk.itu.moapd.scootersharing.lufr.R
import dk.itu.moapd.scootersharing.lufr.model.RidesDB
import dk.itu.moapd.scootersharing.lufr.databinding.FragmentAllRidesBinding

class AllRidesFragment : Fragment() {
    companion object {
        private val TAG = AllRidesFragment::class.qualifiedName
    }
    private lateinit var binding: FragmentAllRidesBinding
    private lateinit var bottomNavBar: BottomNavigationView

    private lateinit var recyclerView: RecyclerView

    private lateinit var user: FirebaseUser
    private lateinit var auth: FirebaseAuth

    /**
     * Default onCreate function.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
        super.onCreate(savedInstanceState)

        // Singleton to share an object between the app activities .
        RidesDB.initialize {
            Log.d("RidesDB", "Data is fully loaded")
        }

        user = Firebase.auth.currentUser!!
        auth = Firebase.auth
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View? {
        binding = FragmentAllRidesBinding.inflate(layoutInflater, container, false)

        recyclerView = binding.recyclerView
        recyclerView.adapter = CardAdapter(RidesDB.getRidesAsCards())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        return binding.root
    }
    /**
     * onCreateView function which inflates the binding, and holds the functionality for the 3 buttons.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        bottomNavBar = requireActivity().findViewById(R.id.bottomNavigationView)
        bottomNavBar.visibility = View.VISIBLE

//        binding.apply {
//
//            logoutButton.setOnClickListener {
//                auth.signOut()
//                Toast.makeText(context, "Successfully logged out",
//                    Toast.LENGTH_LONG).show()
//                loadFragment(WelcomeFragment())
//            }
//            settingsButton.setOnClickListener{
//                loadFragment(SettingsFragment())
//            }
//            allRidesButton.setOnClickListener{
//                loadFragment(AllRidesFragment())
//            }
//            myRidesButton.setOnClickListener{
//                loadFragment(MyRidesFragment())
//            }
//        }
    }

    private fun loadFragment(fragment: Fragment){
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}