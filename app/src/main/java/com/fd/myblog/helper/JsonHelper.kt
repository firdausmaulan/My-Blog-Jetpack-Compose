package com.fd.myblog.helper

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.reflect.KClass

@OptIn(InternalSerializationApi::class)
object JsonHelper {
    private val jsonFormatter = Json { ignoreUnknownKeys = true }

    fun <T : Any> fromJson(json: String, type: KClass<T>): T {
        return jsonFormatter.decodeFromString(type.serializer(), json)
    }

    fun <T : Any> toJson(data: T, type: KClass<T>): String {
        return jsonFormatter.encodeToString(type.serializer(), data)
    }
}