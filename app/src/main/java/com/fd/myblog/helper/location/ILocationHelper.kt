package com.fd.myblog.helper.location

import android.location.Location
import com.fd.myblog.data.model.LocationData

interface ILocationHelper {

    suspend fun getLastKnownLocation(): Location?

    suspend fun getAddress(latitude: Double?, longitude: Double?): String?

    fun getLocationsFromJson(results: String?): List<LocationData>

    fun setLocationToJson(location: LocationData): String

    fun getLocationFromJson(location: String?): LocationData?

}