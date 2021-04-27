package com.verizon.location.navdemo

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentResultListener
import com.verizon.location.common.NavEventListener
import com.verizon.location.common.NavEventListener.NavigationEndReason
import com.verizon.location.common.model.*
import com.verizon.location.navigation.NavigationManager
import com.verizon.location.navigation.ui.NavigationUiFragment
import com.verizon.location.navigation.ui.view.RouteDetailsView
import com.verizon.location.navigation.ui.view.RouteOverviewView
import com.verizon.location.search.ui.SearchFragment

class NavigationFragment : NavigationUiFragment(), NavEventListener {

    private var navManager: NavigationManager? = null
    private var isNavServiceBound = false

    private var selectedRoute: Route? = null
    private var stops = mutableListOf<RouteStop>()
    private var routeAlternatives: List<Route>? = null
    private var changeStopPosition = 0

    var routeDetailsViewListener = object: RouteDetailsView.RouteDetailsViewListener {
        override fun onRouteDetailsViewClosed(secondsToTravel: Int, metersToTravel: Double) {
            (activity as NavigationDemoActivity).toggleFabButtons(true)
            when {
                navManager?.state == NavigationManager.NavigationState.FINISHED -> {
                    controlBar.showEndOfRouteActions()
                }
                navManager?.state == NavigationManager.NavigationState.STOPPED -> {
                    showRouteOverview()
                    controlBar.update(secondsToTravel, metersToTravel)
                    controlBar.showRouteOverviewActions()
                }
                navManager?.isCurrentlyNavigating() == true -> {
                    maneuverBar.show()
                    controlBar.update(secondsToTravel, metersToTravel)
                    controlBar.showRouteOverviewActions()
                }
                else -> {
                    maneuverBar.show()
                    routeOverviewBar.update(stops)
                    routeOverviewBar.show()
                    controlBar.update(secondsToTravel, metersToTravel)
                    controlBar.showRouteOverviewActions()
                }
            }
        }
    }

