package com.verizon.location.navigation.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.google.android.material.button.MaterialButton
import com.verizon.location.common.model.RouteOptions
import com.verizon.location.navigation.ui.R
import com.verizon.location.common.model.RouteStop
import com.verizon.location.common.model.TranportMode
import com.verizon.location.common.util.UiUtil

class RouteOverviewView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val walkButton: MaterialButton
    private val driveButton: MaterialButton
    private val truckButton: MaterialButton
    private var routeStopsView: RouteStopsView
    var listener: RouteOverviewListener? = null
    private var selectedRouteOption: TranportMode? = null

    init {
        orientation = VERTICAL
        inflate(context, R.layout.view_route_overview, this)
        routeStopsView = findViewById(R.id.route_stops)
        routeStopsView.setStopClickListener(object : RouteStopsView.StopClickListener {
            override fun onStopClicked(position: Int) {
                listener?.onChangeStopSelected(position)
            }
        })

        val pad = UiUtil.convertDpToPx(context, 4f).toInt()
        setPadding(4*pad, pad, 4*pad, 0)

        walkButton = findViewById(R.id.walk_button)
        driveButton = findViewById(R.id.drive_button)
        truckButton = findViewById(R.id.truck_button)
        setUpRouteOptionListeners()
    }

    fun hide() {
        if (visibility == VISIBLE) {
            visibility = INVISIBLE
        }
    }

    fun show() {
        if (visibility != VISIBLE) {
            visibility = VISIBLE
        }
    }

    fun update(stops: List<RouteStop>) {
        routeStopsView.setStops(stops)
    }

    private fun setUpRouteOptionListeners() {
        walkButton.setOnClickListener(object: OnClickListener {
            override fun onClick(v: View?) {
                listener?.onRouteOptionsChange(
                    RouteOptions(TranportMode.WALK)
                )
            }
        })
        driveButton.setOnClickListener(object: OnClickListener {
            override fun onClick(v: View?) {
                listener?.onRouteOptionsChange(
                    RouteOptions(TranportMode.DRIVE)
                )
            }
        })
        truckButton.setOnClickListener(object: OnClickListener {
            override fun onClick(v: View?) {
                listener?.onRouteOptionsChange(
                    RouteOptions(TranportMode.TRUCK)
                )
            }
        })
    }

    fun getSelectedRouteOption(): RouteOptions {
        if(walkButton.isChecked) {
            selectedRouteOption = TranportMode.WALK
        } else if (driveButton.isChecked) {
            selectedRouteOption = TranportMode.DRIVE
        } else {
            selectedRouteOption = TranportMode.TRUCK
        }
        return RouteOptions(selectedRouteOption)
    }

    interface RouteOverviewListener {
        fun onChangeStopSelected(position: Int) {}
        fun onRouteOptionsChange(option: RouteOptions) {}
    }

}