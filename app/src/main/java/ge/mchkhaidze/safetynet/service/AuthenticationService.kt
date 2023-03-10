package ge.mchkhaidze.safetynet.service

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import ge.mchkhaidze.safetynet.model.UserInfo.Companion.DEFAULT_ALERT
import ge.mchkhaidze.safetynet.model.UserInfo.Companion.DEFAULT_NOTIFICATIONS
import ge.mchkhaidze.safetynet.model.UserInfo.Companion.DEFAULT_RADIUS
import ge.mchkhaidze.safetynet.service.UserInfoService.Companion.uploadUserInformation

class AuthenticationService {
    companion object {

        fun signUp(
            username: String,
            email: String,
            password: String,
            actionAfterLogged: () -> Boolean,
            handleError: (String) -> Boolean
        ) {
            if (email.isEmpty() || password.isEmpty()) {
                return
            }
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (!it.isSuccessful) {
                        it.exception?.message?.let { it1 -> handleError(it1) }
                        return@addOnCompleteListener
                    }
                    Log.d(
                        "SignUp",
                        "User created with credentials: ${it.result!!.user!!.uid}, $email, $password"
                    )

                    uploadUserInformation(
                        if (username == "") {
                            "user_" + (0..1000000000).random()
                        } else {
                            username
                        },
                        null,
                        "",
                        DEFAULT_RADIUS,
                        DEFAULT_NOTIFICATIONS,
                        DEFAULT_ALERT,
                        actionAfterLogged,
                        handleError
                    )
                }
                .addOnFailureListener {
                    it.message?.let { it1 -> handleError(it1) }
                    Log.d(
                        "SignUp",
                        "Failed to create user: ${it.message}"
                    )
                }
        }

        fun logIn(
            email: String,
            password: String,
            actionAfterLogged: () -> Boolean,
            handleError: (String) -> Boolean
        ) {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (!it.isSuccessful) {
                        it.exception?.message?.let { it1 -> handleError(it1) }
                        return@addOnCompleteListener
                    }
                    Log.d(
                        "LogIn",
                        "User Logged in with credentials: $email, $password"
                    )
                    actionAfterLogged()
                }
                .addOnFailureListener {
                    it.message?.let { it1 -> handleError(it1) }
                    Log.d(
                        "LogIn",
                        "Failed to log in user: ${it.message}"
                    )
                }
        }
    }

}
