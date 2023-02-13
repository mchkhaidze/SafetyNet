package ge.mchkhaidze.safetynet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import ge.mchkhaidze.safetynet.R
import ge.mchkhaidze.safetynet.model.NewsFeedItem

class NewsFeedAdapter : RecyclerView.Adapter<NewsFeedAdapter.ViewHolder>() {

    var list: ArrayList<NewsFeedItem> = ArrayList()
    private var isTextExpanded = false

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val newsFeedUserPhoto: ImageView = itemView.findViewById(R.id.poster_image)
        val newsFeedUsername: TextView = itemView.findViewById(R.id.profile_name)
        val newsFeedPostDate: TextView = itemView.findViewById(R.id.date)
        val viewPager: ViewPager2 = itemView.findViewById(R.id.view_pager)
        val countView: TextView = itemView.findViewById(R.id.overlay_view)
        val newsFeedDesc: TextView = itemView.findViewById(R.id.news_feed_description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.news_feed_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = list[position]

        Glide.with(holder.itemView.context)
            .load("https://firebasestorage.googleapis.com/v0/b/safetynet-1.appspot.com/o/images%2Fd4d71bb0-aa09-42cc-8c8b-3e695d6582ee?alt=media&token=f63837c6-8b2d-4479-a21c-74557ce7b801")
            .into(holder.newsFeedUserPhoto)

        holder.newsFeedUsername.text = post.userName
        holder.newsFeedPostDate.text = post.createDate
        var tmp = 0
        if (position == 0) {
            tmp = 2
            holder.viewPager.adapter = MediaPagerAdapter(
                listOf(
                    "https://firebasestorage.googleapis.com/v0/b/safetynet-1.appspot.com/o/images%2Fd4d71bb0-aa09-42cc-8c8b-3e695d6582ee?alt=media&token=f63837c6-8b2d-4479-a21c-74557ce7b801",
                    "https://firebasestorage.googleapis.com/v0/b/safetynet-1.appspot.com/o/images%2F6d168ef7-f466-46ef-8cbb-de7970fee001?alt=media&token=9dfb10f9-2056-4178-94bc-c93f073c0679"
                )
            )
        } else {
            holder.viewPager.adapter = MediaPagerAdapter(listOf())
        }

        holder.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(pagePosition: Int) {
                super.onPageSelected(pagePosition)
                if (tmp > 1) {
                    val text = "" + (pagePosition + 1) + "/" + tmp
                    holder.countView.text = text
                    holder.countView.visibility = View.VISIBLE
                }
            }
        })
        holder.newsFeedDesc.text = post.description
        holder.newsFeedDesc.setOnClickListener {
            isTextExpanded = !isTextExpanded
            holder.newsFeedDesc.maxLines = if (isTextExpanded) Integer.MAX_VALUE else 2
        }
    }

    override fun getItemCount() = list.size
}
