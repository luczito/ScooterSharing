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
package dk.itu.moapd.scootersharing.lufr

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import dk.itu.moapd.scootersharing.lufr.databinding.ActivityMainBinding
import dk.itu.moapd.scootersharing.lufr.databinding.ActivityStartRideBinding
import dk.itu.moapd.scootersharing.lufr.databinding.FragmentMainBinding
import dk.itu.moapd.scootersharing.lufr.databinding.FragmentStartRideBinding
import java.sql.Timestamp
import kotlin.time.Duration.Companion.nanoseconds

/**
 * Class MainAcitivity, holds the main logic and functionality of the system.
 * @property scooterName Input text for the scooter name.
 * @property scooterLocation Input text for the scooter location.
 * @property scooter scooter object.
 * @property binding viewbinding for the view.
 */
class Start_Ride_Fragment : Fragment() {

    companion object {
        private val TAG = activity_start_ride::class.qualifiedName
        lateinit var ridesDB : RidesDB
    }

    private lateinit var scooterName: EditText
    private lateinit var scooterLocation: EditText
    private val scooter: Scooter = Scooter(timestamp = java.sql.Timestamp(System.currentTimeMillis()), name = "",location = "")
    private lateinit var binding: FragmentStartRideBinding

    /**
     * onCreate main function that holds the functionality of the system.
     * holds the onclick method for the startride button.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
        super.onCreate(savedInstanceState)

        // Singleton to share an object between the app activities .
        ridesDB = RidesDB.get(requireContext())
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View? {
        binding = FragmentStartRideBinding.inflate(layoutInflater, container, false)

        scooterName = binding.editTextName
        scooterLocation = binding.editTextLocation

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            startRideButton.setOnClickListener {
                if (scooterName.text.isNotEmpty() && scooterLocation.text.isNotEmpty()) {
                    val name = scooterName.text.toString().trim()
                    val location = scooterLocation.text.toString().trim()
                    val timestamp = java.sql.Timestamp(System.currentTimeMillis())

                    ridesDB.addScooter(name, location, timestamp)

                    Snackbar.make(
                        it,
                        ("$timestamp: Ride on scooter '$name' started from '$location'."),
                        Snackbar.LENGTH_LONG
                    ).show()
                    showMessage()
                }
            }
        }
    }

    /**
     * show message method which prints name and location when button is clicked.
     */
    private fun showMessage(){
        Log.d(TAG, scooter.toString())
    }
}