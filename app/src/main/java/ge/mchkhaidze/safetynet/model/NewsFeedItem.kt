package ge.mchkhaidze.safetynet.model

data class NewsFeedItem(
    var userImage: String,
    var userName: String,
    var image: String?,
    var description: String?,
    var createDate: String
) {
    companion object {
        const val POSTS = "posts"
    }
}