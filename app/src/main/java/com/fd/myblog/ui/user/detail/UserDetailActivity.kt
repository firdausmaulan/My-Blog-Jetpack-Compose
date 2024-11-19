package com.fd.myblog.ui.user.detail

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.fd.myblog.helper.Constants
import com.fd.myblog.helper.ImageHelper
import com.fd.myblog.helper.PermissionHelper
import com.fd.myblog.ui.blog.list.BlogListActivity
import com.fd.myblog.ui.location.search.SearchLocationActivity
import com.fd.myblog.ui.theme.MyBlogTheme
import com.fd.myblog.ui.user.changepassword.UserChangePasswordActivity
import org.koin.android.ext.android.inject
import org.osmdroid.config.Configuration
import java.io.File

class UserDetailActivity : ComponentActivity() {

    private val viewModel: UserDetailViewModel by inject()
    private lateinit var imageHelper: ImageHelper
    private val usedId: Int by lazy { intent.getIntExtra(Constants.KEY_USER_ID, 0) }
    private val permissionHelper = PermissionHelper(this)
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var locationLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().userAgentValue = packageName
        registerLocationResultLauncher()

        registerPermissionsResultLauncher()
        if (!permissionHelper.isLocationPermissionGranted()) {
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

        viewModel.fetchUserDetail(usedId)

        setContent {
            MyBlogTheme {
                UserDetailScreen(
                    viewModel = viewModel,
                    imageHelper = imageHelper,
                    onBack = { finish() },
                    onEdit = { editUser() },
                    onEditLocation = { openSearchLocationActivity() },
                    onChangePassword = {
                        navigateToChangePassword()
                    },
                    onLogout = { logout() }
                )
            }
        }
    }

    private fun registerPermissionsResultLauncher() {
        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                val isLocationGranted = permissionHelper.isLocationPermissionGranted()
                if (!isLocationGranted) permissionHelper.showDeniedPermissionMessage()
            }
    }

    private fun registerLocationResultLauncher() {
        locationLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val location = result.data?.getStringExtra(Constants.KEY_LOCATION)
                    viewModel.setLocationFromJson(location)
                }
            }
    }

    private fun editUser() {
        viewModel.isEdit = true
        if (viewModel.address.isNullOrEmpty()) {
            viewModel.fetchLocationAndAddress()
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

    private fun navigateToChangePassword() {
        val intent = Intent(this, UserChangePasswordActivity::class.java)
        intent.putExtra(Constants.KEY_USER_ID, usedId)
        startActivity(intent)
    }

    private fun logout() {
        viewModel.clearSession()
        val intent = Intent(this, BlogListActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}