package dk.itu.moapd.scootersharing.lufr.controller

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import dk.itu.moapd.scootersharing.lufr.BuildConfig
import dk.itu.moapd.scootersharing.lufr.R
import dk.itu.moapd.scootersharing.lufr.controller.SignupFragment.Companion.TAG

class MapsFragment : Fragment() {

    val options = GoogleMapOptions()
    private lateinit var bottomNavBar: BottomNavigationView

    private var locationPermissionGranted = false
    private val fineLocationRequest = 0

    private var lastKnownLocation: Location? = null
    private val defaultLocation = LatLng(55.6596, 12.5910)
    private var defaultCameraPosition: CameraPosition? = null

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var googleMap: GoogleMap

    private var defaultZoom = 18f

    private var cameraPosition = "camera position"
    private val location = "location"

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        val itu = LatLng (55.6596, 12.5910)
        googleMap.addMarker(MarkerOptions().position(itu).title("IT University of Copenhagen"))
        onMapReady(googleMap)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(location)
            defaultCameraPosition = savedInstanceState.getParcelable(cameraPosition)
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())


        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this.callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomNavBar = requireActivity().findViewById(R.id.bottomNavigationView)
        bottomNavBar.visibility = View.VISIBLE

        getLocationPermission()

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(this.requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                fineLocationRequest)
        }
    }

    private fun checkPermission() =
        ActivityCompat.checkSelfPermission(
            this.requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this.requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED

    private fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        if (checkPermission()) {
            return
        } else {
            // Show the current device's location as a blue dot.
            googleMap.isMyLocationEnabled = true
            val location = LatLng(googleMap.myLocation.latitude, googleMap.myLocation.longitude)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, defaultZoom))
            // Set the default map type.
            googleMap.mapType = GoogleMap.MAP_TYPE_HYBRID
        }
    }
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        locationPermissionGranted = false
        when (requestCode) {
            fineLocationRequest -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
        updateLocationUI()
    }
    @SuppressLint("MissingPermission")
    private fun updateLocationUI() {
        try {
            if (locationPermissionGranted) {
                googleMap.isMyLocationEnabled = true
                googleMap.uiSettings.isMyLocationButtonEnabled = true
            } else {
                googleMap.isMyLocationEnabled = false
                googleMap.uiSettings.isMyLocationButtonEnabled = false
                lastKnownLocation = null
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.result
                        if (lastKnownLocation != null) {
                            googleMap.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        lastKnownLocation!!.latitude,
                                        lastKnownLocation!!.longitude
                                    ),
                                    defaultZoom
                                )
                            )
                        }
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.")
                        Log.e(TAG, "Exception: %s", task.exception)
                        googleMap.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                defaultLocation,
                                defaultZoom
                            )
                        )
                        googleMap.uiSettings.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        googleMap.let { map ->
            outState.putParcelable(cameraPosition, map.cameraPosition)
            outState.putParcelable(location, lastKnownLocation)
        }
        super.onSaveInstanceState(outState)
    }
}