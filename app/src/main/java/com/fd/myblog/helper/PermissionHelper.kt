package com.fd.myblog.helper

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat

class PermissionHelper(private val context: Context) {

    /**
     * Checks if a specific permission is granted.
     */
    private fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Requests location permissions.
     */
    fun requestLocationPermission(requestPermissionsLauncher: ActivityResultLauncher<Array<String>>) {
        val locationPermissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        requestPermissionsLauncher.launch(locationPermissions)
    }

    /**
     * Requests media access permissions based on API level.
     */
    fun requestMediaPermissions(requestPermissionsLauncher: ActivityResultLauncher<Array<String>>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            requestPermissionsLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                )
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionsLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO
                )
            )
        } else {
            requestPermissionsLauncher.launch(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE))
        }
    }

    /**
     * Checks if location permissions are granted.
     */
    fun isLocationPermissionGranted(): Boolean {
        return isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION) ||
                isPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    /**
     * Checks if media permissions are granted based on API level.
     */
    fun isMediaPermissionGranted(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        return true
        /*return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            isPermissionGranted(Manifest.permission.READ_MEDIA_IMAGES) &&
                    isPermissionGranted(Manifest.permission.READ_MEDIA_VIDEO) &&
                    isPermissionGranted(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            isPermissionGranted(Manifest.permission.READ_MEDIA_IMAGES) &&
                    isPermissionGranted(Manifest.permission.READ_MEDIA_VIDEO)
        } else {
            isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }*/
    }

    fun showDeniedPermissionMessage() {
        Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
    }
}
