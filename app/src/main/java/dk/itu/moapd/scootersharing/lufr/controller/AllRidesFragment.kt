package dk.itu.moapd.scootersharing.lufr.controller

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.scootersharing.lufr.model.RidesDB
import dk.itu.moapd.scootersharing.lufr.databinding.FragmentAllRidesBinding

class AllRidesFragment : Fragment() {
    companion object {
        private val TAG = AllRidesFragment::class.qualifiedName
    }
    private lateinit var binding: FragmentAllRidesBinding

    private lateinit var recyclerView: RecyclerView

    private lateinit var user: FirebaseUser
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
        super.onCreate(savedInstanceState)

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
    ) : View {
        binding = FragmentAllRidesBinding.inflate(layoutInflater, container, false)

        recyclerView = binding.recyclerView
        recyclerView.adapter = CardAdapter(RidesDB.getRidesAsCards())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}