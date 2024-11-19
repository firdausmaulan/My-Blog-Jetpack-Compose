package com.fd.myblog.ui.user.register

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.fd.myblog.helper.Constants
import com.fd.myblog.helper.ImageHelper
import com.fd.myblog.helper.PermissionHelper
import com.fd.myblog.ui.location.search.SearchLocationActivity
import com.fd.myblog.ui.theme.MyBlogTheme
import org.koin.android.ext.android.inject
import org.osmdroid.config.Configuration
import java.io.File

class UserRegisterActivity : ComponentActivity() {

    private val viewModel: UserRegisterViewModel by inject()
    private lateinit var imageHelper: ImageHelper
    private val permissionHelper = PermissionHelper(this)
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var locationLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().userAgentValue = packageName
        registerLocationResultLauncher()

        registerPermissionsResultLauncher()
        if (permissionHelper.isLocationPermissionGranted()) {
            viewModel.fetchLocationAndAddress()
        } else {
            permissionHelper.requestLocationPermission(permissionLauncher)
        }

        imageHelper = ImageHelper(this)
        imageHelper.registerLaunchers(object : ImageHelper.Listener {
            override fun onImageCaptured(file: File) {
                viewModel.image = file
            }

            override fun onImageSelected(file: File) {
                viewModel.image = file
            }
        })

        setContent {
            MyBlogTheme {
                UserRegisterScreen(
                    viewModel = viewModel,
                    imageHelper = imageHelper,
                    onClose = { finish() },
                    onEditLocation = { openSearchLocationActivity() }
                )
            }
        }
    }

    private fun registerPermissionsResultLauncher() {
        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                val isLocationGranted = permissionHelper.isLocationPermissionGranted()
                if (isLocationGranted) {
                    viewModel.fetchLocationAndAddress()
                } else {
                    permissionHelper.showDeniedPermissionMessage()
                }
            }
    }

    private fun registerLocationResultLauncher() {
        locationLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val location = result.data?.getStringExtra(Constants.KEY_LOCATION)
                    viewModel.setLocation(location)
                }
            }
    }

    private fun openSearchLocationActivity() {
        if (!permissionHelper.isLocationPermissionGranted()) {
            permissionHelper.showDeniedPermissionMessage()
            permissionHelper.requestLocationPermission(permissionLauncher)
            return
        }
        val intent = Intent(this, SearchLocationActivity::class.java)
        intent.putExtra(Constants.KEY_LOCATION, viewModel.setLocationToJson())
        locationLauncher.launch(intent)
    }
}