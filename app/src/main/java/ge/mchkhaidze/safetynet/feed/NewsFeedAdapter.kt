package ge.mchkhaidze.safetynet.feed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ge.mchkhaidze.safetynet.R

class NewsFeedAdapter(private var list: ArrayList<NewsFeedItem>) : RecyclerView.Adapter<NewsFeedAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val newsFeedImage: ImageView = itemView.findViewById(R.id.news_feed_image)
        val newsFeedTitle: TextView = itemView.findViewById(R.id.news_feed_title)
        val newsFeedDescription: TextView = itemView.findViewById(R.id.news_feed_description)
        val newsFeedActions: LinearLayout = itemView.findViewById(R.id.news_feed_actions)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.news_feed_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentNewsFeed = list[position]

//        holder.newsFeedImage.setImageResource(currentNewsFeed.image)
        holder.newsFeedTitle.text = currentNewsFeed.title
        holder.newsFeedDescription.text = currentNewsFeed.description
    }

    override fun getItemCount() = list.size
}
