package com.fd.myblog.data.remote.api.service

import com.fd.myblog.data.remote.api.core.IHttpClient

class LocationApiService(private val httpClient: IHttpClient) {

    suspend fun search(query: String): String {
        return httpClient.customGet(
            baseUrl = "https://nominatim.openstreetmap.org/",
            endpoint = "search",
            params = mapOf(
                "q" to query,
                "format" to "json",
                "limit" to 5
            )
        )
    }

}