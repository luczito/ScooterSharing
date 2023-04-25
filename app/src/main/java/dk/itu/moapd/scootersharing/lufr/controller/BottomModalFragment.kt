package dk.itu.moapd.scootersharing.lufr.controller

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.util.LruCache
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dk.itu.moapd.scootersharing.lufr.R
import dk.itu.moapd.scootersharing.lufr.controller.SignupFragment.Companion.TAG
import dk.itu.moapd.scootersharing.lufr.databinding.FragmentBottomModalBinding
import dk.itu.moapd.scootersharing.lufr.model.RidesDB
import java.io.ByteArrayOutputStream
import java.sql.Date
import java.text.SimpleDateFormat


class BottomModalFragment(private val marker: Marker) : BottomSheetDialogFragment() {

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
    }

    private lateinit var binding: FragmentBottomModalBinding

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var auth: FirebaseAuth

    private val handler = Handler()
    private var isTextViewUpdating = false

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

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        return binding.root
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.scooterName.text = name
        binding.scooterLocation.text = location
        binding.scooterTimestamp.text = timestamp
        binding.rideClock.visibility = View.GONE

        // Download image from Firebase Storage
        val maxCacheSize = 1024 * 1024 * 10 // 10 MB

        val storageRef = Firebase.storage.reference

        //use scooter image
        val scooter = RidesDB.getScooter(name!!)

        val imageRef = storageRef.child("scooters/${scooter.image}")

        val cache = LruCache<String, Bitmap>(maxCacheSize)
        val cachedBitmap = cache.get(name)

        if (cachedBitmap != null) {
            // Load image from cache
            binding.scooterImage.setImageBitmap(cachedBitmap)
        } else {
            // Download image and save in cache
            val ONE_MEGABYTE: Long = 1024 * 1024
            imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener {
                val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                binding.scooterImage.setImageBitmap(bitmap)
                cache.put(name, bitmap) // Save image in cache
            }.addOnFailureListener {
                // Handle any errors
            }
        }



        //check is the ride is currently reserved
        if (reserved != "") {
            binding.scooterReserved.text = "$name is reserved"
            if (reserved == auth.currentUser?.email.toString()) {
                binding.reserveButton.text = "Cancel reservation"
                binding.reserveButton.isClickable = true
                binding.reserveButton.visibility = View.VISIBLE
            } else {
                binding.reserveButton.visibility = View.GONE
                binding.reserveButton.isClickable = false
            }
        } else {
            binding.scooterReserved.text = "$name is available"
            binding.reserveButton.visibility = View.VISIBLE
            binding.reserveButton.isClickable = true
        }

        //check if the ride is currently in use by user
        if (RidesDB.getScooter(name!!).user != "") {
            //check if that user is not me
            if(RidesDB.getScooter(name!!).user != auth.currentUser?.email){
                binding.startRideButton.visibility = View.GONE
                binding.startRideButton.isClickable = false
                binding.reserveButton.visibility = View.GONE
                binding.reserveButton.isClickable = false
                binding.scooterReserved.visibility = View.VISIBLE
                binding.scooterReserved.text = "$name is currently in use"
            }else {
                binding.rideClock.visibility = View.VISIBLE
                binding.startRideButton.text = "End ride"
                binding.scooterReserved.visibility = View.GONE
                binding.reserveButton.visibility = View.GONE
                binding.reserveButton.isClickable = false
                startUpdatingCounter()
            }
        }

        binding.apply {
            reserveButton.setOnClickListener {
                if (reserved == auth.currentUser?.email.toString()) {
                    changeColor("blue")

                    //toast it up - cancel toaster
                    Toast.makeText(
                        context, "Successfully cancelled reservation of $name",
                        Toast.LENGTH_LONG
                    ).show()
                    dismiss()
                    RidesDB.cancelReservation(name!!, auth.currentUser?.email.toString())
                } else {
                    changeColor("yellow")

                    //toast it up - reserve toaster
                    Toast.makeText(
                        context, "Successfully reserved $name",
                        Toast.LENGTH_LONG
                    ).show()
                    dismiss()
                    RidesDB.reserveScooter(name!!, auth.currentUser?.email.toString())
                }
            }
            startRideButton.setOnClickListener {
                if (startRideButton.text == "Start ride") {
                    //double checks
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Start ride")
                        .setMessage("Start ride on scooter $name?")
                        .setNeutralButton("Cancel") { _, _ ->
                            dismiss()
                        }
                        .setPositiveButton("Start") { _, _ ->
                            //remove reserve button, start counter, change button title
                            RidesDB.startRide(name!!, auth.currentUser?.email.toString())

                            changeColor("red")

                            binding.rideClock.visibility = View.VISIBLE
                            startUpdatingCounter()

                            binding.scooterReserved.text = "$name is not available"
                            reserveButton.visibility = View.GONE
                            reserveButton.isClickable = false
                            binding.scooterReserved.visibility = View.GONE
                            startRideButton.text = "End ride"
                        }
                        .show()

                } else if (startRideButton.text == "End ride") {
                    //double checks
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("End ride")
                        .setMessage("End ride on scooter $name?")
                        .setNeutralButton("Cancel") { _, _ ->

                        }
                        .setPositiveButton("End") { _, _ ->

                            getLocation {lat, long ->
                                val geoCoder = Geocoder(requireContext())
                                val matches = geoCoder.getFromLocation(lat, long, 1)
                                val bestMatch = if (matches!!.isEmpty()) null else matches[0]
                                RidesDB.updateScooter(name!!, bestMatch!!.getAddressLine(0), System.currentTimeMillis(), lat, long)
                                marker.position = LatLng(lat, long)
                                marker.isVisible = false
                                marker.isVisible = true
                            }


                            stopUpdatingCounter() //stop the count

                            changeColor("blue")

                            binding.scooterReserved.visibility = View.VISIBLE
                            binding.scooterReserved.text = "$name is available"

                            reserveButton.visibility = View.VISIBLE     //show the reserve button again
                            reserveButton.isClickable = true

                            binding.scooterTimestamp.text = RidesDB.getScooter(name!!).getFormatTimestamp()
                            binding.scooterLocation.text = RidesDB.getScooter(name!!).location

                            binding.rideClock.visibility = View.GONE

                            startRideButton.text = "Start ride" //change button title again

                            dismiss()
                            //toast it up - display ride time to user
                            Toast.makeText(
                                context, "Ride on scooter: $name ended. Ride length: ${
                                    SimpleDateFormat("HH:mm:ss").format(
                                        Date((RidesDB.endRide(name!!) * 1000).toLong())
                                    )
                                }",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        .show()
                }
            }
        }
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun startUpdatingCounter() {
        isTextViewUpdating = true

        binding.rideClock.text = "Ongoing ride: ${
            SimpleDateFormat("HH:mm:ss").format(
                Date(((RidesDB.getScooter(name!!).timer - 3600) * 1000).toLong())
            )}"

        handler.postDelayed(updateTextViewRunnable, 1000)
    }

    private fun stopUpdatingCounter() {
        isTextViewUpdating = false

        handler.removeCallbacks(updateTextViewRunnable)
    }

    private val updateTextViewRunnable = object : Runnable {
        @SuppressLint("SetTextI18n", "SimpleDateFormat")
        override fun run() {
            binding.rideClock.text = "Ongoing ride: ${
                SimpleDateFormat("HH:mm:ss").format(
                    Date(((RidesDB.getScooter(name!!).timer - 3600) * 1000).toLong())
                )}"

            if (isTextViewUpdating) {
                handler.postDelayed(this, 1000)
            }
        }
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

    private fun changeColor(color: String){
        val vectorDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.marker_scooter)
        if(color == "red"){
            vectorDrawable?.setTint(ContextCompat.getColor(requireContext(), R.color.red))
        }else if (color == "yellow"){
            vectorDrawable?.setTint(ContextCompat.getColor(requireContext(), R.color.yellow))
        }else if (color == "blue"){
            vectorDrawable?.setTint(ContextCompat.getColor(requireContext(), R.color.main_blue))
        }

        val bitmap = Bitmap.createBitmap(
            vectorDrawable?.intrinsicWidth ?: 0,
            vectorDrawable?.intrinsicHeight ?: 0,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable?.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable?.draw(canvas)

        val icon = BitmapDescriptorFactory.fromBitmap(bitmap)
        marker.setIcon(icon)
    }
}