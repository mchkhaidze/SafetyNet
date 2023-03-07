package ge.mchkhaidze.safetynet

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.widget.ImageView
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.bumptech.glide.Glide

class ImagePreference(context: Context, attrs: AttributeSet?) : Preference(context, attrs) {

    private var imageUri: Uri? = null
    lateinit var imageView: ImageView

    init {
        layoutResource = R.layout.image_preference_layout
    }

    fun setImage(imageUrl: String?) {
        Glide.with(context)
            .load(imageUrl)
            .into(imageView)
    }

    fun setImage(imageURI: Uri?) {
        imageUri = imageURI
        imageView.setImageURI(imageURI)
    }

    fun getImageUri(): Uri? {
        return imageUri
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        imageView = holder.itemView.findViewById(R.id.image)
    }
}