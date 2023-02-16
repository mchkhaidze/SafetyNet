package ge.mchkhaidze.safetynet.service

import android.provider.MediaStore
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import ge.mchkhaidze.safetynet.model.NewsFeedItem.Companion.CREATE_DATE
import ge.mchkhaidze.safetynet.model.NewsFeedItem.Companion.DESCRIPTION
import ge.mchkhaidze.safetynet.model.NewsFeedItem.Companion.LATITUDE
import ge.mchkhaidze.safetynet.model.NewsFeedItem.Companion.LONGITUDE
import ge.mchkhaidze.safetynet.model.NewsFeedItem.Companion.POSTS
import ge.mchkhaidze.safetynet.model.NewsFeedItem.Companion.TIMESTAMP
import ge.mchkhaidze.safetynet.model.NewsFeedItem.Companion.UID
import java.util.*


class PostsService {
    companion object {

        fun uploadPost(
            description: String,
            location: Pair<Double, Double>,
            date: String,
            timeStamp: Long,
            resources: List<MediaStore.Files>, //todo save uploaded resources
            actionAfter: (() -> Boolean)?,
            handleError: (String) -> Boolean
        ) {

            val uid = FirebaseAuth.getInstance().uid ?: ""
            val postId = UUID.randomUUID().toString()
            val ref = FirebaseDatabase.getInstance().getReference("/${POSTS}/$postId")
            val newsFeedItem = HashMap<String, Any>()

            newsFeedItem[UID] = uid
            newsFeedItem[DESCRIPTION] = description
            newsFeedItem[CREATE_DATE] = date
            newsFeedItem[TIMESTAMP] = timeStamp
            newsFeedItem[LATITUDE] = location.first
            newsFeedItem[LONGITUDE] = location.second
            ref.setValue(newsFeedItem)
                .addOnSuccessListener {
                    Log.d("Post", "added: $postId")
                    if (actionAfter != null) {
                        actionAfter()
                    }
                }
                .addOnFailureListener {
                    Log.d("Post", "Failed to upload")
                    it.message?.let { it1 -> handleError(it1) }
                }
        }
    }
}