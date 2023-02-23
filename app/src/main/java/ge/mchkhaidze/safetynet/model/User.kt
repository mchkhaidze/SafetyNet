package ge.mchkhaidze.safetynet.model

data class User(
    val alert: Boolean? = null,
    val photo_url: String? = null,
    val radius: Int? = null,
    var username: String? = null
)
