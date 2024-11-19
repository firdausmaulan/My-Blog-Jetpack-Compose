package com.fd.myblog.ui.user.detail

import com.fd.myblog.data.model.User

sealed class UserDetailState {
    data object Loading : UserDetailState()
    data class Error(val message: String) : UserDetailState()
    data class Success(val user: User) : UserDetailState()
}