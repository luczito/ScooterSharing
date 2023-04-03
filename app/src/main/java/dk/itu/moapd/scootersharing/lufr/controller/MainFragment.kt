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

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.database.DataSetObserver
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
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.scootersharing.lufr.R
import dk.itu.moapd.scootersharing.lufr.RidesDB
import dk.itu.moapd.scootersharing.lufr.databinding.FragmentMainBinding

/**
 * Class MainFragment, holds the logic and functionality of the main fragment.
 * @property binding fragment binding for the view fragment.
 * @property listView the listview for the list of scooters
 */
class MainFragment : Fragment() {

    companion object {
        private val TAG = MainFragment::class.qualifiedName
    }
    private lateinit var binding: FragmentMainBinding

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
        auth = Firebase.auth
        user = auth.currentUser!!
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) :View? {
        binding = FragmentMainBinding.inflate(layoutInflater, container, false)
        recyclerView = binding.recyclerView
        var adapter = CustomArrayAdapter(ridesDB.getRidesList())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.visibility = View.GONE

        adapter.setFragment(this)
        return binding.root
    }
    /**
     * onCreateView function which inflates the binding, and holds the functionality for the 3 buttons.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        binding.editTextUser.setText("Welcome " + user.email)

        binding.apply {
            startRideButton.setOnClickListener {
                val fragment = Start_Ride_Fragment()
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            }
            updateRideButton.setOnClickListener {
                val fragment = Update_Ride_Fragment()
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            }
            showRidesButton.setOnClickListener {
                if (recyclerView.visibility == View.GONE){
                    recyclerView.visibility = View.VISIBLE
                }else{
                    recyclerView.visibility = View.GONE
                }
            }
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
    fun onDataChanged() {
        recyclerView.visibility = View.GONE
    }
}
