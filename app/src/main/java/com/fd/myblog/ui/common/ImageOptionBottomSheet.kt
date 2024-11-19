package com.fd.myblog.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fd.myblog.helper.ImageHelper

interface ImageOptionListener {
    fun onDismiss()
}

@Composable
fun ImageOptionBottomSheet(
    imageHelper: ImageHelper,
    userFrontCamera: Boolean = false,
    listener: ImageOptionListener
) {

    var showBottomSheet by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (showBottomSheet) {
            BottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                    listener.onDismiss()
                }
            ) {
                ImageOptionContent(
                    onCameraClick = {
                        imageHelper.fromCamera(userFrontCamera)
                    },
                    onGalleryClick = {
                        imageHelper.fromGallery()
                    },
                    onDismiss = {
                        showBottomSheet = false
                        listener.onDismiss()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = Modifier.fillMaxWidth()
    ) {
        content()
    }
}

@Composable
fun ImageOptionContent(
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Select Image From",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        HorizontalDivider()

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Camera",
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onCameraClick()
                    onDismiss()
                }
                .padding(16.dp)
        )
        HorizontalDivider()

        Text(
            text = "Gallery",
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onGalleryClick()
                    onDismiss()
                }
                .padding(16.dp)
        )
    }
}