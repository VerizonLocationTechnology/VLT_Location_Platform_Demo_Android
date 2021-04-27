package com.verizon.location.navigation.ui.util

import com.verizon.location.common.model.ManeuverType
import com.verizon.location.common.model.ManeuverType.ARRIVE
import com.verizon.location.common.model.ManeuverType.CONTINUE
import com.verizon.location.common.model.ManeuverType.DEPART
import com.verizon.location.common.model.ManeuverType.FERRY
import com.verizon.location.common.model.ManeuverType.LEFT_EXIT
import com.verizon.location.common.model.ManeuverType.LEFT_FORK
import com.verizon.location.common.model.ManeuverType.LEFT_RAMP
import com.verizon.location.common.model.ManeuverType.LEFT_ROUNDABOUT_ENTER
import com.verizon.location.common.model.ManeuverType.LEFT_ROUNDABOUT_EXIT1
import com.verizon.location.common.model.ManeuverType.LEFT_ROUNDABOUT_EXIT10
import com.verizon.location.common.model.ManeuverType.LEFT_ROUNDABOUT_EXIT11
import com.verizon.location.common.model.ManeuverType.LEFT_ROUNDABOUT_EXIT12
import com.verizon.location.common.model.ManeuverType.LEFT_ROUNDABOUT_EXIT2
import com.verizon.location.common.model.ManeuverType.LEFT_ROUNDABOUT_EXIT3
import com.verizon.location.common.model.ManeuverType.LEFT_ROUNDABOUT_EXIT4
import com.verizon.location.common.model.ManeuverType.LEFT_ROUNDABOUT_EXIT5
import com.verizon.location.common.model.ManeuverType.LEFT_ROUNDABOUT_EXIT6
import com.verizon.location.common.model.ManeuverType.LEFT_ROUNDABOUT_EXIT7
import com.verizon.location.common.model.ManeuverType.LEFT_ROUNDABOUT_EXIT8
import com.verizon.location.common.model.ManeuverType.LEFT_ROUNDABOUT_EXIT9
import com.verizon.location.common.model.ManeuverType.LEFT_ROUNDABOUT_PASS
import com.verizon.location.common.model.ManeuverType.LEFT_TURN
import com.verizon.location.common.model.ManeuverType.LEFT_U_TURN
import com.verizon.location.common.model.ManeuverType.MIDDLE_FORK
import com.verizon.location.common.model.ManeuverType.RIGHT_EXIT
import com.verizon.location.common.model.ManeuverType.RIGHT_FORK
import com.verizon.location.common.model.ManeuverType.RIGHT_RAMP
import com.verizon.location.common.model.ManeuverType.RIGHT_ROUNDABOUT_ENTER
import com.verizon.location.common.model.ManeuverType.RIGHT_ROUNDABOUT_EXIT1
import com.verizon.location.common.model.ManeuverType.RIGHT_ROUNDABOUT_EXIT10
import com.verizon.location.common.model.ManeuverType.RIGHT_ROUNDABOUT_EXIT11
import com.verizon.location.common.model.ManeuverType.RIGHT_ROUNDABOUT_EXIT12
import com.verizon.location.common.model.ManeuverType.RIGHT_ROUNDABOUT_EXIT2
import com.verizon.location.common.model.ManeuverType.RIGHT_ROUNDABOUT_EXIT3
import com.verizon.location.common.model.ManeuverType.RIGHT_ROUNDABOUT_EXIT4
import com.verizon.location.common.model.ManeuverType.RIGHT_ROUNDABOUT_EXIT5
import com.verizon.location.common.model.ManeuverType.RIGHT_ROUNDABOUT_EXIT6
import com.verizon.location.common.model.ManeuverType.RIGHT_ROUNDABOUT_EXIT7
import com.verizon.location.common.model.ManeuverType.RIGHT_ROUNDABOUT_EXIT8
import com.verizon.location.common.model.ManeuverType.RIGHT_ROUNDABOUT_EXIT9
import com.verizon.location.common.model.ManeuverType.RIGHT_ROUNDABOUT_PASS
import com.verizon.location.common.model.ManeuverType.RIGHT_TURN
import com.verizon.location.common.model.ManeuverType.RIGHT_U_TURN
import com.verizon.location.common.model.ManeuverType.LEFT_SHARP_TURN
import com.verizon.location.common.model.ManeuverType.RIGHT_SHARP_TURN
import com.verizon.location.common.model.ManeuverType.LEFT_SLIGHT_TURN
import com.verizon.location.common.model.ManeuverType.RIGHT_SLIGHT_TURN
import com.verizon.location.navigation.ui.R
import java.util.*

object NavatarUtil {

