package com.fd.myblog.ui.user.register

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.fd.myblog.R
import com.fd.myblog.data.remote.request.UserFormRequest
import com.fd.myblog.helper.ImageHelper
import com.fd.myblog.helper.UiHelper
import com.fd.myblog.ui.common.ErrorBottomSheetDialog
import com.fd.myblog.ui.common.ImageOptionBottomSheet
import com.fd.myblog.ui.common.ImageOptionListener
import com.fd.myblog.ui.common.LoadingButton
import com.fd.myblog.ui.common.MapScreen
import com.fd.myblog.ui.common.SuccessBottomSheetDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserRegisterScreen(
    viewModel: UserRegisterViewModel,
    imageHelper: ImageHelper,
    onClose: () -> Unit,
    onEditLocation: () -> Unit
) {

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordConfirmation by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var showImageOptionBottomSheet by remember { mutableStateOf(false) }

    Scaffold(topBar = {
        TopAppBar(
            colors = UiHelper.topAppBarColors(),
            title = {
                Text(
                    "Register",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold
                )
            },
            actions = {
                IconButton(onClick = { onClose() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = "Close"
                    )
                }
            }
        )
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(state = rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(
                Modifier
                    .fillMaxWidth()
                    .height(8.dp)
            )

            AsyncImage(
                model = viewModel.image,
                contentDescription = "Profile Image",
                modifier = Modifier
                    .height(120.dp)
                    .width(120.dp)
                    .padding(8.dp)
                    .clip(CircleShape)
                    .clickable {
                        showImageOptionBottomSheet = true
                    },
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.placeholder_image),
                error = painterResource(id = R.drawable.placeholder_image)
            )

            // Name Input with Validation
            OutlinedTextField(
                colors = UiHelper.textFieldCustomColors(),
                value = name,
                onValueChange = {
                    name = it
                    viewModel.nameError = it.isEmpty()
                },
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

            // Email Input with Validation
            OutlinedTextField(
                colors = UiHelper.textFieldCustomColors(),
                value = email,
                onValueChange = {
                    email = it
                    viewModel.emailError = !viewModel.isValidEmail(it)
                },
                label = { Text("Email") },
                isError = viewModel.emailError,
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            if (viewModel.emailError) {
                Text(
                    text = "Invalid email format",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                )
            }

            // Password Input with Validation
            OutlinedTextField(
                colors = UiHelper.textFieldCustomColors(),
                value = password,
                onValueChange = {
                    password = it
                    viewModel.passwordError = it.length < 8
                },
                label = { Text("Password") },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = viewModel.passwordError,
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible })
                    {
                        Icon(
                            if (isPasswordVisible) painterResource(id = R.drawable.ic_visible)
                            else painterResource(id = R.drawable.ic_visible_off),
                            contentDescription = "Password visibility toggle"
                        )
                    }
                }
            )
            if (viewModel.passwordError) {
                Text(
                    text = "Password must be at least 8 characters",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                )
            }

            // Password Confirmation
            OutlinedTextField(
                colors = UiHelper.textFieldCustomColors(),
                value = passwordConfirmation,
                onValueChange = {
                    passwordConfirmation = it
                    viewModel.passwordNotMatch = it != password
                },
                label = { Text("Confirm Password") },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = viewModel.passwordNotMatch,
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible })
                    {
                        Icon(
                            if (isPasswordVisible) painterResource(id = R.drawable.ic_visible)
                            else painterResource(id = R.drawable.ic_visible_off),
                            contentDescription = "Password visibility toggle"
                        )
                    }
                }
            )
            if (viewModel.passwordNotMatch) {
                Text(
                    text = "Passwords doesn't match",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                )
            }

            // Address
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

            when (val state = viewModel.state) {
                is UserRegisterState.Idle -> {
                    val userFormRequest = UserFormRequest(
                        name = name,
                        email = email,
                        password = password,
                        passwordConfirmation = passwordConfirmation,
                        address = viewModel.address,
                        latitude = viewModel.latitude,
                        longitude = viewModel.longitude,
                        image = viewModel.image,
                    )
                    SubmitButton(viewModel, userFormRequest)
                    Spacer(
                        Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                    )
                }

                is UserRegisterState.Loading -> LoadingButton()
                is UserRegisterState.Error -> {
                    ErrorBottomSheetDialog(
                        onDismissRequest = {
                            viewModel.state = UserRegisterState.Idle
                        },
                        subMessage = state.message
                    )
                }

                is UserRegisterState.Success -> {
                    SuccessBottomSheetDialog(onDismissRequest = {
                        onClose()
                    })
                }
            }

        }
    }
    // Show ImageOptionBottomSheet if showBottomSheet is true
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

@Composable
fun SubmitButton(viewModel: UserRegisterViewModel, userFormRequest: UserFormRequest) {
    Button(
        onClick = {
            viewModel.register(userFormRequest)
        },
        elevation = UiHelper.buttonElevation(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text("SUBMIT")
    }
}