package ge.mchkhaidze.safetynet

import android.content.Context
import android.content.Intent

class NavigationService {

    companion object {
        fun loadPage(
            context: Context,
            pageClass: Class<*>,
            extras: Map<String, String>? = null
        ) {
            val intent = Intent(context, pageClass)
            if (extras != null) {
                for (i in extras) {
                    intent.putExtra(i.key, i.value)
                }
            }
            context.startActivity(intent)

        }
    }
}