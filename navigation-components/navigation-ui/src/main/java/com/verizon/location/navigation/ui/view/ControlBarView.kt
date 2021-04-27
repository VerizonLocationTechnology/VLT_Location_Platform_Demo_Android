package com.verizon.location.navigation.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import com.verizon.location.navigation.ui.R
import com.verizon.location.common.util.DistanceUtil
import com.verizon.location.common.util.UiUtil
import java.util.*

class ControlBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    val etaInfoContainer: LinearLayout
    val startButton: MaterialButton
    val detailsButton: MaterialButton
    val endButton: MaterialButton
    private val distanceView: TextView
    private val timeView: TextView

    init {
        orientation = VERTICAL
        val pad = UiUtil.convertDpToPx(context, 4f).toInt()
        setPadding(pad*5, pad*3, pad*5, pad*3)
        inflate(context, R.layout.view_control_bar, this)
        etaInfoContainer = findViewById(R.id.eta_info)
        startButton = findViewById(R.id.start_button)
        detailsButton = findViewById(R.id.details_button)
        endButton = findViewById(R.id.end_button)
        distanceView = findViewById(R.id.distance_text)
        timeView = findViewById(R.id.time_text)
    }

    fun hide() {
        startButton.text = context.getString(R.string.start_button_text)
        timeView.text = ""
        distanceView.text = ""
        visibility = View.INVISIBLE
    }

    fun showRouteOverviewActions() {
        etaInfoContainer.visibility = VISIBLE
        endButton.visibility = GONE
        detailsButton.visibility = VISIBLE
        startButton.visibility = VISIBLE
        startButton.isEnabled = true
        if (visibility != VISIBLE) {
            visibility = VISIBLE
        }
    }

    fun showRouteProgressActions() {
        etaInfoContainer.visibility = VISIBLE
        startButton.visibility = GONE
        detailsButton.visibility = VISIBLE
        endButton.visibility = VISIBLE
        if (visibility != VISIBLE) {
            visibility = VISIBLE
        }
    }

    fun showEndOfRouteActions() {
        etaInfoContainer.visibility = INVISIBLE
        startButton.visibility = GONE
        detailsButton.visibility = GONE
        endButton.visibility = VISIBLE
        if (visibility != VISIBLE) {
            visibility = VISIBLE
        }
    }

    fun update(secondsToTravel: Int, metersToTravel: Double) {
        updateTime(secondsToTravel)
        updateDistance(metersToTravel)
    }

    private fun updateTime(secondsToTravel: Int) {
        val duration = formattedDuration(secondsToTravel)
        timeView.text = String.format(Locale.getDefault(),"%s", duration)
    }

    private fun formattedDuration(secondsToTravel: Int): String {
        return  if (secondsToTravel < 45) {
            "${secondsToTravel} ${context.getString(R.string.sec_abbreviated_text)}"
        } else {
            val minutes = (secondsToTravel / 60)
            "$minutes ${context.getString(R.string.min_abbreviated_text)}"
        }
    }

    private fun updateDistance(metersToTravel: Double) {
        distanceView.text = "(${DistanceUtil.formattedDistance(metersToTravel)})"
    }
}