package ge.mchkhaidze.safetynet.service

import com.google.firebase.database.*
import ge.mchkhaidze.safetynet.model.NewsFeedItem.Companion.POSTS

class NewsFeedService {

    private val limit = 5
    var lastNode: String? = ""

    fun lazyLoadPosts(
        updateData: (uid: String?, userImage: String?, username: String?, image: String?, description: String?, date: String?) -> Boolean,
        handleError: (String) -> Boolean
    ) {
        val query: Query
        try {
            query = if (lastNode == "") {
                FirebaseDatabase.getInstance().reference
                    .child(POSTS)
                    .orderByChild("create_date")
                    .limitToFirst(limit)
            } else {
                FirebaseDatabase.getInstance().reference
                    .child(POSTS)
                    .orderByChild("create_date")
                    .startAfter(lastNode)
                    .limitToFirst(limit)
            }
        } catch (ex: Exception) {
            handleError("Data is not available")
            return
        }

        query.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.children.count() == 0) {
                    updateData(null, null, null, null, null, null)
                }

                for (i in snapshot.children) {
                    val uid = i.key
                    val hMap = i.value as HashMap<String, String>
                    val userImage = hMap["user_image"]
                    val username = hMap["username"]
                    val image = hMap["resource"]
                    val desc = hMap["description"]
                    val date = hMap["create_date"]
                    lastNode = username
                    updateData(uid, userImage, username, image, desc, date)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                handleError(error.message)
            }
        })
    }
}