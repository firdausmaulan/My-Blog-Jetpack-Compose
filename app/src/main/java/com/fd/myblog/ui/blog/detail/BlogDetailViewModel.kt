package com.fd.myblog.ui.blog.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fd.myblog.data.local.preference.AppPreference
import com.fd.myblog.data.remote.api.core.Result
import com.fd.myblog.data.remote.api.service.BlogPostApiService
import kotlinx.coroutines.launch

class BlogDetailViewModel(
    private val blogPostApiService: BlogPostApiService,
    private val preference: AppPreference
) : ViewModel() {

    var state by mutableStateOf<BlogDetailState>(BlogDetailState.Loading)

    var canEdit by mutableStateOf(false)

    var blogTitle by mutableStateOf("Detail")

    fun fetchBlogDetail(blogId: Int) {
        viewModelScope.launch {
            val result = blogPostApiService.detail(blogId)
            when (result) {
                is Result.Success -> {
                    state = BlogDetailState.Success(result.data.blog)
                    blogTitle = result.data.blog.title
                    canEdit = result.data.blog.userId == preference.getUser()?.id
                }

                is Result.Error -> {
                    state = BlogDetailState.Error("Failed to load blog detail: ${result.error}")
                }
            }
        }
    }
}
