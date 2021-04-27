package com.verizon.location.common.util

object DistanceUtil {

    const val FEET_PER_METER = 3.28084
    const val FEET_PER_MILE = 5280.0

    fun convertMetersToFeet(meters: Double): Double {
        return FEET_PER_METER * meters
    }

    fun convertFeetToMiles(feet: Double): Double {
        return Math.round(feet * 10.0 / FEET_PER_MILE).toInt() / 10.0
    }

    fun formattedDistance(meters: Double): String {
        val feet = convertMetersToFeet(meters)
        return if (feet > 2640) {
            val miles = convertFeetToMiles(feet)
            "$miles mi"
        } else {
            "${feet.toInt()} ft"
        }
    }

}