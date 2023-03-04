package ge.mchkhaidze.safetynet.activity

import android.os.Bundle
import android.widget.ImageView
import androidx.preference.EditTextPreference
import androidx.preference.SeekBarPreference
import androidx.preference.SwitchPreference
import com.google.android.material.appbar.MaterialToolbar
import ge.mchkhaidze.safetynet.ErrorHandler
import ge.mchkhaidze.safetynet.R
import ge.mchkhaidze.safetynet.Utils
import ge.mchkhaidze.safetynet.service.UserInfoService


class SettingsActivity : BaseActivity(), ErrorHandler {
    private lateinit var imageView: ImageView
    private lateinit var username: EditTextPreference
    private lateinit var distance: SeekBarPreference
    private lateinit var notifications: SwitchPreference
    private lateinit var alerts: SwitchPreference

    private lateinit var toolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val fragment = PreferencesFragment()

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings_layout, fragment)
            .commit()

        toolbar = findViewById(R.id.toolbar_actions)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_done -> {
                    //todo save updated image
                    username = fragment.findPreference("username")!!
                    distance = fragment.findPreference("radius")!!
                    notifications = fragment.findPreference("notifications")!!
                    alerts = fragment.findPreference("alerts")!!

                    UserInfoService.uploadUserInformation(
                        username.title.toString(),
                        null,
                        "",
                        distance.value,
                        notifications.isChecked,
                        alerts.isChecked,
                        this::finishActivity,
                        this::handleError
                    )
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun finishActivity(): Boolean {
        finish()
        return true
    }

    override fun handleError(err: String): Boolean {
        Utils.showWarning(err, findViewById(R.id.toolbar_actions))
        return true
    }
}