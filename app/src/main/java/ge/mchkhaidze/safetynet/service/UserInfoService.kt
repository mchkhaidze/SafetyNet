package ge.mchkhaidze.safetynet.service

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import ge.mchkhaidze.safetynet.model.UserInfo.Companion.ALERT
import ge.mchkhaidze.safetynet.model.UserInfo.Companion.PHOTO_URL
import ge.mchkhaidze.safetynet.model.UserInfo.Companion.RADIUS
import ge.mchkhaidze.safetynet.model.UserInfo.Companion.USERNAME
import ge.mchkhaidze.safetynet.model.UserInfo.Companion.USERS
import java.util.*


class UserInfoService {
    companion object {
        private const val IMAGES = "images"

        fun uploadUserInformation(
            username: String,
            imageUri: Uri?,
            imageURL: String,
            radius: Int,
            alert: Boolean,
            actionAfterLogged: (() -> Boolean)?,
            handleError: (String) -> Boolean
        ) {
            if (imageUri == null) {
                uploadInfo(username, imageURL, radius, alert, actionAfterLogged, handleError)
                return
            }

            val filename = UUID.randomUUID().toString()
            val ref = FirebaseStorage.getInstance().getReference("/$IMAGES/$filename")
            ref.putFile(imageUri)
                .addOnSuccessListener {
                    Log.d("Storage", "Successfully uploaded image ${it.metadata?.path}")
                    ref.downloadUrl.addOnSuccessListener { currUrl ->
                        uploadInfo(
                            username,
                            currUrl.toString(),
                            radius,
                            alert,
                            actionAfterLogged,
                            handleError
                        )
                    }.addOnFailureListener { currUrl ->
                        currUrl.message?.let { it1 -> handleError(it1) }
                    }
                }.addOnFailureListener {
                    it.message?.let { it1 -> handleError(it1) }
                }
        }

//        fun getInfo(processInfo: (String, String) -> Boolean, handleError: (String) -> Boolean) {
//            val uid = FirebaseAuth.getInstance().uid ?: ""
//            val ref = FirebaseDatabase.getInstance().getReference("/$USERS/$uid")
//            ref.get()
//                .addOnSuccessListener {
//                    Log.d("prof", "$uid $it")
//                    val hMap = it.value as HashMap<Any, Any>
//                    val username = hMap[USERNAME]!! as String
//                    val photo = hMap[PHOTO]!! as String
//                    processInfo(username, photo)
//                }
//                .addOnFailureListener {
//                    it.message?.let { it1 -> handleError(it1) }
//                }
//        }

        private fun uploadInfo(
            username: String,
            photoUrl: String,
            radius: Int,
            alert: Boolean,
            actionAfterLogged: (() -> Boolean)?,
            handleError: (String) -> Boolean
        ) {
            val uid = FirebaseAuth.getInstance().uid ?: ""
            val ref = FirebaseDatabase.getInstance().getReference("/$USERS/$uid")
            val userDetails = HashMap<String, Any>()
            userDetails[USERNAME] = username
            userDetails[PHOTO_URL] = photoUrl
            userDetails[RADIUS] = radius
            userDetails[ALERT] = alert
            ref.setValue(userDetails)
                .addOnSuccessListener {
                    Log.d("UserInfo", "info added: $username")
                    if (actionAfterLogged != null) {
                        actionAfterLogged()
                    }
                }
                .addOnFailureListener {
                    Log.d("UserInfo", "Failed to add info")
                    it.message?.let { it1 -> handleError(it1) }
                }
        }
    }
}