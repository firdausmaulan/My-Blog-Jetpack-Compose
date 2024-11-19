package com.fd.myblog.data.remote.request

import java.io.File

data class BlogFormRequest(
    val image: File? = null,
    val title: String? = null,
    val content: String? = null,
)