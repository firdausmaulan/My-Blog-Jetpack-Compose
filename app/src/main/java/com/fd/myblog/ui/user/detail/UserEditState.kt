package com.fd.myblog.ui.user.detail

import com.fd.myblog.data.model.User

sealed class UserEditState {
    data object Idle : UserEditState()
    data object Loading : UserEditState()
    data class Error(val message: String) : UserEditState()
    data class Success(val user: User) : UserEditState()
}