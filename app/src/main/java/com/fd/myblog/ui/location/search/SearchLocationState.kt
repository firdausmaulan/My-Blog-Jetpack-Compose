package com.fd.myblog.ui.location.search

import com.fd.myblog.data.model.LocationData

sealed class SearchLocationState {
    object Idle : SearchLocationState()
    data class Success(val results: List<LocationData>) : SearchLocationState()
    data class Error(val message: String) : SearchLocationState()
}