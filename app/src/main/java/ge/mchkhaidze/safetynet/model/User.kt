package ge.mchkhaidze.safetynet.model

data class User(
    val notifications: Boolean? = null,
    val alert: Boolean? = null,
    val photo_url: String? = null,
    val radius: Int? = null,
    var username: String? = null
) {
    companion object {
        const val DEFAULT_IMAGE =
            "https://firebasestorage.googleapis.com/v0/b/safetynet-1.appspot.com/o/profile_pic.png?alt=media&token=6593d9d5-0565-4d7e-b0ed-9c4edf3dc114"

    }
}
