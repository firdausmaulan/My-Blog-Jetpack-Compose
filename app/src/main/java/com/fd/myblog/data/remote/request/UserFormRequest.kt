package com.fd.myblog.data.remote.request

import com.fd.myblog.helper.Constants
import java.io.File

data class UserFormRequest(
    val image: File? = null,
    val name: String? = null,
    val email: String? = null,
    val password: String? = null,
    val passwordConfirmation: String? = null,
    val latitude : Double? = null,
    val longitude : Double? = null,
    val address: String? = null,
    val role: String? = Constants.ROLE_USER
)