    private val navServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            return if (binder is NavigationService.NavigationServiceBinder) {
                navManager = binder.service.navManager
                navManager?.addEventListener(this@NavigationFragment)
                isNavServiceBound = true
            } else {
                throw IllegalArgumentException("Given IBinder is not an instance of the expected implementation type.")
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            navManager?.removeEventListener(this@NavigationFragment)
            navManager = null
            isNavServiceBound = false
        }
    }

    private val backPressCallback: OnBackPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            when {
                isRouteDetailsShowing -> {
                    if (navManager?.state == NavigationManager.NavigationState.STOPPED) {
                        showRouteOverview()
                    } else {
                        showRouteProgress()
                    }
                }
                isRouteOverviewShowing -> {
                    navManager?.cancelNavigation()
                    hideAllViews()
                }
                isRouteProgressShowing -> {
                    navManager?.cancelNavigation()
                    hideAllViews()
                }
                isRouteDestinationShowing -> {
                    hideAllViews()
                }
                navManager?.isCurrentlyNavigating() == true -> {
                    navManager?.cancelNavigation()
                    hideAllViews()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        childFragmentManager.setFragmentResultListener(
            REQUEST_CHANGE_STOP,
            this,
            FragmentResultListener { requestKey, result ->
                stops.removeAt(changeStopPosition)
                stops.add(
                    changeStopPosition,
                    RouteStop(
                        result.getString(SearchFragment.ARG_NAME) ?: "",
                        Location("").apply {
                            this.latitude = result.getDouble(SearchFragment.ARG_LATITUDE)
                            this.longitude = result.getDouble(SearchFragment.ARG_LONGITUDE)
                        }
                    )
                )
                getRoutes(stops)
            })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val intent = Intent(requireContext(), NavigationService::class.java)
        requireActivity().startService(intent)
        requireActivity().bindService(intent, navServiceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        routeOverviewBar.listener = object: RouteOverviewView.RouteOverviewListener {
            override fun onChangeStopSelected(position: Int) {
                changeStopPosition = position
                showSearchToChangeStop()
            }

            override fun onRouteOptionsChange(option: RouteOptions) {
                navManager?.getRoutes(stops, option)
            }
        }
        controlBar.startButton.setOnClickListener {
            handleStartNavigationClicked()
        }
        controlBar.detailsButton.setOnClickListener {
            routeDetailsView.setRouteDetailsViewListener(routeDetailsViewListener)
            showRouteDetails()
            (activity as NavigationDemoActivity).toggleFabButtons(false)
        }
        controlBar.endButton.setOnClickListener {
            navManager?.cancelNavigation()
            hideAllViews()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(this, backPressCallback)
    }

    private fun showSearchToChangeStop() {
        childFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .add(
                R.id.search_frag_container,
                SearchFragment.newInstance(REQUEST_CHANGE_STOP, navManager?.lastKnownLocation)
            )
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        navManager?.let {
            if (!it.isCurrentlyNavigating()) {
                requireActivity().stopService(Intent(requireActivity(), NavigationService::class.java))
            }
        }
        requireActivity().unbindService(navServiceConnection)
        isNavServiceBound = false
    }

    override fun onRoutesRequested(routeStops: List<RouteStop>) {
        stops = routeStops.toMutableList()
        backPressCallback.isEnabled = true
    }

    override fun onRoutesRetrieved(routes: List<Route>, stops: List<RouteStop>) {
        routeAlternatives = routes
        selectedRoute = routes[0]
        updateRouteOverview(stops, routes[0].secondsToTravel, routes[0].metersToTravel, routes[0].maneuvers)
        updateRouteDetails(routes[0].maneuvers)
        showRouteOverview()
    }

    override fun onRoutesRetrieveFailed(error: String) {
        hideAllViews()
    }

    override fun onNavigationStarted(route: Route) {
        updateRouteProgress(route.maneuvers[0], route.metersToTravel)
        updateRouteDetails(route.maneuvers)
        showRouteProgress()
    }

    override fun onEtaUpdated(secondsToTravel: Int, metersToTravel: Double) {
        updateEta(secondsToTravel, metersToTravel)
    }

    override fun onManeuversUpdated(prompts: ArrayList<ManeuverPrompt>, metersRemaining: Double) {
        if (prompts.isNotEmpty()) {
            updateRouteProgress(prompts[0], metersRemaining)
            updateRouteDetails(prompts)
        }
    }

    override fun onRouteProgress(prompt: ManeuverPrompt, metersToTravel: Double) {
        retryRerouteSnackbar.dismiss()
        updateRouteProgress(prompt, metersToTravel)
        activity?.runOnUiThread {
            if (routeDetailsView.isShowing()) {
                if (prompt.type.toString().equals("ARRIVE")) {
                    routeDetailsView.updatePrompt("You have arrived at your destination.")
                } else {
                    routeDetailsView.routeDistance = metersToTravel
                }
            }
        }
    }

    override fun onRerouteRetrieved(route: Route) {
        selectedRoute = route
        updateRouteProgress(route.maneuvers[0], route.metersToTravel)
        updateRouteDetails(route.maneuvers)
    }

    override fun onRerouteFailed(error: String) {
        showReroutingFailed{ navManager?.requestReroute() }
    }

    override fun onNavigationEnded(reason: NavigationEndReason, destination: RouteStop?) {
        hideAllViews()
        stops.clear()
        selectedRoute = null
        backPressCallback.isEnabled = false
    }

    override fun onDestinationReached() {
        showRouteDestination()
    }

    private fun handleStartNavigationClicked() {
        selectedRoute?.let { route ->
            navManager?.startNavigation(route)
        }
    }

    fun getRoutes(start: RouteStop, destination: RouteStop) {
        navManager?.getRoutes(
            mutableListOf<RouteStop>().apply {
                this.add(start)
                this.add(destination)
            },
            selectedRouteOptions
        )
    }

    fun getRoutes(stops: List<RouteStop>) {
        navManager?.getRoutes(stops, selectedRouteOptions)
    }

    fun selectRoute(route: Route) {
        selectedRoute = route
        updateRouteDetails(route.maneuvers)
    }

    fun isNotRoutingOrNavigating(): Boolean {
        return stops.isEmpty()
    }

    fun addListener(listener: NavEventListener) {
        navManager?.addEventListener(listener)
    }

    fun removeListener(listener: NavEventListener) {
        navManager?.removeEventListener(listener)
    }

    private companion object {
        const val REQUEST_CHANGE_STOP = "changeStopRequest"
    }
}