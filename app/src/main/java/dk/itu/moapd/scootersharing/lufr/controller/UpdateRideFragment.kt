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
import dk.itu.moapd.scootersharing.lufr.databinding.FragmentUpdateRideBinding
import dk.itu.moapd.scootersharing.lufr.model.Scooter
import java.sql.Date
import java.text.SimpleDateFormat

/**
 * Class UpdateRideFragment, holds the logic and functionality of the update page.
 * @property scooterName Input text for the scooter name.
 * @property scooterLocation Input text for the scooter location.
 * @property scooter scooter object.
 * @property binding fragment binding for the view fragment.
 */
class UpdateRideFragment : Fragment() {

    companion object {
        private val TAG = UpdateRideFragment::class.qualifiedName
    }

    private lateinit var scooterName: EditText
    private lateinit var scooterLocation: EditText

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val scooter: Scooter = Scooter(name = "", location = "", timestamp = System.currentTimeMillis(), lat = 0.0, long = 0.0, image = "")

    private lateinit var binding: FragmentUpdateRideBinding
    private lateinit var bottomNavBar: BottomNavigationView
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

        auth = Firebase.auth

    }

    /**
     * onCreateView function which inflates the binding as well as gets the inputs from text fields.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View {
        binding = FragmentUpdateRideBinding.inflate(layoutInflater, container, false)

        scooterName = binding.editTextName
        scooterLocation = binding.editTextLocation

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())

        return binding.root
    }

    /**
     * onViewCreated function, holds the logic for the "updateRideButton", as well as the snackbar notification.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        bottomNavBar = requireActivity().findViewById(R.id.bottomNavigationView)
        bottomNavBar.visibility = View.VISIBLE

        val latestScooter = RidesDB.getCurrentScooter()

        binding.editTextName.setText(latestScooter?.name)

        binding.apply {
            updateRideButton.setOnClickListener {
                if (scooterLocation.text.isNotEmpty()){

                    val location = scooterLocation.text.toString().trim()
                    val timestamp = System.currentTimeMillis()

                    getLocation{lat, long ->
                        RidesDB.updateCurrentScooter(
                            location,
                            timestamp,
                            lat,
                            long
                        )
                    }

                    Snackbar.make(
                        binding.root,
                        ("[${SimpleDateFormat("dd/MM/yyyy HH:mm").format(Date(timestamp))}]: - Scooter updated with location: '$location'."),
                        Snackbar.LENGTH_LONG
                    ).show()
                    showMessage()
                    loadFragment(UpdateRideFragment())
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
    private fun getLocation(callback: (Double, Double) -> Unit) {
        if (checkPermission()) {
            throw Exception("No location permissions!")
        }
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val lat = location.latitude
                    val long = location.longitude
                    callback(lat, long)
                } else {
                    throw Exception("Location is null")
                }
            }
            .addOnFailureListener { exception: Exception ->
                throw exception
            }
    }

    private fun checkPermission() =
        ActivityCompat.checkSelfPermission(
            this.requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this.requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
}