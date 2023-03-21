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

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
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
        lateinit var ridesDB : RidesDB
    }

    private lateinit var scooterName: EditText
    private lateinit var scooterLocation: EditText
    private val scooter: Scooter = Scooter(timestamp = System.currentTimeMillis(), name = "", location = "")
    private lateinit var binding: FragmentUpdateRideBinding

    /**
     * Default onCreate function.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
        super.onCreate(savedInstanceState)

        // Singleton to share an object between the app activities .
        ridesDB = RidesDB.get(requireContext())

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

        binding.editTextName.setText(latestScooter.name)

        binding.apply {
            updateRideButton.setOnClickListener {
                if (scooterLocation.text.isNotEmpty()){

                    val location = scooterLocation.text.toString().trim()
                    val timestamp = System.currentTimeMillis()

                    ridesDB.updateCurrentScooter(location,timestamp)

                    Snackbar.make(
                        binding.root,
                        ("[$timestamp] - Scooter updated with location: '$location', and time: '$timestamp'."),
                        Snackbar.LENGTH_LONG
                    ).show()
                    showMessage()
                }
            }
        }
    }
    /**
     * show message method which logs name and location when "updateRideButton" is clicked.
     */
    private fun showMessage(){
        Log.d(TAG, scooter.toString())
    }
}