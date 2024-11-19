package com.fd.myblog.ui.location.search

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.fd.myblog.helper.Constants
import com.fd.myblog.ui.theme.MyBlogTheme
import org.koin.android.ext.android.inject


class SearchLocationActivity : ComponentActivity() {

    private val viewModel: SearchLocationViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val location = intent.getStringExtra(Constants.KEY_LOCATION)
        val locationData = viewModel.setLocationFromJson(location)
        if (locationData != null) viewModel.setLocation(locationData)

        setContent {
            MyBlogTheme {
                SearchLocationScreen(
                    viewModel,
                    onClose = { finish() },
                    onConfirm = {
                        val intent = Intent()
                        intent.putExtra(Constants.KEY_LOCATION, viewModel.setLocationToJson(it))
                        setResult(RESULT_OK, intent)
                        finish()
                    }
                )
            }
        }
    }
}