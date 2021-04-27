package com.verizon.location.navigation.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.verizon.location.common.model.ManeuverPrompt
import com.verizon.location.common.model.RouteOptions
import com.verizon.location.common.model.RouteStop
import com.verizon.location.navigation.ui.util.ConversionUtil
import com.verizon.location.navigation.ui.view.RouteDestinationView
import com.verizon.location.navigation.ui.view.ControlBarView
import com.verizon.location.navigation.ui.view.ManeuverBarView
import com.verizon.location.navigation.ui.view.RouteOverviewView
import com.verizon.location.navigation.ui.view.RouteDetailsView
import timber.log.Timber

open class NavigationUiFragment : Fragment(R.layout.fragment_navigation_ui) {

    protected lateinit var routeOverviewBar: RouteOverviewView
    protected lateinit var maneuverBar: ManeuverBarView
    protected lateinit var destinationView: RouteDestinationView
    protected lateinit var retryRerouteSnackbar: Snackbar
    protected lateinit var controlBar: ControlBarView
    protected lateinit var routeDetailsView: RouteDetailsView
    private var viewPortListener: ViewPortListener? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        maneuverBar = view.findViewById(R.id.maneuver_bar)
        destinationView = view.findViewById(R.id.destination_view)
        routeOverviewBar = view.findViewById(R.id.route_overview)
        controlBar = view.findViewById(R.id.control_bar)
        routeDetailsView = view.findViewById(R.id.route_details_view)
        retryRerouteSnackbar = Snackbar.make(view, "Rerouting failed", Snackbar.LENGTH_INDEFINITE)
    }

    protected fun showRouteOverview() {
        if (routeDetailsView.isShowing()) {
            routeDetailsView.hide()
        }
        maneuverBar.hide()
        destinationView.hide()
        routeOverviewBar.show()
        retryRerouteSnackbar.dismiss()
        viewPortListener?.onViewPortChanged(routeOverviewBar.height, controlBar.height)
    }

    protected fun updateRouteOverview(
        stops: List<RouteStop>,
        secondsToTravel: Int,
        metersToTravel: Double,
        prompts: List<ManeuverPrompt>
    ) {
        routeOverviewBar.update(stops)
        controlBar.update(secondsToTravel, metersToTravel)
        controlBar.showRouteOverviewActions()
        routeDetailsView.setRouteDetails(prompts, secondsToTravel, metersToTravel, stops)
    }

    protected fun showRouteProgress() {
        if (routeDetailsView.isShowing()) {
            routeDetailsView.hide()
        }
        routeOverviewBar.hide()
        destinationView.hide()
        retryRerouteSnackbar.dismiss()
        controlBar.showRouteProgressActions()
        maneuverBar.show()
        viewPortListener?.onViewPortChanged(maneuverBar.height, controlBar.height)
    }

    protected fun updateRouteProgress(nextManeuver: ManeuverPrompt, maneuverMetersToTravel: Double) {
        activity?.runOnUiThread {
            routeDetailsView.routeDistance = maneuverMetersToTravel
            routeDetailsView.setCurrentManeuver(nextManeuver)
            maneuverBar.update(nextManeuver, maneuverMetersToTravel)
            if (!routeDetailsView.isShowing()) {
                controlBar.showRouteProgressActions()
            } else {
                Toast.makeText(context, "${nextManeuver.text}", Toast.LENGTH_LONG).show()
            }
        }
        viewPortListener?.onViewPortChanged(maneuverBar.height, controlBar.height)
    }

    protected fun showReroutingFailed(listener: View.OnClickListener) {
        controlBar.hide()
        retryRerouteSnackbar
                .setAction("Retry", listener)
                .show()
    }

    protected fun showRouteDestination() {
        routeOverviewBar.hide()
        maneuverBar.hide()
        destinationView.show("You have reached your destination")
        controlBar.showEndOfRouteActions()
        retryRerouteSnackbar.dismiss()
        viewPortListener?.onViewPortChanged(destinationView.height, controlBar.height)
    }

    protected fun updateRouteDetails(maneuvers: List<ManeuverPrompt>) {
        routeDetailsView.updateRouteManeuvers(maneuvers)
    }

    protected fun updateEta(secondsToTravel: Int, metersToTravel: Double) {
        activity?.runOnUiThread {
            routeDetailsView.routeTime = secondsToTravel
            routeDetailsView.routeDistance = metersToTravel
            routeDetailsView.tripTimeView.text =
                "${ConversionUtil.convertTimeToDisplayString(routeDetailsView.routeTime)} (${
                    ConversionUtil.convertDistanceToDisplayString(routeDetailsView.routeDistance)
                })"
            controlBar.update(secondsToTravel, metersToTravel)
        }
    }

    protected fun hideAllViews() {
        if (routeDetailsView.isShowing()) {
            routeDetailsView.hide()
        }
        maneuverBar.hide()
        controlBar.hide()
        routeOverviewBar.hide()
        destinationView.hide()
        retryRerouteSnackbar.dismiss()
        viewPortListener?.onViewPortChanged(0, 0)
    }

    protected fun showRouteDetails() {
        routeOverviewBar.hide()
        controlBar.hide()
        maneuverBar.hide()
        routeDetailsView.show()
    }

    protected val selectedRouteOptions: RouteOptions
        get() = routeOverviewBar.getSelectedRouteOption()

    protected val isRouteOverviewShowing: Boolean
        get() = routeOverviewBar.isShown

    protected val isRouteProgressShowing: Boolean
        get() = maneuverBar.isShown

    protected val isRouteDestinationShowing: Boolean
        get() = destinationView.isShown

    protected val isRouteDetailsShowing: Boolean
        get() = routeDetailsView.isShowing()

    fun setViewPortListener(listener: ViewPortListener) {
        viewPortListener = listener
    }

    interface ViewPortListener {
        fun onViewPortChanged(top: Int, bottom: Int)
    }
}