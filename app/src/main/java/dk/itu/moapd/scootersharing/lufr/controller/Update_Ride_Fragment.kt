/**
MIT License

Copyright (c) [2023] [Lucas Alexander Bjerre Fremming]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package dk.itu.moapd.scootersharing.lufr.controller

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.scootersharing.lufr.R
import dk.itu.moapd.scootersharing.lufr.RidesDB
import dk.itu.moapd.scootersharing.lufr.databinding.FragmentUpdateRideBinding
import dk.itu.moapd.scootersharing.lufr.model.Scooter

/**
 * Class Update_Ride_Fragment, holds the logic and functionality of the update page.
 * @property scooterName Input text for the scooter name.
 * @property scooterLocation Input text for the scooter location.
 * @property scooter scooter object.
 * @property binding fragment binding for the view fragment.
 */
class Update_Ride_Fragment : Fragment() {

    companion object {
        private val TAG = Update_Ride_Fragment::class.qualifiedName
    }
    private lateinit var ridesDB : RidesDB

    private lateinit var scooterName: EditText
    private lateinit var scooterLocation: EditText
    private val scooter: Scooter = Scooter(timestamp = System.currentTimeMillis(), name = "", location = "", image = "")
    private lateinit var binding: FragmentUpdateRideBinding
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var auth: FirebaseAuth

    /**
     * Default onCreate function.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
        super.onCreate(savedInstanceState)

        // Singleton to share an object between the app activities .
        ridesDB = RidesDB(this.requireContext())

        auth = Firebase.auth

    }

    /**
     * onCreateView function which inflates the binding as well as gets the inputs from text fields.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View? {
        binding = FragmentUpdateRideBinding.inflate(layoutInflater, container, false)

        scooterName = binding.editTextName
        scooterLocation = binding.editTextLocation

        return binding.root
    }

    /**
     * onViewCreated function, holds the logic for the "updateRideButton", as well as the snackbar notification.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        val latestScooter = ridesDB.getCurrentScooter()

        binding.editTextName.setText(latestScooter?.name)

        binding.apply {
            updateRideButton.setOnClickListener {
                if (scooterLocation.text.isNotEmpty()){

                    val location = scooterLocation.text.toString().trim()
                    val timestamp = System.currentTimeMillis()

                    ridesDB.updateCurrentScooter(location,timestamp)

                    Snackbar.make(
                        binding.root,
                        ("[$timestamp] - Scooter updated with location: '$location', and time: '$'."),
                        Snackbar.LENGTH_LONG
                    ).show()
                    showMessage()
                    loadFragment(MyRidesFragment())
                }
            }
            logoutButton.setOnClickListener {
                auth.signOut()
                Toast.makeText(context, "Successfully logged out",
                    Toast.LENGTH_LONG).show()
                loadFragment(WelcomeFragment())
            }
            settingsButton.setOnClickListener{
                loadFragment(SettingsFragment())
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

    /**
     * show message method which logs name and location when "updateRideButton" is clicked.
     */
    private fun showMessage(){
        Log.d(TAG, scooter.toString())
    }
}