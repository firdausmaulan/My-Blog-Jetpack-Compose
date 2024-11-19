package com.fd.myblog.ui.blog.form

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fd.myblog.data.remote.api.core.Result
import com.fd.myblog.data.remote.api.service.BlogPostApiService
import com.fd.myblog.data.remote.request.BlogFormRequest
import com.fd.myblog.ui.blog.detail.BlogDetailState
import kotlinx.coroutines.launch
import java.io.File

class BlogFormViewModel(private val blogPostApiService: BlogPostApiService) : ViewModel() {

    var state by mutableStateOf<BlogFormState>(BlogFormState.Idle)

    var stateDetail by mutableStateOf<BlogDetailState>(BlogDetailState.Idle)

    var isEdit by mutableStateOf(false)

    var blogPostId: Int? = null

    var image: File? by mutableStateOf(null)

    private fun isValidRequest(request: BlogFormRequest): Boolean {
        var isValidImage = request.image != null
        if (isEdit) isValidImage = true
        return  isValidImage &&
                request.title != null &&
                request.content != null &&
                request.title.isNotEmpty() &&
                request.content.isNotEmpty()
    }

    fun fetchDetail() {
        viewModelScope.launch {
            val result = blogPostApiService.detail(blogPostId)
            when (result) {
                is Result.Success -> {
                    stateDetail = BlogDetailState.Success(result.data.blog)
                }

                is Result.Error -> {
                    stateDetail =
                        BlogDetailState.Error("Failed to load blog detail: ${result.error}")
                }
            }
        }
    }

    fun create(request: BlogFormRequest) {
        if (!isValidRequest(request)) return
        state = BlogFormState.Loading
        viewModelScope.launch {
            val result = blogPostApiService.create(request)
            when (result) {
                is Result.Success -> {
                    state = BlogFormState.Success(result.data.blog)
                }

                is Result.Error -> {
                    state = BlogFormState.Error("Failed to create post: ${result.error}")
                }
            }
        }
    }

    fun edit(request: BlogFormRequest) {
        if (!isValidRequest(request)) return
        state = BlogFormState.Loading
        viewModelScope.launch {
            val result = blogPostApiService.update(blogPostId, request)
            when (result) {
                is Result.Success -> {
                    state = BlogFormState.Success(result.data.blog)
                }

                is Result.Error -> {
                    state = BlogFormState.Error("Failed to update post: ${result.error}")
                }
            }
        }
    }
}
