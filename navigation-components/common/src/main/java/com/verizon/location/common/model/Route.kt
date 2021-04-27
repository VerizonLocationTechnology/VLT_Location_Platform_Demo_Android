package com.verizon.location.common.model

import com.verizon.location.commonmodels.BoundingBox
import com.verizon.location.commonmodels.Coordinate

class Route(
    var polyline: List<Coordinate>,
    val maneuvers: List<ManeuverPrompt>,
    var boundingBox: BoundingBox,
    val metersToTravel: Double,
    val secondsToTravel: Int
)