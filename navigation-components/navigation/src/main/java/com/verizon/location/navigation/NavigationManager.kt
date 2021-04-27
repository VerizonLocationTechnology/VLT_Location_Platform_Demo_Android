package com.verizon.location.navigation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.here.sdk.core.GeoCoordinates
import com.here.sdk.core.UnitSystem
import com.here.sdk.core.errors.InstantiationErrorException
import com.here.sdk.navigation.DestinationReachedListener
import com.here.sdk.navigation.Navigator
import com.here.sdk.navigation.RouteDeviationListener
import com.here.sdk.navigation.RouteProgressListener
import com.here.sdk.routing.*
import com.here.sdk.routing.Route as HereRoute
import com.here.sdk.routing.Waypoint as HereWaypoint
import com.verizon.location.common.NavEventListener
import com.verizon.location.common.NavEventListener.NavigationEndReason
import com.verizon.location.common.model.*
import com.verizon.location.common.model.RouteOptions
import com.verizon.location.navigation.location.PlatformLocationProvider
import com.verizon.location.navigation.util.toRoutes
import com.verizon.location.navigation.util.getManeuvers
import com.verizon.location.navigation.util.toRoute
import timber.log.Timber

class NavigationManager(context: Context) {

    private val locationProvider = PlatformLocationProvider(context)
    private var navigator: Navigator? = null
    private var routingEngine: RoutingEngine? = null

    private var navState : NavigationState = NavigationState.STOPPED
    private val navEventListeners = mutableSetOf<NavEventListener>()

    private var stops: List<RouteStop> = emptyList()
    private var maneuvers: List<ManeuverPrompt>? = null
    private var lastRerouteFailed = false
    private var deviationCount = 0

    // Uses a List<Pair> instead of a Map due to an existing bug with Kotlin Map
    private var routeAlternatives = mutableListOf<Pair<Route, HereRoute>>()

    init {
        if (hasLocationPermission(context)) {
            locationProvider.start()
        }
        try {
            navigator = Navigator(locationProvider)
            routingEngine = RoutingEngine()
        } catch (e: InstantiationErrorException) {
            Toast.makeText(context, e.localizedMessage, Toast.LENGTH_LONG).show()
            throw RuntimeException("Initialization of Navigator failed: " + e.error.name)
        }
    }

    fun getRoutes(stops: List<RouteStop>, options: RouteOptions?) {
        this.stops = stops
        val waypoints = mutableListOf<HereWaypoint>().apply {
            stops.forEach { stop ->
                this.add(HereWaypoint(GeoCoordinates(stop.location.latitude, stop.location.longitude)))
            }
        }
        navEventListeners.forEach { it.onRoutesRequested(stops) }

        val callback = CalculateRouteCallback { routingError, hereRoutes ->
            routingError?.let { error ->
                Timber.e(error.name)
                navEventListeners.forEach { listener ->
                    listener.onRoutesRetrieveFailed(error.name)
                }
            } ?: run {
                hereRoutes?.let {
                    routeAlternatives.clear()
                    it.forEach { hereRoute ->
                        val route = hereRoute.toRoute()
                        routeAlternatives.add(Pair(route, hereRoute))
                    }
                    navEventListeners.forEach { listener ->
                        listener.onRoutesRetrieved(hereRoutes.toRoutes(), stops)
                    }
                }
            }
        }

        when (options?.transportMode) {
            TranportMode.TRUCK -> routingEngine?.calculateRoute(
                waypoints,
                TruckOptions().apply {
                    this.textOptions.unitSystem = UnitSystem.IMPERIAL
                    this.routeOptions = com.here.sdk.routing.RouteOptions(OptimizationMode.FASTEST, 5, null)
                },
                callback
            )
            TranportMode.WALK -> routingEngine?.calculateRoute(
                waypoints,
                PedestrianOptions().apply {
                    this.textOptions.unitSystem = UnitSystem.IMPERIAL
                    this.routeOptions = com.here.sdk.routing.RouteOptions(OptimizationMode.FASTEST, 5, null)
                },
                callback
            )
            else -> routingEngine?.calculateRoute(
                waypoints,
                CarOptions().apply {
                    this.textOptions.unitSystem = UnitSystem.IMPERIAL
                    this.routeOptions = com.here.sdk.routing.RouteOptions(OptimizationMode.FASTEST, 5, null)
                },
                callback
            )
        }
    }

    fun addEventListener(listener: NavEventListener) {
        navEventListeners.add(listener)
    }

    fun removeEventListener(listener: NavEventListener) {
        navEventListeners.remove(listener)
    }

    @SuppressLint("MissingPermission")
    fun startNavigation(route: Route) {
        routeAlternatives.firstOrNull { it.first.javaClass == route.javaClass }?.let { pair ->
            navState = NavigationState.STARTED
            maneuvers = route.maneuvers
            navigator?.route = pair.second
            resetListeners()
            setupListeners()

            navEventListeners.forEach { listener ->
                listener.onNavigationStarted(route)
            }
        } ?: Timber.e("No here routes")
    }

