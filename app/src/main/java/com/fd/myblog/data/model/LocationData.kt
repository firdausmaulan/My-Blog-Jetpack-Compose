package com.fd.myblog.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocationData(
    @SerialName("display_name")
    val displayName: String,
    @SerialName("lat")
    val latitude: Double,
    @SerialName("lon")
    val longitude: Double
)
