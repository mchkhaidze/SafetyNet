package ge.mchkhaidze.safetynet

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.location.*
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ge.mchkhaidze.safetynet.adapter.UploadedContentAdapter
import ge.mchkhaidze.safetynet.service.PostsService
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class NewPostFragment : BottomSheetDialogFragment(), ErrorHandler {

    companion object {
        private const val REQUEST_GALLERY_PERMISSION = 100
        private const val REQUEST_CAMERA_PERMISSION = 200
    }

    private var selectedImageUri: Uri? = null
    private var adapter = UploadedContentAdapter()

    private lateinit var gallery: ImageButton
    private lateinit var camera: ImageButton
    private lateinit var description: EditText
    private lateinit var parentView: View
    private lateinit var globalLayoutListener: OnGlobalLayoutListener
    private lateinit var sendButton: ImageButton
    private lateinit var resourceRv: RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_new_post, container, false)
        resourceRv = view.findViewById(R.id.content_rv)
        resourceRv.adapter = adapter
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

        setUpButton()

        parentView = requireActivity().window.decorView
        globalLayoutListener = OnGlobalLayoutListener {
            val rect = Rect()
            view?.getWindowVisibleDisplayFrame(rect)
            val screenHeight = view?.rootView?.height ?: 0
            val keypadHeight = screenHeight - rect.bottom

            val fragmentView =
                dialog?.findViewById<View>(R.id.fragment) ?: return@OnGlobalLayoutListener
            val layoutParams = fragmentView.layoutParams as ViewGroup.LayoutParams
            if (keypadHeight > screenHeight * 0.15) {
                layoutParams.height = (screenHeight - keypadHeight).toInt()
            } else {
                layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            }
            fragmentView.layoutParams = layoutParams
        }

//        globalLayoutListener = OnGlobalLayoutListener {
//            val rect = Rect()
//            parentView.getWindowVisibleDisplayFrame(rect)
//            val screenHeight = parentView.rootView.height
//            val keypadHeight = screenHeight - rect.bottom
//
//            val bottomSheet = dialog!!.findViewById<View>(R.id.fragment) as FrameLayout
//            if (keypadHeight > screenHeight * 0.15) {
//                bottomSheet.layoutParams.height = (screenHeight - keypadHeight).toInt()
//            } else {
//                bottomSheet.layoutParams.height = FrameLayout.LayoutParams.MATCH_PARENT
//            }
//            bottomSheet.requestLayout()
//        }
        parentView.viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)

        return view
    }

    private fun setUpButton() {
        sendButton.setOnClickListener {
            if (description.text.isNotEmpty() || adapter.list.isNotEmpty()) {
                val text = description.text
                val location: Pair<Double, Double>? = getCurrentLocation()

                var addressString = ""
                if (location != null) {
                    addressString = try {
                        val geocoder = Geocoder(requireContext(), Locale.getDefault())
                        val addresses: List<Address> =
                            geocoder.getFromLocation(location.first, location.second, 1)!!
                        val address = addresses[0]
                        address.getAddressLine(0)
                    } catch (ex: Exception) {
                        ""
                    }

                }

                if (location != null) {
                    PostsService.uploadPost(
                        text.toString(),
                        location,
                        addressString,
                        Utils.getFormattedDate(),
                        System.currentTimeMillis(),
                        adapter.list,
                        this::dismissFragment,
                        this::handleError
                    )
                    dismiss()
                } else {
                    Utils.showWarning("Location permission is required", sendButton)
                }
            }
        }
    }

    private fun dismissFragment(): Boolean {
        dismiss()
        return true
    }

    override fun handleError(err: String): Boolean {
        Utils.showWarning(err, description)
        return true
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
                val selectedImages: List<Uri> = if (data?.clipData != null) {
                    val count = data.clipData!!.itemCount
                    (0 until count).map { data.clipData!!.getItemAt(it).uri }
                } else {
                    listOf(data?.data!!)
                }

                adapter.list.addAll(selectedImages)
                adapter.notifyDataSetChanged()
            }
        }

    private fun openCameraActivityForResult() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile = createImageFile()
        selectedImageUri = FileProvider.getUriForFile(
            requireContext(),
            "ge.mchkhaidze.safetynet.fileprovider",
            photoFile
        )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, selectedImageUri)
        cameraResultLauncher.launch(intent)
    }

    private var cameraResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                if (selectedImageUri != null) {
                    adapter.list.add(selectedImageUri!!)
                    adapter.notifyDataSetChanged()

                }
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

        locationManager.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            0,
            0f,
            object : LocationListener {
                override fun onLocationChanged(location: Location) {}

                override fun onProviderEnabled(provider: String) {}

                override fun onProviderDisabled(provider: String) {}

                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            })

        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            0,
            0f,
            object : LocationListener {
                override fun onLocationChanged(location: Location) {}

                override fun onProviderEnabled(provider: String) {}

                override fun onProviderDisabled(provider: String) {}

                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            })

        val location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

        if (location != null) {
            val latitude = location.latitude
            val longitude = location.longitude
            return Pair(latitude, longitude)
        }
        return null
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val fileName = "JPEG_${timeStamp}_"
        val storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile(fileName, ".jpg", storageDir)
    }
}