    fun getNavatar(type: ManeuverType): Int {
        return when (type) {
            DEPART -> R.drawable.navatar_continue // TODO
            ARRIVE -> R.drawable.navatar_continue // TODO
            LEFT_U_TURN -> R.drawable.navatar_uturn_left
            LEFT_SHARP_TURN -> R.drawable.navatar_sharp_left
            LEFT_TURN -> R.drawable.navatar_left
            LEFT_SLIGHT_TURN -> R.drawable.navatar_slight_left
            CONTINUE -> R.drawable.navatar_continue
            RIGHT_SLIGHT_TURN -> R.drawable.navatar_slight_right
            RIGHT_TURN -> R.drawable.navatar_right
            RIGHT_SHARP_TURN -> R.drawable.navatar_sharp_right
            RIGHT_U_TURN -> R.drawable.navatar_uturn_right
            LEFT_EXIT -> R.drawable.navatar_exit_left
            RIGHT_EXIT -> R.drawable.navatar_exit_right
            LEFT_RAMP -> R.drawable.navatar_ramp_left
            RIGHT_RAMP -> R.drawable.navatar_ramp_right
            LEFT_FORK -> R.drawable.navatar_fork_left
            MIDDLE_FORK -> R.drawable.navatar_continue // TODO
            RIGHT_FORK -> R.drawable.navatar_fork_right
            FERRY -> R.drawable.navatar_continue // TODO
            LEFT_ROUNDABOUT_ENTER -> R.drawable.navatar_round_a_bout // TODO
            RIGHT_ROUNDABOUT_ENTER -> R.drawable.navatar_round_a_bout // TODO
            LEFT_ROUNDABOUT_PASS -> R.drawable.navatar_round_a_bout // TODO
            RIGHT_ROUNDABOUT_PASS -> R.drawable.navatar_round_a_bout // TODO
            LEFT_ROUNDABOUT_EXIT1 -> R.drawable.navatar_round_a_bout // TODO
            LEFT_ROUNDABOUT_EXIT2 -> R.drawable.navatar_round_a_bout // TODO
            LEFT_ROUNDABOUT_EXIT3 -> R.drawable.navatar_round_a_bout // TODO
            LEFT_ROUNDABOUT_EXIT4 -> R.drawable.navatar_round_a_bout // TODO
            LEFT_ROUNDABOUT_EXIT5 -> R.drawable.navatar_round_a_bout // TODO
            LEFT_ROUNDABOUT_EXIT6 -> R.drawable.navatar_round_a_bout // TODO
            LEFT_ROUNDABOUT_EXIT7 -> R.drawable.navatar_round_a_bout // TODO
            LEFT_ROUNDABOUT_EXIT8 -> R.drawable.navatar_round_a_bout // TODO
            LEFT_ROUNDABOUT_EXIT9 -> R.drawable.navatar_round_a_bout // TODO
            LEFT_ROUNDABOUT_EXIT10 -> R.drawable.navatar_round_a_bout // TODO
            LEFT_ROUNDABOUT_EXIT11 -> R.drawable.navatar_round_a_bout // TODO
            LEFT_ROUNDABOUT_EXIT12 -> R.drawable.navatar_round_a_bout // TODO
            RIGHT_ROUNDABOUT_EXIT1 -> R.drawable.navatar_round_a_bout // TODO
            RIGHT_ROUNDABOUT_EXIT2 -> R.drawable.navatar_round_a_bout // TODO
            RIGHT_ROUNDABOUT_EXIT3 -> R.drawable.navatar_round_a_bout // TODO
            RIGHT_ROUNDABOUT_EXIT4 -> R.drawable.navatar_round_a_bout // TODO
            RIGHT_ROUNDABOUT_EXIT5 -> R.drawable.navatar_round_a_bout // TODO
            RIGHT_ROUNDABOUT_EXIT6 -> R.drawable.navatar_round_a_bout // TODO
            RIGHT_ROUNDABOUT_EXIT7 -> R.drawable.navatar_round_a_bout // TODO
            RIGHT_ROUNDABOUT_EXIT8 -> R.drawable.navatar_round_a_bout // TODO
            RIGHT_ROUNDABOUT_EXIT9 -> R.drawable.navatar_round_a_bout // TODO
            RIGHT_ROUNDABOUT_EXIT10 -> R.drawable.navatar_round_a_bout // TODO
            RIGHT_ROUNDABOUT_EXIT11 -> R.drawable.navatar_round_a_bout // TODO
            RIGHT_ROUNDABOUT_EXIT12 -> R.drawable.navatar_round_a_bout // TODO
            else -> R.drawable.navatar_continue
        }
    }

}