package com.fd.myblog.data.model

import com.fd.myblog.helper.Constants
import com.fd.myblog.helper.DateHelper
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BlogPost(
    @SerialName("id")
    val id: Int,
    @SerialName("title")
    val title: String,
    @SerialName("content")
    val content: String,
    @SerialName("image")
    val image: String? = "",
    @SerialName("image_url")
    val imageUrl: String = Constants.BASE_IMAGE_URL + image,
    @SerialName("user_id")
    val userId: Int,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("updated_at")
    val updatedAt: String,
    @SerialName("formatted_created_at")
    val formattedCreatedAt: String = DateHelper.formatDate(createdAt),
    @SerialName("formatted_updated_at")
    val formattedUpdatedAt: String = DateHelper.formatDate(updatedAt),
)
