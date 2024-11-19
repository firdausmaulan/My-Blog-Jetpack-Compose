package com.fd.myblog.data.local.preference

interface ICorePreference {

    fun save(key: String, value: String)
    fun save(key: String, value: Int)
    fun save(key: String, value: Boolean)
    fun save(key: String, value: Float)
    fun save(key: String, value: Long)

    fun getString(key: String): String?
    fun getInt(key: String): Int?
    fun getBoolean(key: String): Boolean?
    fun getFloat(key: String): Float?
    fun getLong(key: String): Long?

    fun remove(key: String)

    fun clear()

}