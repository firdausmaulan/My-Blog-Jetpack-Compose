package com.fd.myblog.data.remote.request

data class UserLoginRequest(
    val email: String? = null,
    val password: String? = null,
)