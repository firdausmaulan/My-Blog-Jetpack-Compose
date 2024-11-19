package com.fd.myblog.data.model

import com.fd.myblog.helper.Constants
import com.fd.myblog.helper.DateHelper
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("id")
    val id: Int,
    @SerialName("token")
    var token: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("email")
    val email: String? = null,
    @SerialName("role")
    val role: String? = null,
    @SerialName("image")
    val image: String? = null,
    @SerialName("image_url")
    val imageUrl: String? = Constants.BASE_IMAGE_URL + image,
    @SerialName("address")
    val address: String? = null,
    @SerialName("latitude")
    val latitude: Double? = null,
    @SerialName("longitude")
    val longitude: Double? = null,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("updated_at")
    val updatedAt: String,
    @SerialName("formatted_created_at")
    val formattedCreatedAt: String = DateHelper.formatDate(createdAt),
    @SerialName("formatted_updated_at")
    val formattedUpdatedAt: String = DateHelper.formatDate(updatedAt),
)
