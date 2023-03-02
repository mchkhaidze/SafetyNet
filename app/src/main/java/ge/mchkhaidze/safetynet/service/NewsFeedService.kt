package ge.mchkhaidze.safetynet.service

import com.google.firebase.auth.FirebaseAuth
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

        load(updateData, handleError, postsRef)
    }

    fun loadUserSpecificPosts(
        uid: String,
        updateData: (data: ArrayList<NewsFeedItem>?) -> Boolean,
        handleError: (String) -> Boolean
    ) {
        val postsRef =
            FirebaseDatabase.getInstance().getReference(POSTS).orderByChild("uid").equalTo(uid)

        load(updateData, handleError, postsRef)
    }

    private fun load(
        updateData: (data: ArrayList<NewsFeedItem>?) -> Boolean,
        handleError: (String) -> Boolean,
        postsRef: Query
    ) {
        val usersRef = FirebaseDatabase.getInstance().getReference("users")
        val resourcesRef = FirebaseDatabase.getInstance().getReference("resources")
        val list: MutableList<NewsFeedItem> = ArrayList()

        postsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.children.count() == 0) {
                    updateData(null)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors here
            }
        })

        postsRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val post = dataSnapshot.getValue(Post::class.java)
                if (post?.reports?.containsKey(FirebaseAuth.getInstance().uid) == true) {
                } else {
                    var item: NewsFeedItem?
                    val userRef = usersRef.child(post?.uid ?: "")
                    userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(userSnapshot: DataSnapshot) {
                            val user = userSnapshot.getValue(User::class.java)

                            item = NewsFeedItem(
                                dataSnapshot.key!!,
                                post?.uid!!,
                                user?.photo_url!!,
                                user.username!!,
                                mutableListOf(),
                                post.description,
                                post.create_date!!,
                                post.timestamp!!,
                                post.latitude!!,
                                post.longitude!!,
                                post.address!!,
                                post.likes?.contains(FirebaseAuth.getInstance().uid) == true,
                                post.likes?.size ?: 0,
                                post.dislikes?.contains(FirebaseAuth.getInstance().uid) == true,
                                post.dislikes?.size ?: 0,
                            )

                            val resourcesQuery =
                                resourcesRef.orderByChild("post_id").equalTo(dataSnapshot.key)
                            resourcesQuery.addListenerForSingleValueEvent(object :
                                ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    for (resourceSnapshot in dataSnapshot.children) {
                                        val resource =
                                            resourceSnapshot.getValue(Resource::class.java)
                                        if (resource != null) {
                                            item?.resources?.add(resource.resource_url!!)
                                        }
                                    }
                                    list.add(item!!)
                                    val sorted =
                                        list.sortedWith(compareByDescending { it.timestamp })
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
            }

            override fun onChildChanged(
                dataSnapshot: DataSnapshot,
                previousChildName: String?
            ) {
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
        })
    }
}