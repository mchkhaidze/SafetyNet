package ge.mchkhaidze.safetynet.model

data class NewsFeedItem(
    var postId: String,
    var userId: String,
    var userImage: String,
    var userName: String,
    var resources: MutableList<String>,
    var description: String?,
    var createDate: String,
    var timestamp: Long,
    var latitude: String,
    var longitude: String,
    var address: String
) {
    companion object {
        const val POSTS = "posts"
        const val CREATE_DATE = "create_date"
        const val DESCRIPTION = "description"
        const val LATITUDE = "latitude"
        const val LONGITUDE = "longitude"
        const val ADDRESS = "address"
        const val UID = "uid"
        const val TIMESTAMP = "timestamp"
    }
}