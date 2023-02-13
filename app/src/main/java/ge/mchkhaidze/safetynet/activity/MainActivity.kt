package ge.mchkhaidze.safetynet.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import ge.mchkhaidze.safetynet.R
import ge.mchkhaidze.safetynet.service.NavigationService

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        if (FirebaseAuth.getInstance().currentUser == null) {
            NavigationService.loadPage(this, SignUpActivity::class.java)
        } else {
            NavigationService.loadPage(this, SignInActivity::class.java)
        }
    }

}