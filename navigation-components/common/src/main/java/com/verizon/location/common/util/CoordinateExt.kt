package com.verizon.location.common.util

import com.verizon.location.commonmodels.BoundingBox
import com.verizon.location.commonmodels.Coordinate

fun List<Coordinate>.toBoundingBox(): BoundingBox {
    var xMin = 180.0f
    var xMax = -180.0f
    var yMin = 90.0f
    var yMax = -90.0f

    this.forEach { coord ->
        if (coord.lat > xMax) {
            xMax = coord.lat
        }
        if (coord.lat < xMin) {
            xMin = coord.lat
        }
        if (coord.lng > yMax) {
            yMax = coord.lng
        }
        if (coord.lng < yMin) {
            yMin = coord.lng
        }
    }

    return BoundingBox(
        northWest = Coordinate(yMax, xMin),
        southEast = Coordinate(yMin, xMax)
    )
}
