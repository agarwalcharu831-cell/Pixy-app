package com.example.pixy.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.example.pixy.model.Issue
import com.mapbox.geojson.Point
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.style.standard.LightPresetValue
import com.mapbox.maps.extension.compose.style.standard.MapboxStandardStyle
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createCircleAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.OnMapClickListener

private data class Hotspot(
    val latitude: Double,
    val longitude: Double,
    val radius: Double,
    val color: String
)

private fun buildHotspots(issues: List<Issue>): List<Hotspot> {
    if (issues.isEmpty()) return emptyList()

    val clusters = mutableListOf<MutableList<Issue>>()
    val threshold = 0.02

    issues.forEach { issue ->
        val cluster = clusters.firstOrNull { group ->
            group.any { existing ->
                kotlin.math.abs(existing.latitude - issue.latitude) < threshold &&
                        kotlin.math.abs(existing.longitude - issue.longitude) < threshold
            }
        }
        if (cluster == null) {
            clusters.add(mutableListOf(issue))
        } else {
            cluster.add(issue)
        }
    }

    return clusters.map { group ->
        val avgLat = group.map { it.latitude }.average()
        val avgLng = group.map { it.longitude }.average()
        val size = group.size
        val radius = when {
            size >= 8 -> 40.0
            size >= 5 -> 30.0
            size >= 3 -> 22.0
            else -> 14.0
        }
        val color = when {
            size >= 8 -> "#FF3B30"
            size >= 5 -> "#FF9500"
            size >= 3 -> "#FFD60A"
            else -> "#34C759"
        }
        Hotspot(avgLat, avgLng, radius, color)
    }
}

@OptIn(MapboxExperimental::class)
@Composable
fun PixyMapboxHomeMap(
    issues: List<Issue>,
    userLatitude: Double? = null,
    userLongitude: Double? = null,
    onIssueClick: (String) -> Unit
) {
    val centerLng = userLongitude ?: 77.2090
    val centerLat = userLatitude ?: 28.6139

    val viewportState = rememberMapViewportState {
        setCameraOptions {
            center(Point.fromLngLat(centerLng, centerLat))
            zoom(11.5)
        }
    }

    val hotspots = buildHotspots(issues)

    MapboxMap(
        modifier = Modifier.fillMaxSize(),
        mapViewportState = viewportState,
        style = {
            MapboxStandardStyle {
                lightPreset = LightPresetValue.DAY
            }
        }
    ) {
        issues.forEach { issue ->
            PointAnnotation(
                point = Point.fromLngLat(issue.longitude, issue.latitude),
                onClick = {
                    onIssueClick(issue.id)
                    true
                }
            )
        }

        if (userLatitude != null && userLongitude != null) {
            PointAnnotation(
                point = Point.fromLngLat(userLongitude, userLatitude)
            )
        }

        MapEffect(issues, userLatitude, userLongitude) { mapView ->
            val circleManager = mapView.annotations.createCircleAnnotationManager()
            circleManager.deleteAll()

            hotspots.forEach { spot ->
                circleManager.create(
                    CircleAnnotationOptions()
                        .withPoint(Point.fromLngLat(spot.longitude, spot.latitude))
                        .withCircleRadius(spot.radius)
                        .withCircleColor(spot.color)
                        .withCircleOpacity(0.18)
                        .withCircleStrokeColor(spot.color)
                        .withCircleStrokeWidth(1.0)
                )
            }

            if (userLatitude != null && userLongitude != null) {
                val pointManager = mapView.annotations.createPointAnnotationManager()
                pointManager.deleteAll()
                pointManager.create(
                    PointAnnotationOptions()
                        .withPoint(Point.fromLngLat(userLongitude, userLatitude))
                )
            }
        }
    }

    LaunchedEffect(centerLat, centerLng) {
        viewportState.setCameraOptions {
            center(Point.fromLngLat(centerLng, centerLat))
            zoom(11.5)
        }
    }
}

@OptIn(MapboxExperimental::class)
@Composable
fun PixyMapboxDetailMap(
    latitude: Double,
    longitude: Double
) {
    val viewportState = rememberMapViewportState {
        setCameraOptions {
            center(Point.fromLngLat(longitude, latitude))
            zoom(14.5)
        }
    }

    MapboxMap(
        modifier = Modifier.fillMaxSize(),
        mapViewportState = viewportState,
        style = {
            MapboxStandardStyle {
                lightPreset = LightPresetValue.DAY
            }
        }
    ) {
        PointAnnotation(point = Point.fromLngLat(longitude, latitude))
    }
}

@OptIn(MapboxExperimental::class)
@Composable
fun PixyMapboxPickerMap(
    latitude: Double,
    longitude: Double,
    userLatitude: Double?,
    userLongitude: Double?,
    onMapTap: (Double, Double) -> Unit
) {
    val startLng = userLongitude ?: longitude
    val startLat = userLatitude ?: latitude

    val viewportState = rememberMapViewportState {
        setCameraOptions {
            center(Point.fromLngLat(startLng, startLat))
            zoom(13.0)
        }
    }

    MapboxMap(
        modifier = Modifier.fillMaxSize(),
        mapViewportState = viewportState,
        style = {
            MapboxStandardStyle {
                lightPreset = LightPresetValue.DAY
            }
        },
        onMapClickListener = OnMapClickListener { clickedPoint ->
            onMapTap(clickedPoint.latitude(), clickedPoint.longitude())
            true
        }
    ) {
        PointAnnotation(point = Point.fromLngLat(longitude, latitude))
        if (userLatitude != null && userLongitude != null) {
            PointAnnotation(point = Point.fromLngLat(userLongitude, userLatitude))
        }
    }

    LaunchedEffect(startLat, startLng) {
        viewportState.setCameraOptions {
            center(Point.fromLngLat(startLng, startLat))
            zoom(13.0)
        }
    }
}