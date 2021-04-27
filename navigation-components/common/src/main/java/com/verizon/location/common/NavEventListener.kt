package com.verizon.location.common

import com.verizon.location.common.model.ManeuverPrompt
import com.verizon.location.common.model.Route
import com.verizon.location.common.model.RouteStop

interface NavEventListener {
    enum class NavigationEndReason {
        DESTINATION_REACHED,
        NAVIGATION_CANCELED,
        NAVIGATION_PAUSED
    }

    fun onRoutesRequested(stops: List<RouteStop>) {}
    fun onRoutesRetrieved(routeAlternatives: List<Route>, stops: List<RouteStop>) {}
    fun onRoutesRetrieveFailed(error: String) {}
    fun onRerouteRequested(stops: List<RouteStop>) {}
    fun onRerouteRetrieved(route: Route) {}
    fun onRerouteFailed(error: String) {}
    fun onNavigationStarted(route: Route) {}
    fun onNavigationEnded(reason: NavigationEndReason, destination: RouteStop?) {}
    fun onEtaUpdated(secondsToTravel: Int, metersToTravel: Double) {}
    fun onManeuversUpdated(prompts: ArrayList<ManeuverPrompt>, metersToTravel: Double) {}
    fun onRouteProgress(nextManeuver: ManeuverPrompt, distanceRemaining: Double) {}
    //fun onNavigationPaused() {}
    fun onDestinationReached() {}

}