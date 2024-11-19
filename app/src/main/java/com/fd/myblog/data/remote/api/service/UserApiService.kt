package com.fd.myblog.data.remote.api.service

import com.fd.myblog.data.remote.api.core.IHttpClient
import com.fd.myblog.data.remote.api.core.NetworkError
import com.fd.myblog.data.remote.api.core.Result
import com.fd.myblog.data.remote.request.SearchRequest
import com.fd.myblog.data.remote.request.UserFormRequest
import com.fd.myblog.data.remote.request.UserLoginRequest
import com.fd.myblog.data.remote.response.UserDetailResponse
import com.fd.myblog.data.remote.response.UserListResponse

class UserApiService(private val httpClient: IHttpClient) {

    suspend fun register(request: UserFormRequest): Result<UserDetailResponse, NetworkError> {
        val formData = mapOf(
            "name" to request.name,
            "email" to request.email,
            "password" to request.password,
            "password_confirmation" to request.passwordConfirmation,
            "latitude" to request.latitude,
            "longitude" to request.longitude,
            "address" to request.address,
            "role" to request.role,
            "image" to request.image
        )
        return httpClient.post(
            endpoint = "register",
            params = formData,
            type = UserDetailResponse::class
        )
    }

    suspend fun login(request: UserLoginRequest): Result<UserDetailResponse, NetworkError> {
        val formData = mapOf(
            "email" to request.email,
            "password" to request.password
        )
        return httpClient.post(
            endpoint = "login",
            params = formData,
            type = UserDetailResponse::class
        )
    }

    suspend fun updateUser(
        id: Int?,
        request: UserFormRequest
    ): Result<UserDetailResponse, NetworkError> {
        val formData = mutableMapOf<String, Any>()
        if (request.image != null) {
            formData["image"] = request.image
        }
        if (request.name != null) {
            formData["name"] = request.name
        }
        if (request.address != null) {
            formData["address"] = request.address
        }
        if (request.latitude != null) {
            formData["latitude"] = request.latitude
        }
        if (request.longitude != null) {
            formData["longitude"] = request.longitude
        }
        if (request.password != null) {
            formData["password"] = request.password
        }
        if (request.passwordConfirmation != null) {
            formData["password_confirmation"] = request.passwordConfirmation
        }
        return httpClient.put(
            endpoint = "user/$id",
            params = formData,
            type = UserDetailResponse::class
        )
    }

    suspend fun detailUser(id: Int): Result<UserDetailResponse, NetworkError> {
        return httpClient.get(endpoint = "user/$id", type = UserDetailResponse::class)
    }

    suspend fun searchUsers(request: SearchRequest): Result<UserListResponse, NetworkError> {
        return httpClient.get(
            endpoint = "users",
            params = mapOf(
                "query" to request.query,
                "page" to request.page,
            ),
            type = UserListResponse::class
        )
    }
}