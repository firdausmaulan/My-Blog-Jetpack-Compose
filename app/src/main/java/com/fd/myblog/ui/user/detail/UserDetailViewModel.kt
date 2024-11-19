package com.fd.myblog.ui.user.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fd.myblog.data.local.preference.AppPreference
import com.fd.myblog.data.model.LocationData
import com.fd.myblog.data.model.User
import com.fd.myblog.data.remote.api.core.Result
import com.fd.myblog.data.remote.api.service.UserApiService
import com.fd.myblog.data.remote.request.UserFormRequest
import com.fd.myblog.helper.location.ILocationHelper
import kotlinx.coroutines.launch
import java.io.File

class UserDetailViewModel(
    private val userApiService: UserApiService,
    private val preference: AppPreference,
    private val locationHelper: ILocationHelper
) : ViewModel() {

    var state by mutableStateOf<UserDetailState>(UserDetailState.Loading)

    var stateEdit by mutableStateOf<UserEditState>(UserEditState.Idle)

    var isEdit by mutableStateOf(false)

    var user: User? = null

    var image: File? by mutableStateOf(null)

    var nameError by mutableStateOf(false)

    var address: String? by mutableStateOf(null)

    var latitude: Double? by mutableStateOf(null)

    var longitude: Double? by mutableStateOf(null)

    fun fetchUserDetail(id: Int) {
        viewModelScope.launch {
            val result = userApiService.detailUser(id)
            when (result) {
                is Result.Success -> {
                    state = UserDetailState.Success(result.data.user)
                    user = result.data.user
                    setLocation(LocationData(
                        latitude = user?.latitude ?: 0.0,
                        longitude = user?.longitude ?: 0.0,
                        displayName = user?.address ?: ""
                    ))
                }

                is Result.Error -> {
                    state = UserDetailState.Error("Failed to load user detail: ${result.error}")
                }
            }
        }
    }

    fun fetchLocationAndAddress() {
        viewModelScope.launch {
            val location = locationHelper.getLastKnownLocation()
            location?.let {
                setLocation(LocationData(
                    latitude = it.latitude,
                    longitude = it.longitude,
                    displayName = getAddress(it.latitude, it.longitude) ?: ""
                ))
            }
        }
    }

    private suspend fun getAddress(latitude: Double, longitude: Double): String? {
        return locationHelper.getAddress(latitude, longitude)
    }

    fun setLocationFromJson(json: String?) {
        val location = locationHelper.getLocationFromJson(json) ?: return
        setLocation(location)
    }

    fun setLocationToJson(): String {
        val location = LocationData(
            latitude = latitude ?: 0.0,
            longitude = longitude ?: 0.0,
            displayName = address ?: ""
        )
        return locationHelper.setLocationToJson(location)
    }

    private fun setLocation(location : LocationData) {
        this.latitude = location.latitude
        this.longitude = location.longitude
        this.address = location.displayName
    }

    fun edit(userFormRequest: UserFormRequest) {
        if (userFormRequest.name.isNullOrEmpty()) return
        viewModelScope.launch {
            stateEdit = UserEditState.Loading
            val result = userApiService.updateUser(user?.id, userFormRequest)
            when (result) {
                is Result.Success -> {
                    stateEdit = UserEditState.Success(result.data.user)
                    preference.saveUser(result.data.user)
                }

                is Result.Error -> {
                    stateEdit = UserEditState.Error("Failed to update user: ${result.error}")
                }
            }
        }
    }

    fun clearSession() {
        preference.clear()
    }
}
