package ge.mchkhaidze.safetynet.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import androidx.preference.SwitchPreference
import com.google.firebase.auth.FirebaseAuth
import ge.mchkhaidze.safetynet.ErrorHandler
import ge.mchkhaidze.safetynet.ImagePreference
import ge.mchkhaidze.safetynet.R
import ge.mchkhaidze.safetynet.model.User
import ge.mchkhaidze.safetynet.model.User.Companion.DEFAULT_IMAGE
import ge.mchkhaidze.safetynet.service.UserInfoService
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class PreferencesFragment : PreferenceFragmentCompat(), ErrorHandler {

    private var selectedImageUri: Uri? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference, rootKey)

        UserInfoService.getUserInfo(
            FirebaseAuth.getInstance().uid ?: "",
            this::setCurrentUser,
            this::handleError
        )

        val profilePicturePref = findPreference<ImagePreference>("user_photo")
        profilePicturePref?.setOnPreferenceClickListener {
            launchImagePicker()
            true
        }
    }

    private fun setCurrentUser(user: User?): Boolean {
        if (user != null) {
            val image = findPreference<ImagePreference>("user_photo")
            val username = findPreference<EditTextPreference>("username")
            val distance = findPreference<SeekBarPreference>("radius")
            val notifications = findPreference<SwitchPreference>("notifications")
            val alerts = findPreference<SwitchPreference>("alerts")

            if (user.photo_url != "") {
                image?.setImage(user.photo_url)
            } else {
                image?.setImage(DEFAULT_IMAGE)
            }

            if (username != null) {
                username.title = user.username!!

                username.setOnPreferenceChangeListener { _, newValue ->
                    username.title = newValue.toString()
                    true
                }
            }

            if (distance != null) {
                distance.value = user.radius!!
            }

            if (notifications != null) {
                notifications.isChecked = user.notifications!!
            }

            if (alerts != null) {
                alerts.isChecked = user.alert!!
            }
        } else {
            handleError("error loading user")
        }
        return true
    }

    override fun handleError(err: String): Boolean {
        return true
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST_CODE) {
                if (data?.data != null) {
                    selectedImageUri = data.data
                }

                val profilePicturePref = findPreference<ImagePreference>("user_photo")
                profilePicturePref?.setImage(selectedImageUri)
            }
        }
    }

    private fun launchImagePicker() {
        val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        try {
            val photoFile = createImageFile()
            selectedImageUri = FileProvider.getUriForFile(
                requireContext(),
                "ge.mchkhaidze.safetynet.fileprovider",
                photoFile
            )
            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, selectedImageUri)
        } catch (e: IOException) {
            Log.e("error", "Error creating image file: ${e.message}")
        }

        val chooserIntent = Intent.createChooser(pickIntent, "Select Profile Picture")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(takePhotoIntent))

        startActivityForResult(chooserIntent, PICK_IMAGE_REQUEST_CODE)
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val fileName = "JPEG_${timeStamp}_"
        val storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile(fileName, ".jpg", storageDir)
    }

    companion object {
        private const val PICK_IMAGE_REQUEST_CODE = 1
        private const val REQUEST_GALLERY_PERMISSION = 100
        private const val REQUEST_CAMERA_PERMISSION = 200
    }
}