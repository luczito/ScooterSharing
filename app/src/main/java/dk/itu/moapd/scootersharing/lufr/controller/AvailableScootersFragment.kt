package dk.itu.moapd.scootersharing.lufr.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.scootersharing.lufr.R
import dk.itu.moapd.scootersharing.lufr.RidesDB
import dk.itu.moapd.scootersharing.lufr.databinding.FragmentAvailableScootersBinding
import dk.itu.moapd.scootersharing.lufr.model.Card

class AvailableScootersFragment : Fragment() {
    companion object {
        private val TAG = AvailableScootersFragment::class.qualifiedName
    }
    private lateinit var binding: FragmentAvailableScootersBinding

    private lateinit var ridesDB : RidesDB

    private lateinit var recyclerView: RecyclerView

    private lateinit var user: FirebaseUser
    private lateinit var auth: FirebaseAuth

    private lateinit var userTextField: EditText

    /**
     * Default onCreate function.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
        super.onCreate(savedInstanceState)

        // Singleton to share an object between the app activities .
        ridesDB = RidesDB(this.requireContext())

        user = Firebase.auth.currentUser!!
        auth = Firebase.auth



    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View? {
        binding = FragmentAvailableScootersBinding.inflate(layoutInflater, container, false)

        //val cards = listOf(
            //Card("Title 1", "Secondary text 1", "Supporting text 1", R.drawable.media1, "Action 1"),
            //Card("Title 2", "Secondary text 2", "Supporting text 2", R.drawable.media2, "Action 2"),
            //Card("Title 3", "Secondary text 3", "Supporting text 3", R.drawable.media3, "Action 3")
        //)

        val recyclerView = binding.recyclerView
        //recyclerView.adapter = CardAdapter(cards)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        return binding.root
    }
    /**
     * onCreateView function which inflates the binding, and holds the functionality for the 3 buttons.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        binding.editTextUser.setText("Welcome " + user.email)

        binding.apply {

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
            settingsButton.setOnClickListener{
                val fragment = SettingsFragment()
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }
}