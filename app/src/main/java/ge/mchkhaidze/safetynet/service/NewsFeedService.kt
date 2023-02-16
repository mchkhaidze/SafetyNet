package ge.mchkhaidze.safetynet.service

import com.google.firebase.database.*
import ge.mchkhaidze.safetynet.model.NewsFeedItem
import ge.mchkhaidze.safetynet.model.NewsFeedItem.Companion.CREATE_DATE
import ge.mchkhaidze.safetynet.model.NewsFeedItem.Companion.DESCRIPTION
import ge.mchkhaidze.safetynet.model.NewsFeedItem.Companion.POSTS
import ge.mchkhaidze.safetynet.model.NewsFeedItem.Companion.TIMESTAMP
import ge.mchkhaidze.safetynet.model.NewsFeedItem.Companion.UID

class NewsFeedService {

    fun loadAllPosts(
        updateData: (data: ArrayList<NewsFeedItem>?) -> Boolean,
        handleError: (String) -> Boolean
    ) {
        val query: Query
        try {
            query = FirebaseDatabase.getInstance().reference
                .child(POSTS)
                .orderByChild(TIMESTAMP)
        } catch (ex: Exception) {
            handleError("Data is not available")
            return
        }

        query.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.children.count() == 0) {
                    updateData(null)
                } else {

                    val list: ArrayList<NewsFeedItem> = ArrayList()

                    for (i in snapshot.children) {
                        val hMap = i.value as HashMap<*, *>

                        list.add(
                            NewsFeedItem(
                                hMap[UID] as String,
                                "https://firebasestorage.googleapis.com/v0/b/safetynet-1.appspot.com/o/profile_pic.png?alt=media&token=6593d9d5-0565-4d7e-b0ed-9c4edf3dc114",
                                "username",
                                listOf(),
                                hMap[DESCRIPTION] as String?,
                                hMap[CREATE_DATE]!! as String,
                                hMap[TIMESTAMP]!! as Long
                            )
                        ) //todo get username, image and resources
                    }

                    val sorted = list.sortedWith(compareByDescending { it.timestamp })
                    updateData(ArrayList(sorted))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                handleError(error.message)
            }
        })
    }

    fun loadUserSpecificPosts(
        uid: String,
        updateData: (data: ArrayList<NewsFeedItem>?) -> Boolean,
        handleError: (String) -> Boolean
    ) {
        val query: Query
        try {
            query = FirebaseDatabase.getInstance().reference
                .child(POSTS)
                .orderByChild(TIMESTAMP)
        } catch (ex: Exception) {
            handleError("Data is not available")
            return
        }

        query.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.children.count() == 0) {
                    updateData(null)
                } else {

                    val list: ArrayList<NewsFeedItem> = ArrayList()

                    for (i in snapshot.children) {
                        val hMap = i.value as HashMap<*, *>

                        if (hMap[UID] == uid) {
                            list.add(
                                NewsFeedItem(
                                    hMap[UID] as String,
                                    "https://firebasestorage.googleapis.com/v0/b/safetynet-1.appspot.com/o/profile_pic.png?alt=media&token=6593d9d5-0565-4d7e-b0ed-9c4edf3dc114",
                                    "username",
                                    listOf(),
                                    hMap[DESCRIPTION] as String?,
                                    hMap[CREATE_DATE]!! as String,
                                    hMap[TIMESTAMP]!! as Long
                                )
                            ) //todo get username, image and resources
                        }
                    }

                    if (list.isEmpty()) {
                        updateData(null)
                    } else {
                        val sorted = list.sortedWith(compareByDescending { it.timestamp })
                        updateData(ArrayList(sorted))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                handleError(error.message)
            }
        })
    }
}