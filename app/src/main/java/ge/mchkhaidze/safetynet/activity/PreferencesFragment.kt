package ge.mchkhaidze.safetynet.activity

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import ge.mchkhaidze.safetynet.R

class PreferencesFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference, rootKey)
    }
}