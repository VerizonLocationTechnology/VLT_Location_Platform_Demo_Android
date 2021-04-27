package com.verizon.location.navigation.location

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location as AndroidLocation
import android.location.LocationListener as AndroidLocationListener
import android.location.LocationManager
import android.os.Bundle
import com.here.sdk.core.Location
import com.here.sdk.core.LocationListener as HereLocationListener
import com.here.sdk.core.LocationProvider
import com.verizon.location.navigation.util.toHereLocation
import timber.log.Timber

class PlatformLocationProvider(context: Context) : LocationProvider, AndroidLocationListener {

    private val lm: LocationManager? = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private val hasGps = context.packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS)

    var lastKnownLocation: Location? = null

    private var locationListener: HereLocationListener? = null
    private var initialPositionListener: InitialPositionListener? = null

    @SuppressLint("MissingPermission")
    override fun start() {
        lm?.let {
            if (it.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGps) {
                it.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    LOCATION_UPDATE_INTERVAL_IN_MS,
                    1f,
                    this
                )
            } else if (it.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                it.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    LOCATION_UPDATE_INTERVAL_IN_MS,
                    1f,
                    this
                )
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun stop() {
        lm?.removeUpdates(this)
    }

    override fun setListener(listener: HereLocationListener?) {
        locationListener = listener
    }

    override fun getListener(): HereLocationListener? {
        return locationListener
    }

    fun setInitialPositionListener(listener: InitialPositionListener) {
        initialPositionListener = listener
    }

    override fun onLocationChanged(location: AndroidLocation) {
        handleLocationUpdate(location)
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

    override fun onProviderEnabled(provider: String) {
        Timber.d("PlatformPositioningProvider enabled.")
    }

    override fun onProviderDisabled(provider: String) {
        Timber.d("PlatformPositioningProvider disabled.")
    }

    private fun handleLocationUpdate(location: AndroidLocation) {
        if (lastKnownLocation == null) {
            initialPositionListener?.onInitialPositionReceived(location)
        }
        lastKnownLocation = location.toHereLocation().also {
            locationListener?.onLocationUpdated(it)
        }
    }

    companion object {
        const val LOCATION_UPDATE_INTERVAL_IN_MS = 100L
    }
}