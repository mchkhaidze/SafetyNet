package ge.mchkhaidze.safetynet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ge.mchkhaidze.safetynet.R

class MediaPagerAdapter(private val mediaList: List<String>) :
    RecyclerView.Adapter<MediaPagerAdapter.MediaViewHolder>() {

    class MediaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.resource)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.image_view_item, parent, false)
        return MediaViewHolder(view)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        Glide.with(holder.itemView.context)
            .load(mediaList[position])
            .dontAnimate()
            .into(holder.imageView)
    }

    override fun getItemCount() = mediaList.size
}

