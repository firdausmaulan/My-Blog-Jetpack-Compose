package com.fd.myblog.ui.common

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.fd.myblog.R
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

@Composable
fun EditableMapScreen(
    latitude: Double?,
    longitude: Double?,
    onCenterChanged: (Double, Double) -> Unit,
    mapModifier: Modifier = Modifier.fillMaxSize()
) {
    val context = LocalContext.current
    val frameLayout = remember { FrameLayout(context) }
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var isFromSearch by remember { mutableStateOf(false) }

    // Handler to trigger delayed actions
    val handler = remember { Handler(Looper.getMainLooper()) }
    var mapIdleRunnable by remember { mutableStateOf<Runnable?>(null) }

    fun refreshMap(latitude: Double?, longitude: Double?) {
        isFromSearch = true
        frameLayout.apply {
            // Clear the previous views
            removeAllViews()
            val lat = latitude ?: 0.0
            val lon = longitude ?: 0.0
            val point = GeoPoint(lat, lon)
            mapView = MapView(context).apply {
                controller.setZoom(16.0)
                controller.setCenter(point)

                addMapListener(object : MapListener {
                    override fun onScroll(event: ScrollEvent?): Boolean {
                        // Trigger when the map stops moving
                        mapIdleRunnable?.let { handler.removeCallbacks(it) }
                        mapIdleRunnable = Runnable {
                            val center = mapView?.mapCenter
                            val centerLat = center?.latitude ?: 0.0
                            val centerLon = center?.longitude ?: 0.0
                            if (!isFromSearch) onCenterChanged(centerLat, centerLon)
                            isFromSearch = false // Reset the flag after the event
                        }
                        mapIdleRunnable?.let { handler.postDelayed(it, 1000) } // 1-second delay
                        return true
                    }

                    override fun onZoom(event: ZoomEvent?): Boolean {
                        return true
                    }
                })
            }

            // Add the map to the layout
            addView(
                mapView, FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
            )

            // Add a custom view for the central marker
            val centerMarker = View(context).apply {
                setBackgroundResource(R.drawable.ic_marker) // Your marker drawable
                layoutParams = FrameLayout.LayoutParams(
                    36.toPx(context),
                    36.toPx(context),
                    Gravity.CENTER
                )
            }
            addView(centerMarker)
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

// Extension function to convert int to pixels
fun Int.toPx(context: Context): Int {
    return (this * context.resources.displayMetrics.density).toInt()
}