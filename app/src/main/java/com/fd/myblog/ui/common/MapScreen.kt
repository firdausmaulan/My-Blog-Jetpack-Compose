package com.fd.myblog.ui.common

import android.widget.FrameLayout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.fd.myblog.R
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun MapScreen(
    latitude: Double?,
    longitude: Double?,
    mapModifier: Modifier = Modifier
        .fillMaxWidth()
        .height(250.dp)
) {
    // Context and FrameLayout are created dynamically to reflect state changes
    val context = LocalContext.current
    val frameLayout = remember { FrameLayout(context) }

    // Function to refresh the map
    fun refreshMap(latitude: Double?, longitude: Double?) {
        frameLayout.apply {
            removeAllViews() // Clear the previous views
            val lat = latitude ?: 0.0
            val lon = longitude ?: 0.0
            val point = GeoPoint(lat, lon)
            val mapView = MapView(context).apply {
                controller.setZoom(16.0)
                controller.setCenter(point)

                // Add marker
                val marker = Marker(this).apply {
                    position = point
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    icon = ContextCompat.getDrawable(context, R.drawable.ic_marker)
                    title = "My Location"
                }
                overlays.add(marker)
            }
            addView(
                mapView, FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
            )
        }
    }

    // Refresh map whenever latitude or longitude changes
    LaunchedEffect(latitude, longitude) {
        refreshMap(latitude, longitude)
    }

    AndroidView(
        factory = { frameLayout },
        modifier = mapModifier,
    )
}
