package com.verizon.location.navigation.util

import com.verizon.location.common.model.ManeuverPrompt
import com.here.sdk.routing.Maneuver as HereManeuver
import com.here.sdk.routing.ManeuverAction as HereManeuverAction
import com.verizon.location.common.model.ManeuverType

fun HereManeuver.toManeuverPrompt(): ManeuverPrompt {
    return ManeuverPrompt(
        this.action.toManeuverType(),
        this.text
    )
}

fun HereManeuverAction.toManeuverType(): ManeuverType = when(this) {
    HereManeuverAction.ARRIVE -> ManeuverType.ARRIVE
    HereManeuverAction.CONTINUE_ON -> ManeuverType.CONTINUE
    HereManeuverAction.DEPART -> ManeuverType.CONTINUE
    HereManeuverAction.FERRY -> ManeuverType.CONTINUE
    HereManeuverAction.LEFT_TURN -> ManeuverType.LEFT_TURN
    HereManeuverAction.SHARP_LEFT_TURN -> ManeuverType.LEFT_SHARP_TURN
    HereManeuverAction.LEFT_U_TURN -> ManeuverType.LEFT_U_TURN
    HereManeuverAction.LEFT_EXIT -> ManeuverType.LEFT_EXIT
    HereManeuverAction.LEFT_FORK -> ManeuverType.LEFT_FORK
    HereManeuverAction.LEFT_RAMP -> ManeuverType.LEFT_RAMP
    HereManeuverAction.LEFT_ROUNDABOUT_ENTER -> ManeuverType.LEFT_ROUNDABOUT_ENTER
    HereManeuverAction.LEFT_ROUNDABOUT_EXIT1 -> ManeuverType.LEFT_ROUNDABOUT_EXIT1
    HereManeuverAction.LEFT_ROUNDABOUT_EXIT2 -> ManeuverType.LEFT_ROUNDABOUT_EXIT2
    HereManeuverAction.LEFT_ROUNDABOUT_EXIT3 -> ManeuverType.LEFT_ROUNDABOUT_EXIT3
    HereManeuverAction.LEFT_ROUNDABOUT_EXIT4 -> ManeuverType.LEFT_ROUNDABOUT_EXIT4
    HereManeuverAction.LEFT_ROUNDABOUT_EXIT5 -> ManeuverType.LEFT_ROUNDABOUT_EXIT5
    HereManeuverAction.LEFT_ROUNDABOUT_EXIT6 -> ManeuverType.LEFT_ROUNDABOUT_EXIT6
    HereManeuverAction.LEFT_ROUNDABOUT_EXIT7 -> ManeuverType.LEFT_ROUNDABOUT_EXIT7
    HereManeuverAction.LEFT_ROUNDABOUT_EXIT8 -> ManeuverType.LEFT_ROUNDABOUT_EXIT8
    HereManeuverAction.LEFT_ROUNDABOUT_EXIT9 -> ManeuverType.LEFT_ROUNDABOUT_EXIT9
    HereManeuverAction.LEFT_ROUNDABOUT_EXIT10 -> ManeuverType.LEFT_ROUNDABOUT_EXIT10
    HereManeuverAction.LEFT_ROUNDABOUT_EXIT11 -> ManeuverType.LEFT_ROUNDABOUT_EXIT11
    HereManeuverAction.LEFT_ROUNDABOUT_EXIT12 -> ManeuverType.LEFT_ROUNDABOUT_EXIT12
    HereManeuverAction.LEFT_ROUNDABOUT_PASS -> ManeuverType.LEFT_ROUNDABOUT_PASS
    HereManeuverAction.MIDDLE_FORK -> ManeuverType.CONTINUE // TODO
    HereManeuverAction.RIGHT_TURN -> ManeuverType.RIGHT_TURN
    HereManeuverAction.RIGHT_U_TURN -> ManeuverType.RIGHT_U_TURN
    HereManeuverAction.SHARP_RIGHT_TURN -> ManeuverType.RIGHT_SHARP_TURN
    HereManeuverAction.RIGHT_EXIT -> ManeuverType.RIGHT_EXIT
    HereManeuverAction.RIGHT_FORK -> ManeuverType.RIGHT_FORK
    HereManeuverAction.RIGHT_RAMP -> ManeuverType.RIGHT_RAMP
    HereManeuverAction.RIGHT_ROUNDABOUT_ENTER -> ManeuverType.RIGHT_ROUNDABOUT_ENTER
    HereManeuverAction.RIGHT_ROUNDABOUT_EXIT1 -> ManeuverType.RIGHT_ROUNDABOUT_EXIT1
    HereManeuverAction.RIGHT_ROUNDABOUT_EXIT2 -> ManeuverType.CONTINUE // TODO
    HereManeuverAction.RIGHT_ROUNDABOUT_EXIT3 -> ManeuverType.CONTINUE // TODO
    HereManeuverAction.RIGHT_ROUNDABOUT_EXIT4 -> ManeuverType.CONTINUE // TODO
    HereManeuverAction.RIGHT_ROUNDABOUT_EXIT5 -> ManeuverType.CONTINUE // TODO
    HereManeuverAction.RIGHT_ROUNDABOUT_EXIT6 -> ManeuverType.CONTINUE // TODO
    HereManeuverAction.RIGHT_ROUNDABOUT_EXIT7 -> ManeuverType.CONTINUE // TODO
    HereManeuverAction.RIGHT_ROUNDABOUT_EXIT8 -> ManeuverType.CONTINUE // TODO
    HereManeuverAction.RIGHT_ROUNDABOUT_EXIT9 -> ManeuverType.CONTINUE // TODO
    HereManeuverAction.RIGHT_ROUNDABOUT_EXIT10 -> ManeuverType.RIGHT_ROUNDABOUT_EXIT10
    HereManeuverAction.RIGHT_ROUNDABOUT_EXIT11 -> ManeuverType.CONTINUE // TODO
    HereManeuverAction.RIGHT_ROUNDABOUT_EXIT12 -> ManeuverType.CONTINUE // TODO
    HereManeuverAction.RIGHT_ROUNDABOUT_PASS -> ManeuverType.CONTINUE // TODO
    else -> ManeuverType.CONTINUE
}