package com.fd.myblog.data.remote.api.core

import com.fd.myblog.data.local.preference.AppPreference
import com.fd.myblog.helper.Constants
import com.fd.myblog.helper.JsonHelper
import io.ktor.client.HttpClient
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.serialization.SerializationException
import java.io.File
import kotlin.reflect.KClass

open class HttpClientImpl(
    private val httpClient: HttpClient,
    private val preference: AppPreference
) : IHttpClient {

    companion object {
        const val BASE_URL = Constants.BASE_URL
    }

    override suspend fun <T : Any> get(
        endpoint: String,
        params: Map<String, Any?>,
        type: KClass<T>
    ): Result<T, NetworkError> {
        return try {
            val response = httpClient.get(BASE_URL + endpoint) {
                params.forEach { (key, value) ->
                    parameter(key, value)
                }
                headers {
                    val authTokenProvider = preference.getUser()?.token
                    authTokenProvider?.let { bearerAuth(it) }
                    contentType(ContentType.Application.Json)
                }
            }
            println("URL: ${response.request.url}")
            println("Params: $params")
            println("Response: ${response.bodyAsText()}")
            handleResponse(response, type)
        } catch (e: Exception) {
            e.printStackTrace()
            handleException(e)
        }
    }

    override suspend fun customGet(
        baseUrl: String,
        endpoint: String,
        params: Map<String, Any?>
    ): String {
        return try {
            val response = httpClient.get(baseUrl + endpoint) {
                params.forEach { (key, value) ->
                    parameter(key, value)
                }
            }
            println("URL: ${response.request.url}")
            println("Params: $params")
            println("Response: ${response.bodyAsText()}")
            response.bodyAsText()
        } catch (e: Exception) {
            e.printStackTrace()
            e.message ?: "Unknown error"
        }
    }

    override suspend fun <T : Any> post(
        endpoint: String,
        params: Map<String, Any?>,
        type: KClass<T>
    ): Result<T, NetworkError> {
        return try {
            val response = httpClient.submitFormWithBinaryData(
                url = BASE_URL + endpoint,
                formData = formData {
                    params.forEach { (key, value) ->
                        if (value != null) {
                            if (value is File) {
                                append(key, value.readBytes(), Headers.build {
                                    append(HttpHeaders.ContentType, "image/*")
                                    append(
                                        HttpHeaders.ContentDisposition,
                                        "filename=\"${value.name}\""
                                    )
                                })
                            } else {
                                append(key, value.toString())
                            }
                        }
                    }
                },
            ) {
                headers {
                    val authTokenProvider = preference.getUser()?.token
                    authTokenProvider?.let { bearerAuth(it) }
                }
            }
            println("URL: ${response.request.url}")
            println("Params: $params")
            println("Response: ${response.bodyAsText()}")
            handleResponse(response, type)
        } catch (e: Exception) {
            e.printStackTrace()
            handleException(e)
        }
    }

    override suspend fun <T : Any> put(
        endpoint: String,
        params: Map<String, Any?>,
        type: KClass<T>
    ): Result<T, NetworkError> {
        return try {
            val response = httpClient.submitFormWithBinaryData(
                url = "$BASE_URL$endpoint?_method=PUT",
                formData = formData {
                    params.forEach { (key, value) ->
                        if (value != null) {
                            if (value is File) {
                                append(key, value.readBytes(), Headers.build {
                                    append(HttpHeaders.ContentType, "image/*")
                                    append(
                                        HttpHeaders.ContentDisposition,
                                        "filename=\"${value.name}\""
                                    )
                                })
                            } else {
                                append(key, value.toString())
                            }
                        }
                    }
                }
            ) {
                headers {
                    val authTokenProvider = preference.getUser()?.token
                    authTokenProvider?.let { bearerAuth(it) }
                }
            }
            println("URL: ${response.request.url}")
            println("Params: $params")
            println("Response: ${response.bodyAsText()}")
            handleResponse(response, type)
        } catch (e: Exception) {
            e.printStackTrace()
            handleException(e)
        }
    }

    override suspend fun <T : Any> delete(
        endpoint: String,
        type: KClass<T>
    ): Result<T, NetworkError> {
        return try {
            val response = httpClient.delete(BASE_URL + endpoint) {
                headers {
                    val authTokenProvider = preference.getUser()?.token
                    authTokenProvider?.let { bearerAuth(it) }
                    contentType(ContentType.Application.Json)
                }
            }
            println("URL: ${response.request.url}")
            println("Response: ${response.bodyAsText()}")
            handleResponse(response, type)
        } catch (e: Exception) {
            e.printStackTrace()
            handleException(e)
        }
    }

    private suspend fun <T : Any> handleResponse(
        response: HttpResponse,
        type: KClass<T>
    ): Result<T, NetworkError> {
        return when (response.status.value) {
            in 200..299 -> {
                try {
                    val body = response.bodyAsText()
                    Result.Success(JsonHelper.fromJson(body, type))
                } catch (e: SerializationException) {
                    Result.Error(NetworkError.SERIALIZATION)
                }
            }

            401 -> Result.Error(NetworkError.UNAUTHORIZED)
            409 -> Result.Error(NetworkError.CONFLICT)
            408 -> Result.Error(NetworkError.REQUEST_TIMEOUT)
            413 -> Result.Error(NetworkError.PAYLOAD_TOO_LARGE)
            in 500..599 -> Result.Error(NetworkError.SERVER_ERROR)
            else -> Result.Error(NetworkError.UNKNOWN)
        }
    }

    private fun handleException(exception: Exception): Result.Error<NetworkError> {
        return when (exception) {
            is UnresolvedAddressException -> Result.Error(NetworkError.NO_INTERNET)
            is SerializationException -> Result.Error(NetworkError.SERIALIZATION)
            else -> Result.Error(NetworkError.UNKNOWN)
        }
    }
}