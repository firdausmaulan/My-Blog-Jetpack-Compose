package com.fd.myblog.ui.blog.form

import com.fd.myblog.data.model.BlogPost

sealed class BlogFormState {
    data object Idle : BlogFormState()
    data object Loading : BlogFormState()
    data class Error(val message: String) : BlogFormState()
    data class Success(val blogPost: BlogPost) : BlogFormState()
}