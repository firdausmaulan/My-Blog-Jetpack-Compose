package com.fd.myblog.data.local.preference

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class CorePreference(context: Context) : ICorePreference{

    companion object {
        private const val PREF_NAME = "app_prefs"
    }

    private val sharedPreferences by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        EncryptedSharedPreferences.create(
            context,
            PREF_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    override fun save(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    override fun save(key: String, value: Int) {
        sharedPreferences.edit().putInt(key, value).apply()
    }

    override fun save(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    override fun save(key: String, value: Float) {
        sharedPreferences.edit().putFloat(key, value).apply()
    }

    override fun save(key: String, value: Long) {
        sharedPreferences.edit().putLong(key, value).apply()
    }

    override fun getString(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    override fun getInt(key: String): Int? {
        val result = sharedPreferences.getInt(key, -1)
        if (result == -1) return null
        return result
    }

    override fun getBoolean(key: String): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }

    override fun getFloat(key: String): Float? {
        val result = sharedPreferences.getFloat(key, -1f)
        if (result == -1f) return null
        return result
    }

    override fun getLong(key: String): Long? {
        val result = sharedPreferences.getLong(key, -1L)
        if (result == -1L) return null
        return result
    }

    override fun remove(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }

    override fun clear() {
        sharedPreferences.edit().clear().apply()
    }

}
