package com.verizon.location.navdemo

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.MenuItem.*
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.children
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.verizon.location.common.NavEventListener
import com.verizon.location.common.NavEventListener.NavigationEndReason
import com.verizon.location.common.model.ManeuverPrompt
import com.verizon.location.common.model.Route
import com.verizon.location.common.model.RouteStop
import com.verizon.location.commonmodels.BoundingBox
import com.verizon.location.commonmodels.Coordinate
import com.verizon.location.maps.*
import com.verizon.location.maps.MapGestures.OnMapLongClickListener
import com.verizon.location.maps.model.Marker
import com.verizon.location.maps.model.Polyline
import com.verizon.location.maps.model.layer.ShapeLayer
import com.verizon.location.navigation.location.InitialPositionListener
import com.verizon.location.navigation.location.PlatformLocationProvider
import com.verizon.location.navigation.ui.NavigationUiFragment.ViewPortListener
import com.verizon.location.search.ui.SearchFragment
import timber.log.Timber

class NavigationDemoActivity : AppCompatActivity() {

    private val selectedRouteLayer = ShapeLayer("selected_route")
    private val routeStopsLayer = ShapeLayer("route_stops")
    private val routeMap = mutableMapOf<Route, Polyline>()

    private lateinit var mapView: MapView
    private lateinit var navFrag: NavigationFragment
    private lateinit var mapContainer: FrameLayout
    private lateinit var modeFab: FloatingActionButton
    private lateinit var myLocationFab: FloatingActionButton
    private lateinit var fabContainer: LinearLayout

    private var vltMap: VltMap? = null
    private var locationProvider: PlatformLocationProvider? = null

    private var showTrafficFlow = true
        set(value) {
            field = value
            vltMap?.setFeatureVisible(MapFeature.TRAFFIC_FLOW, value)
        }

    private var showTrafficIncidents = false
        set(value) {
            field = value
            vltMap?.setFeatureVisible(MapFeature.TRAFFIC_INCIDENTS, value)
        }

