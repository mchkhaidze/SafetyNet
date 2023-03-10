package ge.mchkhaidze.safetynet.model

data class UserInfo(
    var username: String,
    var photo: String,
    var uid: String = "",
    var radius: Int,
    var alert: Boolean
) {

    companion object {
        const val USERS = "users"
        const val USERNAME = "username"
        const val PHOTO_URL = "photo_url"
        const val NOTIFICATIONS = "notifications"
        const val RADIUS = "radius"
        const val ALERT = "alert"

        const val DEFAULT_RADIUS = 20
        const val DEFAULT_NOTIFICATIONS = true
        const val DEFAULT_ALERT = false
    }
}