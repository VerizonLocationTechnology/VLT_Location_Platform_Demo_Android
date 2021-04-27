package com.verizon.location.navigation.util

import com.here.sdk.core.GeoCoordinates
import com.verizon.location.common.model.ManeuverPrompt
import com.verizon.location.common.model.Route
import com.verizon.location.commonmodels.BoundingBox
import com.verizon.location.commonmodels.Coordinate
import com.here.sdk.routing.Route as HereRoute

fun HereRoute.toRoute(): Route {
    return Route(
        this.polyline.map { coord -> coord.toCoordinate() },
        this.getManeuvers(),
        BoundingBox(this.boundingBox.northEastCorner.toCoordinate(), this.boundingBox.southWestCorner.toCoordinate()),
        this.lengthInMeters.toDouble(),
        this.durationInSeconds
    )
}

fun List<HereRoute>.toRoutes(): List<Route> {
    return this.map { hereRoute ->
        hereRoute.toRoute()
    }
}

fun GeoCoordinates.toCoordinate() = Coordinate(this.latitude.toFloat(), this.longitude.toFloat())

fun HereRoute.getManeuvers(): List<ManeuverPrompt> {
    val maneuvers = mutableListOf<ManeuverPrompt>()
    this.sections.forEach { hereSection ->
        hereSection.maneuvers.forEach { hereManeuver ->
            maneuvers.add(hereManeuver.toManeuverPrompt())
        }
    }
    return maneuvers
}
