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

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.scootersharing.lufr.R
import dk.itu.moapd.scootersharing.lufr.model.RidesDB
import dk.itu.moapd.scootersharing.lufr.databinding.FragmentStartRideBinding
import dk.itu.moapd.scootersharing.lufr.model.Scooter

/**
 * Class StartRideFragment, holds the logic and functionality of the StartRideFragment.
 * @property scooterName Input text for the scooter name.
 * @property scooterLocation Input text for the scooter location.
 * @property scooter scooter object.
 * @property binding Fragment binding for the view fragment.
 */
class StartRideFragment : Fragment() {

    companion object {
        private val TAG = StartRideFragment::class.qualifiedName
    }

    private lateinit var scooterName: EditText
    private lateinit var scooterLocation: EditText

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val scooter: Scooter =
        Scooter(name = "", location = "", timestamp = System.currentTimeMillis(), lat = 0.0, long = 0.0, image = "")

    private lateinit var binding: FragmentStartRideBinding
    private lateinit var bottomNavBar: BottomNavigationView

    private lateinit var auth: FirebaseAuth

    /**
     * Default onCreate function.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
        super.onCreate(savedInstanceState)

        // Singleton to share an object between the app activities .
        // Singleton to share an object between the app activities .
        RidesDB.initialize {
            Log.d("RidesDB", "Data is fully loaded")
        }

        auth = Firebase.auth
    }

    /**
     * onCreateView function which inflates the binding as well as gets the inputs from text fields.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStartRideBinding.inflate(layoutInflater, container, false)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())

        scooterName = binding.editTextName
        scooterLocation = binding.editTextLocation

        return binding.root
    }

    /**
     * onViewCreated function, holds the logic for the "startRideButton", as well as the snackbar notification.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomNavBar = requireActivity().findViewById(R.id.bottomNavigationView)
        bottomNavBar.visibility = View.VISIBLE

        binding.apply {
            startRideButton.setOnClickListener {
                if (scooterName.text.isNotEmpty() && scooterLocation.text.isNotEmpty()) {
                    val status = RidesDB.addScooter(
                        scooterName.text.toString().trim(),
                        scooterLocation.text.toString().trim(),
                        System.currentTimeMillis(),
                        getLocation().first,
                        getLocation().second,
                        "")

                    Snackbar.make(
                        it,
                        status,
                        Snackbar.LENGTH_LONG
                    ).show()
                    showMessage(status)
                    if (!status.contains("Error")) {
                        //send user back to mainfragment screen
                        loadFragment(StartRideFragment())
                    }
                }
            }
            logoutButton.setOnClickListener {
                auth.signOut()
                Toast.makeText(
                    context, "Successfully logged out",
                    Toast.LENGTH_LONG
                ).show()
                loadFragment(WelcomeFragment())
            }
            settingsButton.setOnClickListener {
                loadFragment(SettingsFragment())
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
     * show message method which logs name and location when "startRideButton" is clicked.
     */
    private fun showMessage(input: String) {
        Log.d(TAG, input)
    }

    private fun getLocation(): Pair<Double, Double>{
        var lat = 0.0
        var long = 0.0
        if (checkPermission()){
            throw Exception("No location permissions!")
        }
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    lat = location.latitude
                    long = location.longitude
                } else {

                }
            }
            .addOnFailureListener { exception: Exception ->
                throw exception
            }
        return Pair(lat, long)
    }

    private fun checkPermission() =
        ActivityCompat.checkSelfPermission(
            this.requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this.requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
}