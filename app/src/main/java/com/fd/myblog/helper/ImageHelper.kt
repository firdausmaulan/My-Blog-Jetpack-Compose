package com.fd.myblog.helper

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.hardware.Camera
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.fd.myblog.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import kotlin.math.min


class ImageHelper(private val activity: ComponentActivity) {

    companion object {
        const val MAX_DIMENSION: Int = 720
    }

    private lateinit var fileFromCamera: File
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryLauncher: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private val permissionHelper = PermissionHelper(activity)

    interface Listener {
        fun onImageCaptured(file: File)
        fun onImageSelected(file: File)
    }

    // Register the launchers in onCreate or onStart
    fun registerLaunchers(listener: Listener) {
        // Register the camera launcher
        cameraLauncher =
            activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    activity.lifecycleScope.launch {
                        val resizedImage = resizeImage(fileFromCamera)
                        listener.onImageCaptured(resizedImage)
                    }
                }
            }

        // Register the gallery launcher
        galleryLauncher =
            activity.registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                val fileFromUri = activity.getFileFromUri(uri)
                if (fileFromUri != null) {
                    activity.lifecycleScope.launch {
                        val resizedImage = resizeImage(fileFromUri)
                        listener.onImageSelected(resizedImage)
                    }
                } else {
                    Toast.makeText(activity, "Failed to select image", Toast.LENGTH_SHORT).show()
                }
            }

        permissionLauncher =
            activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                if (!permissions.all { it.value }) {
                    permissionHelper.showDeniedPermissionMessage()
                }
            }
        if (!permissionHelper.isMediaPermissionGranted()) {
            permissionHelper.requestMediaPermissions(permissionLauncher)
        }
    }

    // Camera method to launch the camera
    fun fromCamera(userFrontCamera: Boolean = false) {
        if (!permissionHelper.isMediaPermissionGranted()) {
            permissionHelper.requestMediaPermissions(permissionLauncher)
            return
        }
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        fileFromCamera = createImageFile()
        val uri = FileProvider.getUriForFile(
            activity,
            Constants.FILE_PROVIDER_AUTHORITY,
            fileFromCamera
        )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        if (userFrontCamera) {
            // Try to force the front camera
            intent.putExtra(
                "android.intent.extras.CAMERA_FACING",
                Camera.CameraInfo.CAMERA_FACING_FRONT
            )
            intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1)
            intent.putExtra("android.intent.extras.UseFrontCamera", true)
        }
        cameraLauncher.launch(intent)
    }

    // Gallery method to pick an image
    fun fromGallery() {
        if (!permissionHelper.isMediaPermissionGranted()) {
            permissionHelper.requestMediaPermissions(permissionLauncher)
            return
        }
        galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    // Helper method to create an image file
    private fun createImageFile(): File {
        val picturesDirectory =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val appDirectory = File(picturesDirectory, activity.getString(R.string.app_name))
        if (!appDirectory.exists()) {
            appDirectory.mkdirs()
        }
        val imageFileName = "IMG_${System.currentTimeMillis()}.jpg"
        return File(appDirectory, imageFileName)
    }

    // Helper method to convert Uri to a File
    private fun Context.getFileFromUri(uri: Uri?): File? {
        if (uri == null) return null
        // Generate a temporary file in the cache directory
        val tempFile = File.createTempFile("temp_image", null, cacheDir)
        tempFile.deleteOnExit()  // Automatically delete the file when the app closes

        try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val outputStream: OutputStream = FileOutputStream(tempFile)

            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            return tempFile  // Return the created temporary file
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    // Resize image method
    private suspend fun resizeImage(file: File): File {
        return withContext(Dispatchers.IO) {
            val originalBitmap = BitmapFactory.decodeFile(file.absolutePath)
            val scaleFactor = calculateScaleFactor(originalBitmap.width, originalBitmap.height)
            val resizedBitmap: Bitmap = if (scaleFactor.toDouble() == 1.0) {
                originalBitmap
            } else {
                resizeBitmap(originalBitmap, scaleFactor)
            }
            FileOutputStream(file).use { outStream ->
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outStream)
            }
            file
        }
    }

    // Calculate scale factor for resizing
    private fun calculateScaleFactor(width: Int, height: Int): Float {
        var scaleFactor = 1.0f
        if (width > MAX_DIMENSION || height > MAX_DIMENSION) {
            val widthScale = MAX_DIMENSION.toFloat() / width
            val heightScale = MAX_DIMENSION.toFloat() / height
            scaleFactor = min(widthScale.toDouble(), heightScale.toDouble()).toFloat()
        }
        return scaleFactor
    }

    // Resize bitmap to maintain aspect ratio
    private fun resizeBitmap(bitmap: Bitmap, scaleFactor: Float): Bitmap {
        val newWidth = Math.round(bitmap.width * scaleFactor)
        val newHeight = Math.round(bitmap.height * scaleFactor)
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
}