    private var showMyLocation = false
        @SuppressLint("MissingPermission")
        set(value) {
            field = value
            if (hasLocationPermission) {
                vltMap?.setMyLocationEnabled(value && (locationProvider?.lastKnownLocation != null))
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(Timber.DebugTree())
        setContentView(R.layout.activity_nav_demo)

        mapContainer = findViewById(R.id.map_container)
        modeFab = findViewById(R.id.mode_fab)
        myLocationFab = findViewById(R.id.my_location_fab)
        fabContainer = findViewById(R.id.fab_container)

        requestLocationPermission(REQUEST_PERMISSION_LOCATION_MAP)

        locationProvider = PlatformLocationProvider(this).also {
            it.setInitialPositionListener(object : InitialPositionListener {
                override fun onInitialPositionReceived(location: Location) {
                    showMyLocation = true
                }
            })
            if (hasLocationPermission) {
                it.start()
            }
        }

        navFrag = supportFragmentManager.findFragmentByTag("NAV_FRAG") as NavigationFragment
        navFrag.setViewPortListener(object: ViewPortListener {
            override fun onViewPortChanged(top: Int, bottom: Int) {
                adjustMapUIElements(top)
            }
        })

        mapView = findViewById(R.id.map_view)
        mapView.onCreate(savedInstanceState)
        mapView.initialize(
            "Enter VLTApiKey here, contact Customer Success for your key",
            VltMapOptions(),
            object : OnMapReadyCallback {
                override fun onMapReady(map: VltMap) {
                    vltMap = map
                    setupMap(map)
                }

                override fun onMapFailedToLoad(error: MapInitializationError) {
                    Timber.e(error.errorDescription)
                }
            }
        )

        findViewById<SearchBar>(R.id.search_bar).setOnClickListener { showSearch() }
    }

    fun setupMap(map: VltMap) {
        navFrag.addListener(object : NavEventListener {
            override fun onRoutesRetrieved(routeAlternatives: List<Route>, stops: List<RouteStop>) {
                routeMap.clear()
                routeStopsLayer.removeAllShapes()
                selectedRouteLayer.removeAllShapes()

                stops.forEach { stop ->
                    routeStopsLayer.addShape(
                        Marker(
                            Coordinate(
                                stop.location.latitude.toFloat(),
                                stop.location.longitude.toFloat()
                            )
                        )
                    )
                }

                if (routeAlternatives.size > 1) {
                    for (i in 0..routeAlternatives.size-1) {
                        val line = Polyline(routeAlternatives[i].polyline, DEFAULT_ROUTE_COLOR, DEFAULT_ROUTE_THICKNESS)
                        routeMap[routeAlternatives[i]] = line
                        selectedRouteLayer.addShape(line)
                    }
                    selectedRoute = routeAlternatives.firstOrNull()
                    addRouteClickListeners()
                }
                selectedRoute?.let {
                    fitMapToBounds(it.boundingBox)
                }
            }

            @SuppressLint("MissingPermission")
            override fun onNavigationStarted(route: Route) {
                showMyLocation = true
                moveCameraToFollowUser()
            }

            override fun onRouteProgress(nextManeuver: ManeuverPrompt, distanceRemaining: Double) {
                moveCameraToFollowUser()
            }

            override fun onRerouteRequested(stops: List<RouteStop>) {
                Toast.makeText(this@NavigationDemoActivity, "Rerouting", Toast.LENGTH_SHORT).show()
            }

            override fun onRerouteRetrieved(route: Route) {
                if ( !routeMap.containsKey(route) ) {
                    val newRouteLine = Polyline(
                        coordinates = route.polyline,
                        strokeColor = SELECTED_ROUTE_COLOR,
                        strokeWidth = SELECTED_ROUTE_THICKNESS
                    )
                    routeMap[route] = newRouteLine
                    selectedRouteLayer.removeAllShapes()
                    selectedRouteLayer.addShape(newRouteLine)
                }
            }

            @SuppressLint("MissingPermission")
            override fun onNavigationEnded(reason: NavigationEndReason, destination: RouteStop?) {
                selectedRouteLayer.removeAllShapes()
                routeStopsLayer.removeAllShapes()
                vltMap?.camera?.update(tilt = 0.0)
            }
        })

        modeFab.setOnClickListener { view ->
            view?.let {
                showLayersMenu(it)
            }
        }

        myLocationFab.setOnClickListener {
            if (hasLocationPermission) {
                if (locationProvider?.lastKnownLocation == null) {
                    Snackbar.make(mapView, "No location available", Snackbar.LENGTH_LONG).show()
                    locationProvider?.start()
                } else {
                    centerOnUserLocation()
                }
            } else {
                requestLocationPermission(REQUEST_PERMISSION_LOCATION_CENTER_ON_USER)
            }
        }

        map.gestures.addOnMapLongClickListener(object : OnMapLongClickListener {
            override fun onMapLongClick(position: Coordinate) {
                if (navFrag.isNotRoutingOrNavigating()) {
                    locationProvider?.lastKnownLocation?.coordinates?.let { currentLoc ->
                        navFrag.getRoutes(
                            RouteStop("Current location", Location("").apply {
                                latitude = currentLoc.latitude
                                longitude = currentLoc.longitude
                            }),
                            RouteStop("${position.lat}, ${position.lng}", Location("").apply {
                                this.latitude = position.lat.toDouble()
                                this.longitude = position.lng.toDouble()
                            })
                        )
                        true
                    } ?: run {
                        Snackbar.make(
                            this@NavigationDemoActivity.findViewById(android.R.id.content),
                            "Unable to determine current location...",
                            Snackbar.LENGTH_LONG
                        ).also { snackbar ->
                            snackbar.setAction(resources.getString(R.string.dismiss_label)) { snackbar.dismiss() }
                            snackbar.show()
                        }
                        if (hasLocationPermission) {
                            locationProvider?.start()
                        } else {
                            requestLocationPermission(REQUEST_PERMISSION_LOCATION_MAP)
                        }
                    }
                }
            }
        })

        map.addLayer(selectedRouteLayer)
        map.addLayer(routeStopsLayer)
    }

    fun toggleFabButtons(show: Boolean) {
        fabContainer.visibility = if (show) VISIBLE else INVISIBLE
    }

    private fun moveCameraToFollowUser() {
        locationProvider?.lastKnownLocation?.let { loc ->
            vltMap?.camera?.update(
                target = Coordinate(
                    loc.coordinates.latitude.toFloat(),
                    loc.coordinates.longitude.toFloat()
                ),
                zoom = FOLLOW_USER_ZOOM,
                bearing = loc.bearingInDegrees,
                tilt = FOLLOW_USER_TILT,
                animated = true
            )
        }
    }

    private fun centerOnUserLocation() {
        locationProvider?.lastKnownLocation?.let { loc ->
            vltMap?.camera?.update(
                target = Coordinate(
                    loc.coordinates.latitude.toFloat(),
                    loc.coordinates.longitude.toFloat()
                ),
                zoom = DEFAULT_ZOOM,
                animated = true
            )
        }
        showMyLocation = true
    }

    private fun showLayersMenu(view: View) {
        PopupMenu(this, view, Gravity.END, 0, R.style.PopupMenuPosition).also { popup ->
            popup.setOnMenuItemClickListener { item ->
                when (item?.itemId) {
                    R.id.night -> {
                        item.isChecked = true
                        setMapMode(MapMode.VERIZON_NIGHT)
                    }
                    R.id.sat -> {
                        item.isChecked = true
                        setMapMode(MapMode.VERIZON_SAT)
                    }
                    R.id.day -> {
                        item.isChecked = true
                        setMapMode(MapMode.VERIZON_DAY)
                    }
                    R.id.flow -> {
                        item.isChecked = !item.isChecked
                        showTrafficFlow = item.isChecked
                    }
                    R.id.incidents -> {
                        item.isChecked = !item.isChecked
                        showTrafficIncidents = item.isChecked
                    }
                }
                true
            }
            popup.inflate(R.menu.popup_layer_menu)
            with (popup.menu) {
                when (vltMap?.mode) {
                    MapMode.VERIZON_SAT -> getItem(LayerMenuItem.SATELLITE_MODE.position).isChecked = true
                    MapMode.VERIZON_NIGHT -> getItem(LayerMenuItem.NIGHT_MODE.position).isChecked = true
                    else -> getItem(LayerMenuItem.DAY_MODE.position).isChecked = true
                }
                getItem(LayerMenuItem.TRAFFIC_FLOW.position).isChecked = showTrafficFlow
                getItem(LayerMenuItem.TRAFFIC_INCIDENTS.position).isChecked = showTrafficIncidents
                children.forEach { item ->
                    if (view.isEnabled) {
                        // Keep the popup menu open after a menu item is selected
                        item.setShowAsAction(SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW)
                        item.actionView = View(applicationContext)
                        item.setOnActionExpandListener(object : OnActionExpandListener {
                            override fun onMenuItemActionExpand(item: MenuItem?) = false
                            override fun onMenuItemActionCollapse(item: MenuItem?) = false
                        })
                    }
                }
            }
            popup.show()
        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    private fun requestLocationPermission(requestCode: Int) {
        ActivityCompat.requestPermissions(
            this,
            listOf(ACCESS_FINE_LOCATION).toTypedArray(),
            requestCode
        )
    }

    private val hasLocationPermission: Boolean get() = ContextCompat
        .checkSelfPermission(applicationContext, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    private fun fitMapToBounds(bounds: BoundingBox) {
        vltMap?.camera?.update(bounds, MAP_PADDING)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        results: IntArray
    ) {
        if (results.isNotEmpty() && results[0] == PackageManager.PERMISSION_GRANTED) {
            locationProvider?.start()
            when (requestCode) {
                REQUEST_PERMISSION_LOCATION_SEARCH -> showSearch()
                REQUEST_PERMISSION_LOCATION_CENTER_ON_USER -> centerOnUserLocation()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, results)
        }
    }

    private fun setMapMode(modeToSet: MapMode) {
        vltMap?.setMode(modeToSet, object : VltMap.SetMapModeCallback {
            override fun onSetModeComplete() {}

            override fun onSetModeFailed() {
                Snackbar.make(
                    this@NavigationDemoActivity.findViewById(android.R.id.content),
                    resources.getString(R.string.style_load_failed_label),
                    Snackbar.LENGTH_LONG
                ).also { snackbar ->
                    snackbar.setAction(resources.getString(R.string.dismiss_label)) { snackbar.dismiss() }
                    snackbar.show()
                }
            }
        })
    }

    fun adjustMapUIElements(topMargin: Int) {
        val lp = fabContainer.layoutParams as FrameLayout.LayoutParams
        lp.topMargin = Math.max(topMargin - mapContainer.top, 0)
        fabContainer.layoutParams = lp
    }

    private fun addRouteClickListeners() {
        selectedRouteLayer.addOnPolylineClickListener(
            object : ShapeLayer.OnPolylineClickListener {
                override fun onPolylineClick(polyline: Polyline) {
                    routeMap.keys.forEach {
                        if (routeMap[it]?.id == polyline.id) {
                            selectedRoute = it.also {
                                fitMapToBounds(it.boundingBox)
                            }
                        }
                    }
                }
            }
        )
    }

    private var selectedRoute: Route? = null
        set(value) {
            field?.let {
                stylizeDeselectedRoute(it)
            }
            field = value
            value?.let {
                stylizeSelectedRoute(it)
                navFrag.selectRoute(it)
            }
        }

    private fun stylizeDeselectedRoute(route: Route) {
        routeMap[route]?.apply {
            strokeWidth = DEFAULT_ROUTE_THICKNESS
            strokeColor = DEFAULT_ROUTE_COLOR
        }?.also {
            selectedRouteLayer.updateShape(it)
        }
    }

    private fun stylizeSelectedRoute(route: Route) {
        routeMap[route]?.apply {
            strokeWidth = SELECTED_ROUTE_THICKNESS
            strokeColor = SELECTED_ROUTE_COLOR
        }?.also {
            selectedRouteLayer.updateShape(it)
        }
    }

    private fun showSearch() {
        if (hasLocationPermission) {
            if (locationProvider?.lastKnownLocation == null) {
                Snackbar.make(
                    findViewById(android.R.id.content),
                    "Cannot initiate search without a current location...",
                    Snackbar.LENGTH_LONG
                ).also { snackbar ->
                    snackbar.setAction(resources.getString(R.string.dismiss_label)) { snackbar.dismiss() }
                    snackbar.show()
                }
                locationProvider?.start()
                return
            }

            supportFragmentManager.setFragmentResultListener(
                REQUEST_SEARCH_DESTINATION,
                this
            ) { requestKey, result ->
                if (requestKey == REQUEST_SEARCH_DESTINATION) {
                    val name = result.getString(SearchFragment.ARG_NAME) ?: ""
                    val lat = result.getDouble(SearchFragment.ARG_LATITUDE)
                    val lng = result.getDouble(SearchFragment.ARG_LONGITUDE)
                    navFrag.getRoutes(
                        RouteStop("Current location", Location("").apply {
                            locationProvider?.lastKnownLocation?.let { loc ->
                                this.latitude = loc.coordinates.latitude
                                this.longitude = loc.coordinates.longitude
                            }
                        }),
                        RouteStop(name, Location("").apply {
                            this.latitude = lat
                            this.longitude = lng
                        })
                    )
                }
            }
            supportFragmentManager
                .beginTransaction()
                .addToBackStack(null)
                .add(
                    R.id.search_container,
                    SearchFragment.newInstance(
                        REQUEST_SEARCH_DESTINATION,
                        Location("").apply {
                            locationProvider?.lastKnownLocation?.coordinates?.let { coords ->
                                this.latitude = coords.latitude
                                this.longitude = coords.longitude
                            }
                        })
                )
                .commit()
        } else {
            requestLocationPermission(REQUEST_PERMISSION_LOCATION_SEARCH)
        }
    }

    enum class LayerMenuItem(val position: Int) {
        DAY_MODE(1),
        NIGHT_MODE(2),
        SATELLITE_MODE(3),
        TRAFFIC_FLOW(5),
        TRAFFIC_INCIDENTS(6)
    }

    private companion object {
        val DEFAULT_ROUTE_COLOR = Color.parseColor("#963691A5")
        const val DEFAULT_ROUTE_THICKNESS = 6f
        val SELECTED_ROUTE_COLOR = Color.parseColor("#FF4B4B9C")
        const val SELECTED_ROUTE_THICKNESS = 5f
        const val REQUEST_PERMISSION_LOCATION_MAP = 14212
        const val REQUEST_PERMISSION_LOCATION_CENTER_ON_USER = 14213
        const val REQUEST_PERMISSION_LOCATION_SEARCH = 14214
        const val REQUEST_SEARCH_DESTINATION = "searchDestinationRequest"
        const val MAP_PADDING = 280
        const val DEFAULT_ZOOM = 11.0
        const val FOLLOW_USER_ZOOM = 14.5
        const val FOLLOW_USER_TILT = 30.0
    }
}