package com.fd.myblog.ui.blog.list

import com.fd.myblog.data.model.BlogPost

sealed class BlogListState {
    data object Loading : BlogListState()
    data object Empty : BlogListState()
    data class Error(val message: String) : BlogListState()
    data class Success(val blogs: List<BlogPost>, val isLoadMore: Boolean) : BlogListState()
}