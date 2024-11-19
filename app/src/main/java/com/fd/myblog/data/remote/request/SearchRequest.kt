package com.fd.myblog.data.remote.request

data class SearchRequest(
    val query: String? = "",
    val page: Int? = 1
)