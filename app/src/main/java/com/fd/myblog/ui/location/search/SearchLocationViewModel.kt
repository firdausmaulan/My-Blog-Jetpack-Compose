package com.fd.myblog.ui.location.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fd.myblog.data.model.LocationData
import com.fd.myblog.data.remote.api.service.LocationApiService
import com.fd.myblog.helper.location.ILocationHelper
import kotlinx.coroutines.launch


class SearchLocationViewModel(
    private val locationApiService: LocationApiService,
    private val locationHelper: ILocationHelper
) : ViewModel() {

    var state by mutableStateOf<SearchLocationState>(SearchLocationState.Idle)

    var selectedLocation: LocationData? = null
    var address by mutableStateOf("")
    var latitude by mutableDoubleStateOf(0.0)
    var longitude by mutableDoubleStateOf(0.0)

    fun searchLocation(query: String) {
        viewModelScope.launch {
            state = try {
                val results = locationApiService.search(query)
                val locations = locationHelper.getLocationsFromJson(results)
                if (locations.isEmpty()) {
                    SearchLocationState.Idle
                } else {
                    SearchLocationState.Success(locations)
                }
            } catch (e: Exception) {
                SearchLocationState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun setLocation(location: LocationData) {
        selectedLocation = location
        address = location.displayName
        latitude = location.latitude
        longitude = location.longitude
    }

    fun onCenterChanged(lat: Double, lon: Double) {
        viewModelScope.launch {
            val address = locationHelper.getAddress(lat, lon).toString()
            setLocation(LocationData(address, lat, lon))
        }
    }

    fun setLocationFromJson(location: String?): LocationData? {
        return locationHelper.getLocationFromJson(location)
    }

    fun setLocationToJson(location: LocationData): String {
        return locationHelper.setLocationToJson(location)
    }
}