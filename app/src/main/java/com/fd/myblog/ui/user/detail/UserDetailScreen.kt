package com.fd.myblog.ui.user.detail

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.fd.myblog.R
import com.fd.myblog.data.model.User
import com.fd.myblog.data.remote.request.UserFormRequest
import com.fd.myblog.helper.ImageHelper
import com.fd.myblog.helper.UiHelper
import com.fd.myblog.ui.common.ConfirmationBottomSheet
import com.fd.myblog.ui.common.ErrorBottomSheetDialog
import com.fd.myblog.ui.common.ErrorScreen
import com.fd.myblog.ui.common.ImageOptionBottomSheet
import com.fd.myblog.ui.common.ImageOptionListener
import com.fd.myblog.ui.common.LoadingButton
import com.fd.myblog.ui.common.LoadingScreen
import com.fd.myblog.ui.common.MapScreen
import com.fd.myblog.ui.common.SuccessBottomSheetDialog
import com.fd.myblog.ui.common.UserSettingBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(
    viewModel: UserDetailViewModel,
    imageHelper: ImageHelper,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onEditLocation: () -> Unit,
    onChangePassword: () -> Unit,
    onLogout: () -> Unit
) {

    var showImageOptionBottomSheet by remember { mutableStateOf(false) }
    var showBottomSheetSetting by remember { mutableStateOf(false) }
    var showBottomSheetConfirmation by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = UiHelper.topAppBarColors(),
                title = {
                    Text(
                        "Profile",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showBottomSheetSetting = true }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_user_setting),
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (val state = viewModel.state) {
                is UserDetailState.Loading -> {
                    LoadingScreen()
                }

                is UserDetailState.Error -> {
                    ErrorScreen(message = state.message)
                }

                is UserDetailState.Success -> {
                    UserDetailContent(
                        user = state.user,
                        viewModel = viewModel,
                        onShowImageOptionBottomSheet = {
                            showImageOptionBottomSheet = true
                        },
                        onBack = { onBack() },
                        onEditLocation = { onEditLocation() }
                    )
                }
            }
        }

        // Show bottom sheet when settings icon is clicked
        if (showBottomSheetSetting) {
            UserSettingBottomSheet(
                onDismissRequest = {
                    showBottomSheetSetting = false
                },
                onEditClick = {
                    onEdit()
                    showBottomSheetSetting = false
                },
                onChangePasswordClick = {
                    onChangePassword()
                    showBottomSheetSetting = false
                },
                onLogoutClick = {
                    showBottomSheetConfirmation = true
                    showBottomSheetSetting = false
                }
            )
        }

        if (showBottomSheetConfirmation) {
            ConfirmationBottomSheet(
                onDismissClick = {
                    showBottomSheetConfirmation = false
                },
                onConfirmClick = {
                    onLogout()
                }
            )
        }

        if (showImageOptionBottomSheet) {
            ImageOptionBottomSheet(
                imageHelper = imageHelper,
                userFrontCamera = true,
                listener = object : ImageOptionListener {
                    override fun onDismiss() {
                        showImageOptionBottomSheet = false
                    }
                })
        }
    }
}

@Composable
fun UserDetailContent(
    user: User,
    viewModel: UserDetailViewModel,
    onShowImageOptionBottomSheet: () -> Unit,
    onBack: () -> Unit,
    onEditLocation: () -> Unit
) {

    var name by remember { mutableStateOf(user.name) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(state = rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = if (viewModel.isEdit && viewModel.image != null) viewModel.image else user.imageUrl,
            contentDescription = "Profile Image",
            modifier = Modifier
                .height(120.dp)
                .width(120.dp)
                .padding(8.dp)
                .clip(CircleShape)
                .clickable {
                    if (viewModel.isEdit) {
                        onShowImageOptionBottomSheet()
                    }
                },
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.placeholder_image),
            error = painterResource(id = R.drawable.placeholder_image)
        )

        OutlinedTextField(
            colors = UiHelper.textFieldCustomColors(),
            value = name.toString(),
            onValueChange = {
                name = it
                viewModel.nameError = it.isEmpty()
            },
            readOnly = !viewModel.isEdit,
            label = { Text("Name") },
            isError = viewModel.nameError,
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
        if (viewModel.nameError) {
            Text(
                text = "Name is required",
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }

        OutlinedTextField(
            colors = UiHelper.textFieldCustomColors(),
            value = if (user.email.isNullOrEmpty()) "" else user.email.toString(),
            onValueChange = { },
            readOnly = true,
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        if (!viewModel.address.isNullOrEmpty()) {
            OutlinedTextField(
                colors = UiHelper.textFieldCustomColors(),
                value = viewModel.address.toString(),
                onValueChange = {},
                readOnly = true,
                label = { Text("Address") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.small
                    )
                    .clip(MaterialTheme.shapes.small)
            ) {
                // Display the map view
                MapScreen(viewModel.latitude, viewModel.longitude)

                if (viewModel.isEdit) {
                    // Edit button
                    Button(
                        onClick = onEditLocation,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                    ) {
                        Text(text = "Edit")
                    }
                }
            }
        }

        when (val state = viewModel.stateEdit) {
            is UserEditState.Idle -> {
                val userFormRequest = UserFormRequest(
                    name = name,
                    address = viewModel.address,
                    latitude = viewModel.latitude,
                    longitude = viewModel.longitude,
                    image = viewModel.image,
                )
                if (viewModel.isEdit) {
                    SubmitButton(viewModel, userFormRequest)
                }
            }

            is UserEditState.Loading -> LoadingButton()
            is UserEditState.Error -> {
                ErrorBottomSheetDialog(
                    onDismissRequest = {
                        viewModel.stateEdit = UserEditState.Idle
                    },
                    subMessage = state.message
                )
            }

            is UserEditState.Success -> {
                SuccessBottomSheetDialog(onDismissRequest = {
                    onBack()
                })
            }
        }
    }
}

@Composable
fun SubmitButton(viewModel: UserDetailViewModel, userFormRequest: UserFormRequest) {
    Button(
        onClick = {
            viewModel.edit(userFormRequest)
        },
        elevation = UiHelper.buttonElevation(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text("SUBMIT")
    }
}