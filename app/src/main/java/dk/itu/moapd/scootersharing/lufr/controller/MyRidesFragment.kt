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
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.scootersharing.lufr.model.RidesDB
import dk.itu.moapd.scootersharing.lufr.databinding.FragmentMyRidesBinding
import dk.itu.moapd.scootersharing.lufr.model.PreviousRide
import dk.itu.moapd.scootersharing.lufr.model.UsersDB

/**
 * Class MainFragment, holds the logic and functionality of the main fragment.
 * @property binding fragment binding for the view fragment
 */
class MyRidesFragment : Fragment() {

    companion object {
        private val TAG = MyRidesFragment::class.qualifiedName
    }

    private lateinit var binding: FragmentMyRidesBinding

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CustomArrayAdapter

    private lateinit var user: FirebaseUser
    private lateinit var auth: FirebaseAuth

    /**
     * Default onCreate function.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
        super.onCreate(savedInstanceState)

        RidesDB.initialize {
            Log.d("RidesDB", "Data is fully loaded")
        }

        auth = Firebase.auth
        if (auth.currentUser != null) {
            user = auth.currentUser!!
        }

    }
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            binding = FragmentMyRidesBinding.inflate(layoutInflater, container, false)
            recyclerView = binding.recyclerView

            //await this
            var rides = ArrayList<PreviousRide>()
            adapter = CustomArrayAdapter(rides)
            UsersDB.getMyRides(auth.currentUser?.email!!){
                    previousRides ->
                rides.clear()
                rides = previousRides as ArrayList<PreviousRide>
                rides.reverse()
                adapter = CustomArrayAdapter(rides)
                recyclerView.adapter = adapter
            }

            recyclerView.adapter = adapter
            return binding.root
        }
        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
        }
}

