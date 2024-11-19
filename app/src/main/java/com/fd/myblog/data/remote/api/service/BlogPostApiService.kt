package com.fd.myblog.data.remote.api.service

import com.fd.myblog.data.remote.api.core.IHttpClient
import com.fd.myblog.data.remote.api.core.NetworkError
import com.fd.myblog.data.remote.api.core.Result
import com.fd.myblog.data.remote.request.BlogFormRequest
import com.fd.myblog.data.remote.request.SearchRequest
import com.fd.myblog.data.remote.response.BlogDetailResponse
import com.fd.myblog.data.remote.response.BlogListResponse

class BlogPostApiService(private val httpClient: IHttpClient) {

    suspend fun create(request: BlogFormRequest): Result<BlogDetailResponse, NetworkError> {
        val formData = mapOf(
            "title" to request.title,
            "content" to request.content,
            "image" to request.image
        )
        return httpClient.post(
            endpoint = "post",
            params = formData,
            type = BlogDetailResponse::class
        )
    }

    suspend fun update(
        id: Int?,
        request: BlogFormRequest
    ): Result<BlogDetailResponse, NetworkError> {
        val formData = mutableMapOf<String, Any>()
        if (request.title != null) formData["title"] = request.title
        if (request.content != null) formData["content"] = request.content
        if (request.image != null) formData["image"] = request.image
        return httpClient.put(
            endpoint = "post/$id",
            params = formData,
            type = BlogDetailResponse::class
        )
    }

    suspend fun detail(id: Int?): Result<BlogDetailResponse, NetworkError> {
        return httpClient.get(endpoint = "post/$id", type = BlogDetailResponse::class)
    }

    suspend fun delete(id: String): Result<BlogDetailResponse, NetworkError> {
        return httpClient.delete(endpoint = "post/$id", type = BlogDetailResponse::class)
    }

    suspend fun search(request: SearchRequest): Result<BlogListResponse, NetworkError> {
        return httpClient.get(
            endpoint = "posts",
            params = mapOf(
                "title" to request.query,
                "page" to request.page,
            ),
            type = BlogListResponse::class
        )
    }

}