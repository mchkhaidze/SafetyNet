package ge.mchkhaidze.safetynet.activity

import android.os.Bundle
import com.google.android.material.appbar.MaterialToolbar
import ge.mchkhaidze.safetynet.R


class SettingsActivity : BaseActivity() {

    private lateinit var toolbar: MaterialToolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings_layout, PreferencesFragment())
            .commit()

        toolbar = findViewById(R.id.toolbar_actions)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_done -> {
                    //todo
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}