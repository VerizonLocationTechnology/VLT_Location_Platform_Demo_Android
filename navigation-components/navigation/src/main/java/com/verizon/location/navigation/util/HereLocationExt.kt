package com.verizon.location.navigation.util

import android.location.Location as AndroidLocation
import com.here.sdk.core.GeoCoordinates
import com.here.sdk.core.Location as HereLocation
import java.util.*

internal fun HereLocation.toAndroidLocation(): AndroidLocation {
    val loc = AndroidLocation("")
    loc.latitude = this.coordinates.latitude
    loc.longitude = this.coordinates.longitude
    loc.altitude = this.coordinates.altitude ?: 0.0
    loc.time = this.timestamp.time
    loc.bearing = this.bearingInDegrees?.toFloat() ?: 0.0f
    loc.speed = this.speedInMetersPerSecond?.toFloat() ?: 0.0f
    loc.accuracy = this.horizontalAccuracyInMeters?.toFloat() ?: 0.0f
    return loc
}

internal fun AndroidLocation.toHereLocation() = HereLocation(
    GeoCoordinates(latitude, longitude, altitude),
    Date(time)
).apply {
    bearingInDegrees = bearing.toDouble()
    speedInMetersPerSecond = speed.toDouble()
    horizontalAccuracyInMeters = accuracy.toDouble()
}