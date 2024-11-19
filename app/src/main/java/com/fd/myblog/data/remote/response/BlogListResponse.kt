package com.fd.myblog.data.remote.response

import com.fd.myblog.data.model.BlogPost
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BlogListResponse (
    @SerialName("statusCode")
    val statusCode : Int,
    @SerialName("message")
    val message : String,
    @SerialName("data")
    val list : List<BlogPost>
)