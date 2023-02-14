package ge.mchkhaidze.safetynet

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.location.LocationManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class NewPostFragment : BottomSheetDialogFragment() {

    companion object {
        private const val REQUEST_GALLERY_PERMISSION = 100
        private const val REQUEST_CAMERA_PERMISSION = 200
        private const val REQUEST_CODE_LOCATION_PERMISSION = 300
    }

    private var resources: List<MediaStore.Files> = listOf()

    private lateinit var gallery: ImageButton
    private lateinit var camera: ImageButton
    private lateinit var description: EditText
    private lateinit var parentView: View
    private lateinit var globalLayoutListener: OnGlobalLayoutListener
    private lateinit var sendButton: ImageButton


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_new_post, container, false)
        gallery = view.findViewById(R.id.gallery)
        camera = view.findViewById(R.id.camera)
        sendButton = view.findViewById(R.id.send_button)
        description = view.findViewById(R.id.input_text)
        gallery.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    view.context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_GALLERY_PERMISSION
                )
            } else {
                openGalleryActivityForResult()
            }
        }

        camera.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    view.context,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
            } else {
                openCameraActivityForResult()
            }
        }
        parentView = requireActivity().window.decorView
        globalLayoutListener = OnGlobalLayoutListener {
            val rect = Rect()
            parentView.getWindowVisibleDisplayFrame(rect)
            val screenHeight = parentView.rootView.height
            val keypadHeight = screenHeight - rect.bottom

            val bottomSheet = dialog!!.findViewById<View>(R.id.fragment) as FrameLayout
            if (keypadHeight > screenHeight * 0.15) {
                bottomSheet.layoutParams.height = (screenHeight - keypadHeight).toInt()
            } else {
                bottomSheet.layoutParams.height = FrameLayout.LayoutParams.MATCH_PARENT
            }
            bottomSheet.requestLayout()
        }
        parentView.viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)

        setUpdButton()

        return view
    }

    private fun setUpdButton() {
        sendButton.setOnClickListener {
            if (description.text.isNotEmpty() || resources.isNotEmpty()) {
                val text = description.text
                val location: Pair<Double, Double>? = getCurrentLocation()

                if (location != null) {
                    val locationLat = location.first
                    val locationLong = location.second
                    // savePost(text, location, resources)
                    dismiss()
                } else {
                    Utils.showWarning("Location permission is required", sendButton)
                }
            }
        }
    }


    private fun openGalleryActivityForResult() {
        val intent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        galleryResultLauncher.launch(intent)
    }

    private var galleryResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                Log.d("TAG123", data.toString())
            }
        }

    private fun openCameraActivityForResult() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraResultLauncher.launch(intent)
    }

    private var cameraResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val image = result?.data
                Log.d("123", image.toString())
            }
        }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCameraActivityForResult()
                } else {
                    Utils.showWarning("Permission is required", camera)
                }
                return
            }
            REQUEST_GALLERY_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGalleryActivityForResult()
                } else {
                    Utils.showWarning("Permission is required", gallery)
                }
                return
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        parentView.viewTreeObserver.removeOnGlobalLayoutListener(globalLayoutListener)
    }

    private fun getCurrentLocation(): Pair<Double, Double>? {

        val locationManager =
            activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return null
        }

        val location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

        if (location != null) {
            val latitude = location.latitude
            val longitude = location.longitude
            return Pair(latitude, longitude)
        }
        return null
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_LOCATION_PERMISSION
            )
        }
    }
}