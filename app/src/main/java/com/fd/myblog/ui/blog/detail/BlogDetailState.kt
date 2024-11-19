package com.fd.myblog.ui.blog.detail

import com.fd.myblog.data.model.BlogPost

sealed class BlogDetailState {
    data object Idle : BlogDetailState()
    data object Loading : BlogDetailState()
    data class Error(val message: String) : BlogDetailState()
    data class Success(val blogPost: BlogPost) : BlogDetailState()
}