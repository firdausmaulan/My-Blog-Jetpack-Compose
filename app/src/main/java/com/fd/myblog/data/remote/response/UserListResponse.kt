package com.fd.myblog.data.remote.response

import com.fd.myblog.data.model.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserListResponse(
    @SerialName("statusCode")
    val statusCode: Int,
    @SerialName("message")
    val message: String,
    @SerialName("data")
    val list: List<User>
)