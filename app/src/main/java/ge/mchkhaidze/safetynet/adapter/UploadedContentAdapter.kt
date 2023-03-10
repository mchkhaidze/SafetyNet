package ge.mchkhaidze.safetynet.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ge.mchkhaidze.safetynet.R


class UploadedContentAdapter : RecyclerView.Adapter<UploadedContentAdapter.ViewHolder>() {

    var list: ArrayList<Uri> = ArrayList()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.imageView)
        val close: TextView = itemView.findViewById(R.id.closeTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.resource_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val content = list[position]

        Glide.with(holder.itemView.context)
            .load(content)
            .into(holder.image)

        holder.close.setOnClickListener {
            list.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    override fun getItemCount() = list.size
}
