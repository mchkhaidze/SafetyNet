package ge.mchkhaidze.safetynet.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import ge.mchkhaidze.safetynet.R
import ge.mchkhaidze.safetynet.service.NavigationService

class MainActivity : BaseActivity() {

    companion object {
        private const val REQUEST_CODE_LOCATION_PERMISSION = 300
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        requestLocationPermission()
        navigate()
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_LOCATION_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                navigate()
            } else {
                Snackbar.make(
                    findViewById(R.id.mainView),
                    "Location permission is required",
                    Snackbar.LENGTH_INDEFINITE
                ).show()
            }
        }
    }

    private fun navigate() {
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
        } else {

            if (FirebaseAuth.getInstance().currentUser == null) {
                NavigationService.loadPage(this, SignInActivity::class.java)
            } else {
                NavigationService.loadPage(this, NewsFeedActivity::class.java)
            }
        }
    }
}