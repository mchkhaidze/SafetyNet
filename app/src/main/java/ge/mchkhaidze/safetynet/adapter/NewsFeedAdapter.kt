package ge.mchkhaidze.safetynet.adapter

import android.app.AlertDialog
import android.content.Context
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import ge.mchkhaidze.safetynet.R
import ge.mchkhaidze.safetynet.activity.MapsActivity
import ge.mchkhaidze.safetynet.activity.ProfileActivity
import ge.mchkhaidze.safetynet.model.NewsFeedItem
import ge.mchkhaidze.safetynet.model.UserInfo
import ge.mchkhaidze.safetynet.service.NavigationService
import ge.mchkhaidze.safetynet.service.PostsService


class NewsFeedAdapter(private val context: Context) :
    RecyclerView.Adapter<NewsFeedAdapter.ViewHolder>() {


    private val defaultPhoto =
        "https://firebasestorage.googleapis.com/v0/b/safetynet-1.appspot.com/o/profile_pic.png?alt=media&token=6593d9d5-0565-4d7e-b0ed-9c4edf3dc114"
    var list: ArrayList<NewsFeedItem> = ArrayList()
    private var isTextExpanded = false

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val newsFeedUserPhoto: ImageView = itemView.findViewById(R.id.poster_image)
        val newsFeedUserImageCard: CardView = itemView.findViewById(R.id.profile_image)
        val newsFeedUsername: TextView = itemView.findViewById(R.id.profile_name)
        val newsFeedPostDate: TextView = itemView.findViewById(R.id.date)
        val newsFeedPostAddr: TextView = itemView.findViewById(R.id.address)
        val viewPager: ViewPager2 = itemView.findViewById(R.id.view_pager)
        val countView: TextView = itemView.findViewById(R.id.overlay_view)
        val newsFeedDesc: TextView = itemView.findViewById(R.id.news_feed_description)
        val likeButton: ImageButton = itemView.findViewById(R.id.like)
        val likeCount: TextView = itemView.findViewById(R.id.like_count)
        val dislikeButton: ImageButton = itemView.findViewById(R.id.dislike)
        val dislikeCount: TextView = itemView.findViewById(R.id.dislike_count)
        val pinButton: ImageButton = itemView.findViewById(R.id.pin)
        val reportButton: ImageButton = itemView.findViewById(R.id.report)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.news_feed_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = list[position]

        val imageUrl = if (post.userImage != "") {
            post.userImage
        } else {
            defaultPhoto
        }
        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .into(holder.newsFeedUserPhoto)

        holder.newsFeedUsername.text = post.userName
        holder.newsFeedPostDate.text = post.createDate
        holder.newsFeedPostAddr.text = post.address

        holder.newsFeedPostAddr.setOnClickListener {
            val extras = mapOf(
                Pair("lat", post.latitude),
                Pair("lng", post.longitude)
            )
            NavigationService.loadPage(context, MapsActivity::class.java, extras)
        }

        holder.newsFeedDesc.text = post.description
        if (post.description == null || post.description == "") {
            holder.newsFeedDesc.visibility = View.GONE
        } else {
            holder.newsFeedDesc.visibility = View.VISIBLE
        }
        holder.newsFeedDesc.setOnClickListener {
            isTextExpanded = !isTextExpanded
            holder.newsFeedDesc.maxLines = if (isTextExpanded) Integer.MAX_VALUE else 2
        }

        holder.viewPager.adapter = MediaPagerAdapter(post.resources)

        if (post.resources.size > 1) {
            holder.viewPager.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(pagePosition: Int) {
                    super.onPageSelected(pagePosition)
                    val text = "" + (pagePosition + 1) + "/" + post.resources.size
                    holder.countView.text = text
                    holder.countView.visibility = View.VISIBLE
                }
            })
        } else {
            holder.countView.visibility = View.GONE
        }

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

        if (post.likedByMe) {
            holder.likeButton.setImageResource(R.drawable.arrow_up_bold_outline_filled)
        } else {
            holder.likeButton.setImageResource(R.drawable.arrow_up_bold_outline)
        }

        if (post.likeCount > 0) {
            holder.likeCount.text = post.likeCount.toString()
        } else {
            holder.likeCount.text = ""
        }

        if (post.dislikedByMe) {
            holder.dislikeButton.setImageResource(R.drawable.arrow_down_bold_outline_filled)
        } else {
            holder.dislikeButton.setImageResource(R.drawable.arrow_down_bold_outline)
        }

        if (post.dislikeCount > 0) {
            holder.dislikeCount.text = post.dislikeCount.toString()
        } else {
            holder.dislikeCount.text = ""
        }

        holder.likeButton.setOnClickListener {
            if (post.likedByMe) {
                post.likedByMe = false
                post.likeCount = post.likeCount - 1
                holder.likeButton.setImageResource(R.drawable.arrow_up_bold_outline)
            } else {
                post.likedByMe = true
                post.likeCount = post.likeCount + 1
                if (post.dislikedByMe) {
                    post.dislikedByMe = false
                    post.dislikeCount = post.dislikeCount - 1
                    holder.dislikeButton.setImageResource(R.drawable.arrow_down_bold_outline)
                }
                holder.likeButton.setImageResource(R.drawable.arrow_up_bold_outline_filled)
            }
            if (post.likeCount > 0) {
                holder.likeCount.text = post.likeCount.toString()
            } else {
                holder.likeCount.text = ""
            }
            if (post.dislikeCount > 0) {
                holder.dislikeCount.text = post.dislikeCount.toString()
            } else {
                holder.dislikeCount.text = ""
            }
            PostsService.updatePostLikes(post.postId, FirebaseAuth.getInstance().uid ?: "", post.likedByMe, post.dislikedByMe)
        }

        holder.dislikeButton.setOnClickListener {
            if (post.dislikedByMe) {
                post.dislikedByMe = false
                post.dislikeCount = post.dislikeCount - 1
                holder.dislikeButton.setImageResource(R.drawable.arrow_down_bold_outline)
            } else {
                post.dislikedByMe = true
                post.dislikeCount = post.dislikeCount + 1
                if (post.likedByMe) {
                    post.likedByMe = false
                    post.likeCount = post.likeCount - 1
                    holder.likeButton.setImageResource(R.drawable.arrow_up_bold_outline)
                }
                holder.dislikeButton.setImageResource(R.drawable.arrow_down_bold_outline_filled)
            }
            if (post.likeCount > 0) {
                holder.likeCount.text = post.likeCount.toString()
            } else {
                holder.likeCount.text = ""
            }
            if (post.dislikeCount > 0) {
                holder.dislikeCount.text = post.dislikeCount.toString()
            } else {
                holder.dislikeCount.text = ""
            }
            PostsService.updatePostLikes(post.postId, FirebaseAuth.getInstance().uid ?: "", post.likedByMe, post.dislikedByMe)
        }

        holder.pinButton.setOnClickListener {
            val extras = mapOf(
                Pair("lat", post.latitude),
                Pair("lng", post.longitude)
            )
            NavigationService.loadPage(context, MapsActivity::class.java, extras)
        }

        if (post.userId == FirebaseAuth.getInstance().uid.toString()) {
            holder.reportButton.visibility = View.GONE
        } else {
            holder.reportButton.visibility = View.VISIBLE
            holder.reportButton.setOnClickListener {
                val builder = AlertDialog.Builder(context, R.style.AlertDialogTheme)
                builder.setTitle("Why are you reporting this post?")
                val input = EditText(context)
                input.inputType = InputType.TYPE_CLASS_TEXT
                builder.setView(input)

                builder.setPositiveButton("Report") { _, _ ->
                    val text = input.text.toString()
                    PostsService.reportPost(post.postId, FirebaseAuth.getInstance().uid ?: "", text)
                    Log.d("position", position.toString() + list.size)
                    list.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, list.size - position)
                }

                builder.show()

            }
        }
    }

    override fun getItemCount() = list.size
}
