package com.fd.myblog.helper.location

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Geocoder.GeocodeListener
import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import com.fd.myblog.data.model.LocationData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class LocationHelper(private val context: Context) : ILocationHelper {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    // Suspending function to get the last known location
    @Suppress("MissingPermission")
    override suspend fun getLastKnownLocation(): Location? {
        return suspendCancellableCoroutine { continuation ->
            fusedLocationClient.lastLocation.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    continuation.resume(task.result) // Resume with the location
                } else {
                    continuation.resumeWithException(
                        task.exception ?: Exception("Failed to get last location")
                    )
                }
            }
        }
    }

    override suspend fun getAddress(latitude: Double?, longitude: Double?): String? {
        if (latitude == null || longitude == null) return ""
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getAddressTiramisu(latitude, longitude)
        } else {
            getAddressBelowTiramisu(latitude, longitude)
        }
    }

    private suspend fun getAddressBelowTiramisu(latitude: Double, longitude: Double): String? {
        return withContext(Dispatchers.IO) {
            val geocoder = Geocoder(context, Locale.getDefault())
            try {
                val addresses: MutableList<Address>? =
                    geocoder.getFromLocation(latitude, longitude, 1)
                if (addresses?.isNotEmpty() == true) {
                    addresses[0].getAddressLine(0) // Full address line
                } else null
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private suspend fun getAddressTiramisu(latitude: Double, longitude: Double): String? {
        return withContext(Dispatchers.IO) {
            suspendCancellableCoroutine { continuation ->
                try {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    geocoder.getFromLocation(
                        latitude, longitude, 1, object : GeocodeListener {
                            override fun onGeocode(addresses: List<Address>) {
                                if (addresses.isNotEmpty()) {
                                    continuation.resume(addresses[0].getAddressLine(0))
                                } else {
                                    continuation.resume(null)
                                }
                            }

                            override fun onError(errorMessage: String?) {
                                continuation.resumeWithException(
                                    Exception(errorMessage ?: "Geocoding failed")
                                )
                            }
                        }
                    )
                } catch (e: Exception) {
                    continuation.resumeWithException(e)
                }
            }
        }
    }

    override fun getLocationsFromJson(results: String?): List<LocationData> {
        val locations = mutableListOf<LocationData>()
        if (results.isNullOrEmpty()) return locations
        val jsonArray = JSONArray(results)
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val locationData = LocationData(
                displayName = jsonObject.getString("display_name"),
                latitude = jsonObject.getDouble("lat"),
                longitude = jsonObject.getDouble("lon")
            )
            locations.add(locationData)
        }
        return locations
    }

    override fun setLocationToJson(location: LocationData): String {
        val jsonObject = JSONObject()
        jsonObject.put("display_name", location.displayName)
        jsonObject.put("lat", location.latitude)
        jsonObject.put("lon", location.longitude)
        return jsonObject.toString()
    }

    override fun getLocationFromJson(location: String?): LocationData? {
        if (location.isNullOrEmpty()) return null
        val jsonObject = JSONObject(location)
        return LocationData(
            displayName = jsonObject.getString("display_name"),
            latitude = jsonObject.getDouble("lat"),
            longitude = jsonObject.getDouble("lon")
        )
    }
}
