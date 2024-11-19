package com.fd.myblog.ui.user.changepassword

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fd.myblog.data.local.preference.AppPreference
import com.fd.myblog.data.remote.api.core.Result
import com.fd.myblog.data.remote.api.service.UserApiService
import com.fd.myblog.data.remote.request.UserFormRequest
import com.fd.myblog.ui.user.detail.UserEditState
import kotlinx.coroutines.launch

class UserChangePasswordViewModel(
    private val userApiService: UserApiService
) : ViewModel() {

    var state by mutableStateOf<UserChangePasswordState>(UserChangePasswordState.Idle)

    var userId : Int? = null

    var passwordError by mutableStateOf(false)

    var passwordNotMatch by mutableStateOf(false)

    private fun isValidRequest(userFormRequest: UserFormRequest): Boolean {
        return userFormRequest.password != null &&
                userFormRequest.passwordConfirmation != null &&
                userFormRequest.password.length >= 6 &&
                userFormRequest.password == userFormRequest.passwordConfirmation
    }

    fun changePassword(userFormRequest: UserFormRequest) {
        if (!isValidRequest(userFormRequest)) return
        viewModelScope.launch {
            state = UserChangePasswordState.Loading
            val result = userApiService.updateUser(userId, userFormRequest)
            when (result) {
                is Result.Success -> {
                    state = UserChangePasswordState.Success(result.data.user)
                }

                is Result.Error -> {
                    state = UserChangePasswordState.Error("Failed to update password: ${result.error}")
                }
            }
        }
    }
}
