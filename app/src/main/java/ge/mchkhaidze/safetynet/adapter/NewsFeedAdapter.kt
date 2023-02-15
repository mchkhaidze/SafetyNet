package ge.mchkhaidze.safetynet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import ge.mchkhaidze.safetynet.R
import ge.mchkhaidze.safetynet.activity.ProfileActivity
import ge.mchkhaidze.safetynet.model.NewsFeedItem
import ge.mchkhaidze.safetynet.model.UserInfo
import ge.mchkhaidze.safetynet.service.NavigationService

class NewsFeedAdapter(private val context: Context) :
    RecyclerView.Adapter<NewsFeedAdapter.ViewHolder>() {

    var list: ArrayList<NewsFeedItem> = ArrayList()
    private var isTextExpanded = false

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val newsFeedUserPhoto: ImageView = itemView.findViewById(R.id.poster_image)
        val newsFeedUserImageCard: CardView = itemView.findViewById(R.id.profile_image)
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
            .load(post.userImage)
            .into(holder.newsFeedUserPhoto)

        holder.newsFeedUsername.text = post.userName
        holder.newsFeedPostDate.text = post.createDate
        holder.newsFeedDesc.text = post.description
        holder.newsFeedDesc.setOnClickListener {
            isTextExpanded = !isTextExpanded
            holder.newsFeedDesc.maxLines = if (isTextExpanded) Integer.MAX_VALUE else 2
        }

        holder.viewPager.adapter = MediaPagerAdapter(post.resources)

        holder.viewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(pagePosition: Int) {
                super.onPageSelected(pagePosition)
                if (post.resources.size > 1) {
                    val text = "" + (pagePosition + 1) + "/" + post.resources.size
                    holder.countView.text = text
                    holder.countView.visibility = View.VISIBLE
                }
            }
        })

        holder.newsFeedUserImageCard.setOnClickListener {
            val extras = mapOf(
                Pair(UserInfo.USERNAME, post.userName),
                Pair(NewsFeedItem.UID, post.userId)
            )
            NavigationService.loadPage(context, ProfileActivity::class.java, extras)
        }

        holder.newsFeedUsername.setOnClickListener {
            val extras = mapOf(
                Pair(UserInfo.USERNAME, post.userName),
                Pair(NewsFeedItem.UID, post.userId)
            )
            NavigationService.loadPage(context, ProfileActivity::class.java, extras)
        }

    }

    override fun getItemCount() = list.size
}
