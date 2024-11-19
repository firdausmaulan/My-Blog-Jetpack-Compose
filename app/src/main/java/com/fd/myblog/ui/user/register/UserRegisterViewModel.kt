package com.fd.myblog.ui.user.register

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fd.myblog.data.model.LocationData
import com.fd.myblog.data.remote.api.core.Result
import com.fd.myblog.data.remote.api.service.UserApiService
import com.fd.myblog.data.remote.request.UserFormRequest
import com.fd.myblog.helper.location.ILocationHelper
import kotlinx.coroutines.launch
import java.io.File

class UserRegisterViewModel(
    private val userApiService: UserApiService,
    private val locationHelper: ILocationHelper
) : ViewModel() {

    var state by mutableStateOf<UserRegisterState>(UserRegisterState.Idle)

    var image: File? by mutableStateOf(null)

    var nameError by mutableStateOf(false)

    var emailError by mutableStateOf(false)

    var passwordError by mutableStateOf(false)

    var passwordNotMatch by mutableStateOf(false)

    var address: String? by mutableStateOf(null)

    var latitude: Double? by mutableStateOf(null)

    var longitude: Double? by mutableStateOf(null)

    // Fetch current location and address
    fun fetchLocationAndAddress() {
        viewModelScope.launch {
            val location = locationHelper.getLastKnownLocation()
            location?.let {
                latitude = it.latitude
                longitude = it.longitude
                address = getAddress(it.latitude, it.longitude)
            }
        }
    }

    private suspend fun getAddress(latitude: Double, longitude: Double): String? {
        return locationHelper.getAddress(latitude, longitude)
    }

    fun setLocation(json: String?) {
        val location = locationHelper.getLocationFromJson(json) ?: return
        this.latitude = location.latitude
        this.longitude = location.longitude
        this.address = location.displayName
    }

    fun setLocationToJson(): String {
        val location = LocationData(
            latitude = latitude ?: 0.0,
            longitude = longitude ?: 0.0,
            displayName = address ?: ""
        )
        return locationHelper.setLocationToJson(location)
    }

    fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidRequest(userFormRequest: UserFormRequest): Boolean {
        return userFormRequest.name != null &&
                userFormRequest.email != null &&
                userFormRequest.password != null &&
                userFormRequest.passwordConfirmation != null &&
                isValidEmail(userFormRequest.email) &&
                userFormRequest.password.length >= 6 &&
                userFormRequest.password == userFormRequest.passwordConfirmation
    }

    fun register(userFormRequest: UserFormRequest) {
        if (!isValidRequest(userFormRequest)) return
        viewModelScope.launch {
            state = UserRegisterState.Loading
            val result = userApiService.register(userFormRequest)
            when (result) {
                is Result.Success -> {
                    state = UserRegisterState.Success(result.data.user)
                }

                is Result.Error -> {
                    state = UserRegisterState.Error("Failed to register user: ${result.error}")
                }
            }
        }
    }
}
