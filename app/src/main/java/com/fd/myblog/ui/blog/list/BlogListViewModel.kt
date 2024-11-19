package com.fd.myblog.ui.blog.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fd.myblog.data.local.preference.AppPreference
import com.fd.myblog.data.remote.api.core.Result
import com.fd.myblog.data.remote.api.service.BlogPostApiService
import com.fd.myblog.data.remote.request.SearchRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BlogListViewModel(
    private val blogPostApiService: BlogPostApiService,
    private val preferenceImpl: AppPreference
) : ViewModel() {

    var state by mutableStateOf<BlogListState>(BlogListState.Loading)
    var isRefreshing by mutableStateOf(false)
    private var currentPage = 1
    private var canLoadMore = true
    private var query = ""

    fun search(query: String) {
        this.query = query
        currentPage = 1
        canLoadMore = true
        loadBlogs()
    }

    fun reloadBlogs() {
        isRefreshing = true
        search("")
        // delay 1 second to simulate refresh
        viewModelScope.launch {
            delay(1000)
            isRefreshing = false
        }
    }

    init {
        loadBlogs()
    }

    fun loadBlogs(isLoadMore: Boolean = false) {
        if (isLoadMore && !canLoadMore) return

        viewModelScope.launch {
            state = if (isLoadMore) BlogListState.Success(
                (state as? BlogListState.Success)?.blogs.orEmpty(),
                true
            )
            else BlogListState.Loading

            val result = blogPostApiService.search(SearchRequest(query = query, page = currentPage))
            when (result) {
                is Result.Success -> {
                    val blogs = result.data.list
                    canLoadMore = blogs.isNotEmpty()
                    if (blogs.isEmpty() && !isLoadMore) {
                        state = BlogListState.Empty
                    } else {
                        val updatedBlogs = if (isLoadMore) (state as? BlogListState.Success)?.blogs.orEmpty() + blogs else blogs
                        state = BlogListState.Success(updatedBlogs, false)
                        currentPage++
                    }
                }

                is Result.Error -> {
                    state = BlogListState.Error("Failed to load blogs: ${result.error}")
                }
            }
        }
    }

    fun isAuthenticated(): Boolean {
        return !preferenceImpl.getUser()?.token.isNullOrEmpty()
    }

    fun getUser() = preferenceImpl.getUser()
}