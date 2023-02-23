package ge.mchkhaidze.safetynet.model

data class Post(
    val title: String? = null,
    val address: String? = null,
    val create_date: String? = null,
    var description: String? = null,
    val latitude: String? = null,
    val longitude: String? = null,
    var timestamp: Long? = null,
    var uid: String? = null
)
