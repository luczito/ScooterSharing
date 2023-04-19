package dk.itu.moapd.scootersharing.lufr.controller

import android.content.pm.PackageManager
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import dk.itu.moapd.scootersharing.lufr.R

class MapsFragment : Fragment() {

    val options = GoogleMapOptions()
    private lateinit var bottomNavBar: BottomNavigationView

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        val itu = LatLng (55.6596, 12.5910)
        googleMap.addMarker(MarkerOptions().position(itu).title("Marker in IT University of Copenhagen"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(itu, 18f))
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

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
//
//    private fun checkPermission() =
//        ActivityCompat.checkSelfPermission(
//            this.requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
//        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//            this.requireContext(),
//            Manifest.permission.ACCESS_COARSE_LOCATION
//        ) != PackageManager.PERMISSION_GRANTED
//
//    override fun onMapReady(googleMap: GoogleMap) {
//        if (checkPermission()) {
//            return
//        } else {
//            // Show the current device's location as a blue dot.
//            googleMap.isMyLocationEnabled = true
//            // Set the default map type.
//            googleMap.mapType = GoogleMap.MAP_TYPE_HYBRID
//        }
//
//    }
}