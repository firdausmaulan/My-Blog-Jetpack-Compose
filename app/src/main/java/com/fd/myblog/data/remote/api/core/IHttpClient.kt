package com.fd.myblog.data.remote.api.core

import kotlin.reflect.KClass

interface IHttpClient {
    suspend fun <T : Any> get(endpoint: String, params: Map<String, Any?> = emptyMap(), type: KClass<T>): Result<T, NetworkError>
    suspend fun <T : Any> post(endpoint: String, params: Map<String, Any?> = emptyMap(), type: KClass<T>): Result<T, NetworkError>
    suspend fun <T : Any> put(endpoint: String, params: Map<String, Any?> = emptyMap(), type: KClass<T>): Result<T, NetworkError>
    suspend fun <T : Any> delete(endpoint: String, type: KClass<T>): Result<T, NetworkError>

    suspend fun customGet(baseUrl: String, endpoint: String, params: Map<String, Any?> = emptyMap()): String
}