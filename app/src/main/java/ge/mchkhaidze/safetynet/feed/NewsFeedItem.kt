package ge.mchkhaidze.safetynet.feed

import java.util.*

data class NewsFeedItem(
    var image: Int = 1,
    var title: String = "2",
    var description: String = "3",
    var createDate: String = Date().toString()
)