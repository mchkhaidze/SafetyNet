package ge.mchkhaidze.safetynet.activity

import android.os.Bundle
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import androidx.preference.SwitchPreference
import com.google.firebase.auth.FirebaseAuth
import ge.mchkhaidze.safetynet.ErrorHandler
import ge.mchkhaidze.safetynet.R
import ge.mchkhaidze.safetynet.model.User
import ge.mchkhaidze.safetynet.service.UserInfoService

class PreferencesFragment : PreferenceFragmentCompat(), ErrorHandler {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference, rootKey)

        UserInfoService.getUserInfo(
            FirebaseAuth.getInstance().uid ?: "",
            this::setCurrentUser,
            this::handleError
        )
    }

    private fun setCurrentUser(user: User?): Boolean {
        if (user != null) {
            //        val image = findPreference<SwitchPreference>("alerts") TODO
            val username = findPreference<EditTextPreference>("username")
            val distance = findPreference<SeekBarPreference>("radius")
            val notifications = findPreference<SwitchPreference>("notifications")
            val alerts = findPreference<SwitchPreference>("alerts")

            if (username != null) {
                username.title = user.username!!

                username.setOnPreferenceChangeListener { _, newValue ->
                    username.title = newValue.toString()
                    true
                }
            }

            if (distance != null) {
                distance.value = user.radius!!
            }

            if (notifications != null) {
                notifications.isChecked = user.notifications!!
            }

            if (alerts != null) {
                alerts.isChecked = user.alert!!
            }
        } else {
            handleError("error loading user")
        }
        return true
    }

    override fun handleError(err: String): Boolean {
        return true
    }
}