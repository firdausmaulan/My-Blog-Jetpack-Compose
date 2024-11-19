package com.fd.myblog.ui.user.register

import com.fd.myblog.data.model.User

sealed class UserRegisterState {
    data object Idle : UserRegisterState()
    data object Loading : UserRegisterState()
    data class Error(val message: String) : UserRegisterState()
    data class Success(val user: User) : UserRegisterState()
}