    @SuppressLint("MissingPermission")
    fun cancelNavigation() {
        navigator?.route = null
        resetListeners()
        navState = NavigationState.STOPPED
        navEventListeners.forEach {
            it.onNavigationEnded(NavigationEndReason.NAVIGATION_CANCELED, null)
        }
    }

    fun requestReroute() {
        lastRerouteFailed = false
        handleRerouteRequest()
    }

    private fun handleRerouteRequest() {
        val currentLoc = locationProvider.lastKnownLocation!!
        val reroutePoints = mutableListOf<HereWaypoint>()
        val stops = mutableListOf<RouteStop>()
        reroutePoints.add(HereWaypoint(GeoCoordinates(currentLoc.coordinates.latitude, currentLoc.coordinates.longitude)))
        navigator?.route?.sections?.last()?.polyline?.last()?.let { destination ->
            reroutePoints.add(HereWaypoint(destination))
        }

        navEventListeners.forEach { listener ->
            listener.onRerouteRequested(stops = stops)
        }
        routingEngine?.calculateRoute(reroutePoints, CarOptions()) { routingError, hereRoutes ->
            routingError?.let { error ->
                Timber.e(error.name)

                // Only the first of consecutive failed reroutes should trigger a callback.
                if (!lastRerouteFailed) {
                    lastRerouteFailed = true
                    navEventListeners.forEach { listener -> listener.onRerouteFailed(error.name) }
                }
            } ?: run {
                lastRerouteFailed = false
                hereRoutes?.get(0)?.let { newRoute ->
                    maneuvers = newRoute.getManeuvers()
                    navigator?.route = newRoute
                    navState = NavigationState.ON_ROUTE
                    navEventListeners.forEach { listener ->
                        listener.onRerouteRetrieved(newRoute.toRoute())
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        setupRouteProgressListener()
        setupDestinationReachedListener()
        setupRouteDeviationListener()
    }

    private fun setupRouteProgressListener() {
        navigator?.routeProgressListener = RouteProgressListener { routeProgress ->
            deviationCount = 0
            lastRerouteFailed = false
            navState = NavigationState.ON_ROUTE

            val nextManeuverProgress = routeProgress.maneuverProgress.firstOrNull() ?: run {
                        Timber.d("No next maneuver available.")
                        return@RouteProgressListener
                    }
            val maneuverPrompt = maneuvers?.get(nextManeuverProgress.maneuverIndex) ?: return@RouteProgressListener

            navEventListeners.forEach { listener ->
                listener.onRouteProgress(
                    maneuverPrompt,
                    nextManeuverProgress.remainingDistanceInMeters.toDouble()
                )
                routeProgress.sectionProgress[routeProgress.sectionIndex].let { section ->
                    listener.onEtaUpdated(
                        section.remainingDurationInSeconds,
                        section.remainingDistanceInMeters.toDouble()
                    )
                }
            }
        }
    }

    private fun setupDestinationReachedListener() {
        navigator?.destinationReachedListener = DestinationReachedListener {
            navState = NavigationState.FINISHED
            navEventListeners.forEach { it.onDestinationReached() }
        }
    }

    private fun setupRouteDeviationListener() {
        navigator?.routeDeviationListener = RouteDeviationListener { routeDeviation ->
            deviationCount++
            navState = NavigationState.OFF_ROUTE

            if (routeDeviation.lastLocationOnRoute == null) {
                Timber.d("User was never following the route.")
            } else {
                // Get current geographic coordinates.
                val currentMapMatchedLocation = routeDeviation.currentLocation.mapMatchedLocation
                val currentGeoCoordinates = currentMapMatchedLocation?.coordinates ?: routeDeviation.currentLocation.originalLocation.coordinates

                // Get last geographic coordinates.
                val lastMapMatchedLocationOnRoute = routeDeviation.lastLocationOnRoute!!.mapMatchedLocation
                val lastGeoCoordinates = lastMapMatchedLocationOnRoute?.coordinates
                    ?: routeDeviation.lastLocationOnRoute!!.originalLocation.coordinates
                val distanceInMeters = currentGeoCoordinates.distanceTo(lastGeoCoordinates).toInt()
                Timber.d("RouteDeviation in meters is $distanceInMeters, state:${navState.name}")
            }

            if (deviationCount > 3) {
                deviationCount = 0
                handleRerouteRequest()
            }
        }
    }

    private fun resetListeners() {
        navigator?.routeProgressListener = null
        navigator?.destinationReachedListener = null
        navigator?.routeDeviationListener = null
    }

    private fun hasLocationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    val lastKnownLocation: Location?
        get() = locationProvider.lastKnownLocation?.let {
            Location("").apply {
                this.latitude = it.coordinates.latitude
                this.longitude = it.coordinates.longitude
            }
        }

    fun isCurrentlyNavigating(): Boolean {
        return (navigator?.route != null)
    }

    val state : NavigationState
        get() = navState

    enum class NavigationState {
        STOPPED,
        STARTED,
        ON_ROUTE,
        OFF_ROUTE,
        PAUSED,
        FINISHED
    }
}