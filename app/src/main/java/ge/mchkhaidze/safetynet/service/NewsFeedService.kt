package ge.mchkhaidze.safetynet.service

import com.google.firebase.database.*
import ge.mchkhaidze.safetynet.model.NewsFeedItem
import ge.mchkhaidze.safetynet.model.NewsFeedItem.Companion.POSTS
import ge.mchkhaidze.safetynet.model.Post
import ge.mchkhaidze.safetynet.model.Resource
import ge.mchkhaidze.safetynet.model.User

class NewsFeedService {

    fun loadAllPosts(
        updateData: (data: ArrayList<NewsFeedItem>?) -> Boolean,
        handleError: (String) -> Boolean
    ) {

        val postsRef = FirebaseDatabase.getInstance().getReference(POSTS)
        val usersRef = FirebaseDatabase.getInstance().getReference("users")
        val resourcesRef = FirebaseDatabase.getInstance().getReference("resources")
        val list: MutableList<NewsFeedItem> = ArrayList()

        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val post = dataSnapshot.getValue(Post::class.java)
                var item: NewsFeedItem?
                val userRef = usersRef.child(post?.uid ?: "")
                userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(userSnapshot: DataSnapshot) {
                        val user = userSnapshot.getValue(User::class.java)

                        item = NewsFeedItem(
                            post?.uid!!,
                            user?.photo_url!!,
                            user.username!!,
                            mutableListOf(),
                            post.description,
                            post.create_date!!,
                            post.timestamp!!,
                            post.latitude!!,
                            post.longitude!!,
                            post.address!!
                        )

                        val resourcesQuery =
                            resourcesRef.orderByChild("post_id").equalTo(dataSnapshot.key)
                        resourcesQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                for (resourceSnapshot in dataSnapshot.children) {
                                    val resource = resourceSnapshot.getValue(Resource::class.java)
                                    if (resource != null) {
                                        item?.resources?.add(resource.resource_url!!)
                                    }
                                }
                                list.add(item!!)
                                val sorted = list.sortedWith(compareByDescending { it.timestamp })
                                updateData(ArrayList(sorted))
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                handleError(databaseError.message)
                            }
                        })
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        handleError(databaseError.message)
                    }
                })
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                // Handle changes to existing posts here
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                // Handle removal of posts here
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                // Handle changes to the order of posts here
            }

            override fun onCancelled(databaseError: DatabaseError) {
                handleError(databaseError.message)
            }
        }

        postsRef.addChildEventListener(childEventListener)

//        val query: Query
//        try {
//            query = FirebaseDatabase.getInstance().reference
//                .child(POSTS)
//                .orderByChild(TIMESTAMP)
//        } catch (ex: Exception) {
//            handleError("Data is not available")
//            return
//        }
//
//        query.addListenerForSingleValueEvent(object : ValueEventListener {
//
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if (snapshot.children.count() == 0) {
//                    updateData(null)
//                } else {
//
//                    val list: ArrayList<NewsFeedItem> = ArrayList()
//
//                    for (i in snapshot.children) {
//                        val hMap = i.value as HashMap<*, *>
//
//                        list.add(
//                            NewsFeedItem(
//                                hMap[UID] as String,
//                                "https://firebasestorage.googleapis.com/v0/b/safetynet-1.appspot.com/o/profile_pic.png?alt=media&token=6593d9d5-0565-4d7e-b0ed-9c4edf3dc114",
//                                "username",
//                                listOf(),
//                                hMap[DESCRIPTION] as String?,
//                                hMap[CREATE_DATE]!! as String,
//                                hMap[TIMESTAMP]!! as Long,
//                                hMap[LATITUDE]!! as String,
//                                hMap[LONGITUDE]!! as String,
//                                hMap[ADDRESS]!! as String
//                            )
//                        )
//                    }
//
//                    val sorted = list.sortedWith(compareByDescending { it.timestamp })
//                    updateData(ArrayList(sorted))
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                handleError(error.message)
//            }
//        })
    }

    fun loadUserSpecificPosts(
        uid: String,
        updateData: (data: ArrayList<NewsFeedItem>?) -> Boolean,
        handleError: (String) -> Boolean
    ) {
        val postsRef =
            FirebaseDatabase.getInstance().getReference(POSTS).orderByChild("uid").equalTo(uid)
        val usersRef = FirebaseDatabase.getInstance().getReference("users")
        val resourcesRef = FirebaseDatabase.getInstance().getReference("resources")
        val list: MutableList<NewsFeedItem> = ArrayList()

        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val post = dataSnapshot.getValue(Post::class.java)
                var item: NewsFeedItem?
                val userRef = usersRef.child(post?.uid ?: "")
                userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(userSnapshot: DataSnapshot) {
                        val user = userSnapshot.getValue(User::class.java)

                        item = NewsFeedItem(
                            post?.uid!!,
                            user?.photo_url!!,
                            user.username!!,
                            mutableListOf(),
                            post.description,
                            post.create_date!!,
                            post.timestamp!!,
                            post.latitude!!,
                            post.longitude!!,
                            post.address!!
                        )

                        val resourcesQuery =
                            resourcesRef.orderByChild("post_id").equalTo(dataSnapshot.key)
                        resourcesQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                for (resourceSnapshot in dataSnapshot.children) {
                                    val resource = resourceSnapshot.getValue(Resource::class.java)
                                    if (resource != null) {
                                        item?.resources?.add(resource.resource_url!!)
                                    }
                                }
                                list.add(item!!)
                                val sorted = list.sortedWith(compareByDescending { it.timestamp })
                                updateData(ArrayList(sorted))
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                handleError(databaseError.message)
                            }
                        })
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        handleError(databaseError.message)
                    }
                })
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                // Handle changes to existing posts here
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                // Handle removal of posts here
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                // Handle changes to the order of posts here
            }

            override fun onCancelled(databaseError: DatabaseError) {
                handleError(databaseError.message)
            }
        }

        postsRef.addChildEventListener(childEventListener)
//        val query: Query
//        try {
//            query = FirebaseDatabase.getInstance().reference
//                .child(POSTS)
//                .orderByChild(TIMESTAMP)
//        } catch (ex: Exception) {
//            handleError("Data is not available")
//            return
//        }
//
//        query.addListenerForSingleValueEvent(object : ValueEventListener {
//
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if (snapshot.children.count() == 0) {
//                    updateData(null)
//                } else {
//
//                    val list: ArrayList<NewsFeedItem> = ArrayList()
//
//                    for (i in snapshot.children) {
//                        val hMap = i.value as HashMap<*, *>
//
//                        if (hMap[UID] == uid) {
//                            list.add(
//                                NewsFeedItem(
//                                    hMap[UID] as String,
//                                    "https://firebasestorage.googleapis.com/v0/b/safetynet-1.appspot.com/o/profile_pic.png?alt=media&token=6593d9d5-0565-4d7e-b0ed-9c4edf3dc114",
//                                    "username",
//                                    mutableListOf(),
//                                    hMap[DESCRIPTION] as String?,
//                                    hMap[CREATE_DATE]!! as String,
//                                    hMap[TIMESTAMP]!! as Long,
//                                    hMap[LATITUDE]!! as String,
//                                    hMap[LONGITUDE]!! as String,
//                                    hMap[ADDRESS]!! as String
//                                )
//                            ) //todo get username, image and resources
//                        }
//                    }
//
//                    if (list.isEmpty()) {
//                        updateData(null)
//                    } else {
//                        val sorted = list.sortedWith(compareByDescending { it.timestamp })
//                        updateData(ArrayList(sorted))
//                    }
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                handleError(error.message)
//            }
//        })
    }
}