package com.fd.myblog.data.local.preference

import com.fd.myblog.data.model.User
import com.fd.myblog.helper.JsonHelper

class AppPreference(private val preference: ICorePreference) {

    companion object {
        private const val USER_KEY = "user"
    }

    fun saveUser(user: User) {
        var currentUser = getUser()
        val currentUserToken = currentUser?.token
        currentUser = user
        if (user.token == null) currentUserToken?.let { currentUser.token = it }
        currentUser.let { preference.save(USER_KEY, JsonHelper.toJson(it, User::class)) }
    }

    fun getUser(): User? {
        return preference.getString(USER_KEY)?.let {
            JsonHelper.fromJson(it, User::class)
        }
    }

    fun clear() {
        preference.clear()
    }
}
