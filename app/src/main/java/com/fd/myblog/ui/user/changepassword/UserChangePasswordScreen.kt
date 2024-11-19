package com.fd.myblog.ui.user.changepassword

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fd.myblog.R
import com.fd.myblog.data.remote.request.UserFormRequest
import com.fd.myblog.helper.UiHelper
import com.fd.myblog.ui.common.ErrorBottomSheetDialog
import com.fd.myblog.ui.common.LoadingButton
import com.fd.myblog.ui.common.SuccessBottomSheetDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserChangePasswordScreen(
    viewModel: UserChangePasswordViewModel,
    onBack: () -> Unit
) {

    var password by remember { mutableStateOf("") }
    var passwordConfirmation by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    Scaffold(topBar = {
        TopAppBar(
            colors = UiHelper.topAppBarColors(),
            title = {
                Text(
                    "Change Password",
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
            }
        )
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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

            Spacer(modifier = Modifier.height(16.dp))

            when (val state = viewModel.state) {
                is UserChangePasswordState.Idle -> {
                    val userFormRequest = UserFormRequest(
                        password = password,
                        passwordConfirmation = passwordConfirmation
                    )
                    SubmitButton(viewModel, userFormRequest)
                }

                is UserChangePasswordState.Loading -> {
                    LoadingButton()
                }

                is UserChangePasswordState.Error -> {
                    ErrorBottomSheetDialog(
                        onDismissRequest = {
                            viewModel.state = UserChangePasswordState.Idle
                        },
                        subMessage = state.message
                    )
                }

                is UserChangePasswordState.Success -> {
                    SuccessBottomSheetDialog(onDismissRequest = {
                        onBack()
                    })
                }
            }
        }
    }
}

@Composable
fun SubmitButton(viewModel: UserChangePasswordViewModel, userFormRequest: UserFormRequest) {
    Button(
        onClick = {
            viewModel.changePassword(userFormRequest)
        },
        elevation = UiHelper.buttonElevation(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("SUBMIT")
    }
}
