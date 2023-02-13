package ge.mchkhaidze.safetynet

import android.app.Activity
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

class Utils {

    companion object {
        fun showWarning(txt: String, view: View, fab: FloatingActionButton? = null) {
            Snackbar.make(view, txt, Snackbar.LENGTH_SHORT).setAnchorView(fab).show()
        }

        fun Activity.hideSoftKeyboard(id: Int) {
            val view: EditText = findViewById(id)
            view.onFocusChangeListener = View.OnFocusChangeListener { _, p1 ->
                Log.d("hideSoftKeyboard", "hideSoftKeyboard: 1")
                if (!p1) {
                    Log.d("hideSoftKeyboard", "hideSoftKeyboard: 2")
                    (getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                        view.windowToken,
                        0
                    )
                }
            }
        }

        fun Activity.startLoader() {
            findViewById<LinearLayout>(R.id.progressBar).visibility = View.VISIBLE
        }

        fun Activity.stopLoader() {
            findViewById<LinearLayout>(R.id.progressBar).visibility = View.GONE
        }

        fun getFormattedDate(): String {
            val date = Date()
            val format = SimpleDateFormat("dd MMM yyyy 'at' HH:mm", Locale.getDefault())
            return format.format(date)
        }
    }

}