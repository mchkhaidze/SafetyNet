package ge.mchkhaidze.safetynet.service

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import ge.mchkhaidze.safetynet.model.NewsFeedItem.Companion.ADDRESS
import ge.mchkhaidze.safetynet.model.NewsFeedItem.Companion.CREATE_DATE
import ge.mchkhaidze.safetynet.model.NewsFeedItem.Companion.DESCRIPTION
import ge.mchkhaidze.safetynet.model.NewsFeedItem.Companion.LATITUDE
import ge.mchkhaidze.safetynet.model.NewsFeedItem.Companion.LONGITUDE
import ge.mchkhaidze.safetynet.model.NewsFeedItem.Companion.POSTS
import ge.mchkhaidze.safetynet.model.NewsFeedItem.Companion.RESOURCES
import ge.mchkhaidze.safetynet.model.NewsFeedItem.Companion.TIMESTAMP
import ge.mchkhaidze.safetynet.model.NewsFeedItem.Companion.UID
import java.util.*


class PostsService {
    companion object {

        fun uploadPost(
            description: String,
            location: Pair<Double, Double>,
            address: String,
            date: String,
            timeStamp: Long,
            resources: List<Uri>,
            actionAfter: (() -> Boolean)?,
            handleError: (String) -> Boolean
        ) {

            val uid = FirebaseAuth.getInstance().uid ?: ""
            val postId = UUID.randomUUID().toString()
            val ref = FirebaseDatabase.getInstance().getReference("/${POSTS}/$postId")
            val post = HashMap<String, Any>()

            post[UID] = uid
            post[DESCRIPTION] = description
            post[CREATE_DATE] = date
            post[TIMESTAMP] = timeStamp
            post[LATITUDE] = location.first.toString()
            post[LONGITUDE] = location.second.toString()
            post[ADDRESS] = address

            ref.setValue(post)
                .addOnSuccessListener {

                    val totalFiles = resources.size
                    var uploadedFiles = 0

                    for (fileUri in resources) {
                        val filename = UUID.randomUUID().toString()
                        val fileRef =
                            FirebaseStorage.getInstance().getReference("/$filename")
                        fileRef.putFile(fileUri)
                            .addOnSuccessListener {
                                fileRef.downloadUrl.addOnSuccessListener { currUrl ->
                                    val resourceName = UUID.randomUUID().toString()
                                    val resourceRef = FirebaseDatabase.getInstance()
                                        .getReference("/${RESOURCES}/$resourceName")

                                    Log.d("TAG", resourceRef.toString())
                                    val res = HashMap<String, Any>()

                                    res["post_id"] = postId
                                    res["resource_url"] = currUrl.toString()

                                    resourceRef.setValue(res)
                                        .addOnSuccessListener {
                                            uploadedFiles++
                                            if (uploadedFiles == totalFiles) {
                                                if (actionAfter != null) {
                                                    actionAfter()
                                                }
                                            }
                                        }
                                }.addOnFailureListener {
                                    it.message?.let { it1 -> handleError(it1) }
                                }

                            }
                            .addOnFailureListener {
                                it.message?.let { it1 -> handleError(it1) }
                            }
                    }
                }
                .addOnFailureListener {
                    Log.d("Post", "Failed to upload")
                    it.message?.let { it1 -> handleError(it1) }
                }
        }

        fun reportPost(postId: String, userId: String, text: String) {
            FirebaseDatabase.getInstance()
                .getReference("/${POSTS}/$postId/reported")
                .setValue(true)

            FirebaseDatabase.getInstance()
                .getReference("/${POSTS}/$postId/reports")
                .child(userId)
                .setValue(text)
        }

        fun updatePostLikes(postId: String, userId: String, liked: Boolean, disliked: Boolean) {
            if (liked) {
                FirebaseDatabase.getInstance()
                    .getReference("/${POSTS}/$postId/likes")
                    .child(userId)
                    .setValue(true)

                FirebaseDatabase.getInstance()
                    .getReference("/${POSTS}/$postId/dislikes")
                    .child(userId)
                    .removeValue()
            } else if (disliked) {
                FirebaseDatabase.getInstance()
                    .getReference("/${POSTS}/$postId/dislikes")
                    .child(userId)
                    .setValue(true)

                FirebaseDatabase.getInstance()
                    .getReference("/${POSTS}/$postId/likes")
                    .child(userId)
                    .removeValue()
            } else {
                FirebaseDatabase.getInstance()
                    .getReference("/${POSTS}/$postId/dislikes")
                    .child(userId)
                    .removeValue()

                FirebaseDatabase.getInstance()
                    .getReference("/${POSTS}/$postId/likes")
                    .child(userId)
                    .removeValue()
            }
        }
    }
}