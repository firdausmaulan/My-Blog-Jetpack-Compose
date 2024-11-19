package com.fd.myblog.ui.user.changepassword

import com.fd.myblog.data.model.User

sealed class UserChangePasswordState {
    object Idle : UserChangePasswordState()
    object Loading : UserChangePasswordState()
    data class Error(val message: String) : UserChangePasswordState()
    data class Success(val user: User) : UserChangePasswordState()
}