package ge.mchkhaidze.safetynet.activity

import android.Manifest
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.LocationManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import ge.mchkhaidze.safetynet.R

class MapsActivity : BaseActivity(), OnMapReadyCallback {
    private lateinit var map: GoogleMap
    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        MapsInitializer.initialize(
            this,
            MapsInitializer.Renderer.LATEST,
            OnMapsSdkInitializedCallback() {})

        // Initialize the MapView
        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)

        // Get a reference to the GoogleMap object
        mapView.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.mapType = GoogleMap.MAP_TYPE_NORMAL

        val extras = intent.extras
        if (extras != null) {
            val latLng = LatLng(extras.getDouble("lat"), extras.getDouble("lng"))
            map.addMarker(MarkerOptions().position(latLng))
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        } else {


            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Snackbar.make(
                    findViewById(R.id.mainView),
                    "Location permission is required",
                    Snackbar.LENGTH_INDEFINITE
                ).show()
                return
            }
            map.isMyLocationEnabled = true
            val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            val criteria = Criteria()
            val provider = locationManager.getBestProvider(criteria, true)
            val location = provider?.let { locationManager.getLastKnownLocation(it) }
            if (location != null) {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}
