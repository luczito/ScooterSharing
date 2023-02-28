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
import androidx.appcompat.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import dk.itu.moapd.scootersharing.lufr.databinding.ActivityMainBinding
import dk.itu.moapd.scootersharing.lufr.databinding.FragmentMainBinding

/**
 * Class MainAcitivity, holds the main logic and functionality of the system.
 * @property scooterName Input text for the scooter name.
 * @property scooterLocation Input text for the scooter location.
 * @property scooter scooter object.
 * @property binding viewbinding for the view.
 */
class MainFragment : Fragment() {

    companion object {
        private val TAG = MainFragment::class.qualifiedName
        lateinit var ridesDB : RidesDB
    }
    private lateinit var binding: FragmentMainBinding

    private lateinit var listView: android.widget.ListView

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
    ) :View? {
        binding = FragmentMainBinding.inflate(layoutInflater, container, false)
        listView = binding.listView
        listView.adapter = CustomArrayAdapter(requireContext(), R.layout.rides_list, ridesDB.getRidesList())
        listView.visibility = View.GONE

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            startRideButton.setOnClickListener {
                val intent = Intent(requireContext(), activity_start_ride::class.java)
                requireActivity().startActivity(intent)
            }
            updateRideButton.setOnClickListener {
                val intent = Intent(requireContext(), activity_update_ride::class.java)
                requireActivity().startActivity(intent)
            }
            showRidesButton.setOnClickListener {
                if (listView.visibility == View.GONE){
                    listView.visibility = View.VISIBLE
                }else{
                    listView.visibility = View.GONE
                }
            }
        }
    }
}
