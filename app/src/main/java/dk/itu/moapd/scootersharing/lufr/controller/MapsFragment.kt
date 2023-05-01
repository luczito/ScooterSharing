package dk.itu.moapd.scootersharing.lufr.controller

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.scootersharing.lufr.BuildConfig
import dk.itu.moapd.scootersharing.lufr.R
import dk.itu.moapd.scootersharing.lufr.controller.SignupFragment.Companion.TAG
import dk.itu.moapd.scootersharing.lufr.model.RidesDB


class MapsFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var bottomNavBar: BottomNavigationView

    private var locationPermissionGranted = false
    private val fineLocationRequest = 0

    private var lastKnownLocation: Location? = null
    private val defaultLocation = LatLng(55.6596, 12.5910)
    private var defaultCameraPosition: CameraPosition? = null

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var googleMap: GoogleMap
    private lateinit var autoCompleteTextView: AutoCompleteTextView

    private var defaultZoom = 18f

    private var cameraPosition = "camera position"
    private val location = "location"

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!Places.isInitialized()) {
            val apiKey = BuildConfig.MAPS_API_KEY
            Places.initialize(requireContext(), apiKey)
        }

        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(location)
            defaultCameraPosition = savedInstanceState.getParcelable(cameraPosition)
        }
        RidesDB.initialize {
            Log.d("RidesDB", "Data is fully loaded")
        }

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_maps, container, false)

        val autoCompleteTextView =
            view.findViewById<AutoCompleteTextView>(R.id.idAutoCompleteTextView)
        autoCompleteTextView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (autoCompleteTextView.compoundDrawables[2] != null) {
                    if (event.x >= autoCompleteTextView.width - autoCompleteTextView.paddingRight - autoCompleteTextView.compoundDrawables[2].bounds.width()) {
                        autoCompleteTextView.setText("")
                        return@setOnTouchListener true
                    }
                }
            }
            false
        }

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomNavBar = requireActivity().findViewById(R.id.bottomNavigationView)
        bottomNavBar.visibility = View.VISIBLE

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?

        mapFragment?.getMapAsync(this)

        autoCompleteTextView = view.findViewById(R.id.idAutoCompleteTextView)

        val placesClient = Places.createClient(requireContext())
        val autoCompleteAdapter =
            PlacesAutoCompleteAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line)
        autoCompleteTextView.setAdapter(autoCompleteAdapter)
        autoCompleteTextView.threshold = 3

        autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            val selectedPlace = autoCompleteAdapter.getItem(position)
            selectedPlace?.let { placeItem ->
                autoCompleteTextView.setText(placeItem.primaryText)

                val placeId = placeItem.placeId
                val fetchPlaceRequest =
                    FetchPlaceRequest.newInstance(placeId, listOf(Place.Field.LAT_LNG))
                placesClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener { response ->
                    val place = response.place
                    val latLng = place.latLng
                    if (latLng != null) {
                        googleMap.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                latLng,
                                defaultZoom
                            )
                        )
                    }
                }.addOnFailureListener { exception ->
                    if (exception is ApiException) {
                        Log.e(TAG, "Place not found: ${exception.statusCode}")
                    }
                }
            }
        }
        auth = Firebase.auth

    }

    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                this.requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                fineLocationRequest
            )
        }
    }

    private fun checkPermission() =
        ActivityCompat.checkSelfPermission(
            this.requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this.requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap

        if (checkPermission()) {
            getLocationPermission()
            return
        } else {
            // Show the current device's location as a blue dot.
            googleMap.isMyLocationEnabled = true
            // Set the default map type.
            googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        }
        getLocationPermission()


        googleMap.setOnMapLoadedCallback {
            val vectorDrawable =
                ContextCompat.getDrawable(requireContext(), R.drawable.marker_scooter)
            for (ride in RidesDB.getRidesList()) {
                if (ride.user != "") {
                    vectorDrawable?.setTint(ContextCompat.getColor(requireContext(), R.color.red))
                } else if (ride.reserved != "") {
                    vectorDrawable?.setTint(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.yellow
                        )
                    )
                } else {
                    vectorDrawable?.setTint(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.main_blue
                        )
                    )
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

                googleMap.addMarker(
                    MarkerOptions()
                        .position(LatLng(ride.lat, ride.long))
                        .title(ride.name)
                        .alpha(1f)
                        .icon(icon)
                )
            }
        }
        updateLocationUI()
        getDeviceLocation()
        googleMap.setOnMarkerClickListener(this)

    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        locationPermissionGranted = false
        when (requestCode) {
            fineLocationRequest -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
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

    // move camera to marker


    private fun moveCameraToMarker(lat: Double, long: Double) {
        if (::googleMap.isInitialized) {
            googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        lat,
                        long
                    ),
                    defaultZoom
                )
            )
        } else {
            Log.d(TAG, "GoogleMap not initialized")
        }
    }


    override fun onMarkerClick(marker: Marker): Boolean {
        val scooter = RidesDB.getScooter(marker.title.toString())
        moveCameraToMarker(marker.position.latitude, marker.position.longitude)

        // Create a BottomSheetDialogFragment
        val bottomSheetDialogFragment = BottomModalFragment(marker)
        // Pass the marker data to the bottom sheet fragment
        val bundle = Bundle()
        bundle.putString("name", marker.title)
        bundle.putString("location", scooter.location)
        bundle.putString("timestamp", scooter.getFormatTimestamp())
        bundle.putString("reserved", scooter.reserved.toString())
        bottomSheetDialogFragment.arguments = bundle

        // Show the bottom sheet fragment
        bottomSheetDialogFragment.show(parentFragmentManager, "bottomSheetDialog")

        return true
    }

}