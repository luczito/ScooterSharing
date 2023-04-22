package dk.itu.moapd.scootersharing.lufr.controller

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.scootersharing.lufr.databinding.FragmentBottomModalBinding
import dk.itu.moapd.scootersharing.lufr.model.RidesDB

class BottomModalFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentBottomModalBinding

    private lateinit var auth: FirebaseAuth

    private var name: String? = null
    private var location: String? = null
    private var timestamp: String? = null
    private var reserved: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            name = it.getString("name")
            location = it.getString("location")
            timestamp = it.getString("timestamp")
            reserved = it.getString("reserved")
        }
        RidesDB.initialize {
            Log.d("RidesDB", "Data is fully loaded")
        }
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentBottomModalBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the data to the views in the bottom sheet

        binding.scooterName.text = name
        binding.scooterLocation.text = location
        binding.scooterTimestamp.text = timestamp
        if(reserved != ""){
            binding.scooterReserved.text = "Scooter: $name is reserved"
            if(reserved == auth.currentUser?.email.toString()){
                binding.reserveButton.text = "Cancel reservation"
                binding.reserveButton.isClickable = true
                binding.reserveButton.visibility = View.VISIBLE
            }else{
                binding.reserveButton.visibility = View.GONE
                binding.reserveButton.isClickable = false
            }
        }else{
            binding.scooterReserved.text = "Scooter: $name is available"
            binding.reserveButton.visibility = View.VISIBLE
            binding.reserveButton.isClickable = true
        }

        binding.apply {
            reserveButton.setOnClickListener{
                if(reserved == auth.currentUser?.email.toString()){
                    Toast.makeText(context, "Successfully cancelled reservation of scooter: $name",
                        Toast.LENGTH_LONG).show()
                    dismiss()
                    RidesDB.cancelReservation(name!!, auth.currentUser?.email.toString())
                }else {
                    Toast.makeText(
                        context, "Successfully reserved scooter: $name",
                        Toast.LENGTH_LONG
                    ).show()
                    dismiss()
                    RidesDB.reserveScooter(name!!, auth.currentUser?.email.toString())
                }
            }
        }
    }
}