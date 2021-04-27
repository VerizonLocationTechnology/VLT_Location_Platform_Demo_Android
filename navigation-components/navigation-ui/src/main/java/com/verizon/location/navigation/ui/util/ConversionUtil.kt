package com.verizon.location.navigation.ui.util

const val NUM_SECONDS_IN_AN_HOUR: Int = 3600
const val NUM_SECONDS_IN_A_MINUTE: Int = 60
const val LOW_RANGE_FOR_MINUTES: Int = 61
const val HIGH_RANGE_FOR_MINUTES: Int = 3599

const val NUM_FEET_IN_A_METER: Double = 3.281
const val NUM_MILES_IN_A_METER: Int = 1609
const val DESIGNATED_THRESHOLD_FOR_FEET: Double = 395.0
const val DESIGNATED_THRESHOLD_FOR_QUARTER_MILE: Double = 403.0


object ConversionUtil {

    fun convertTimeToDisplayString(timeInSeconds: Int): String {
        return when  {
            (timeInSeconds < NUM_SECONDS_IN_A_MINUTE) -> {
                "less than one minute"
            }
            (timeInSeconds == NUM_SECONDS_IN_A_MINUTE) -> {
                "1 min"
            }
            (timeInSeconds in LOW_RANGE_FOR_MINUTES..HIGH_RANGE_FOR_MINUTES) -> "${(timeInSeconds / NUM_SECONDS_IN_A_MINUTE).toString()} mins"
            (timeInSeconds >= NUM_SECONDS_IN_AN_HOUR) -> "${(timeInSeconds / NUM_SECONDS_IN_AN_HOUR).toString()} hours"
            else -> {
                (timeInSeconds / NUM_SECONDS_IN_A_MINUTE).toString()
            }
        }
    }

    fun convertDistanceToDisplayString(distanceInMeters: Double): String {
        return when {
            distanceInMeters < DESIGNATED_THRESHOLD_FOR_FEET -> String.format("%.0f ft", (distanceInMeters * NUM_FEET_IN_A_METER))
            distanceInMeters in DESIGNATED_THRESHOLD_FOR_FEET..DESIGNATED_THRESHOLD_FOR_QUARTER_MILE -> String.format("%.2f mi", (distanceInMeters / NUM_MILES_IN_A_METER))
            distanceInMeters > DESIGNATED_THRESHOLD_FOR_QUARTER_MILE -> String.format("%.1f mi", (distanceInMeters / NUM_MILES_IN_A_METER))
            else -> String.format("%.1f mi", (distanceInMeters / NUM_MILES_IN_A_METER))
        }
    }

}