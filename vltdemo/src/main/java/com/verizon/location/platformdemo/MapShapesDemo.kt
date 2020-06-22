package com.verizon.location.platformdemo

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.verizon.location.commonmodels.Coordinate
import com.verizon.location.maps.*
import com.verizon.location.maps.model.*
import timber.log.Timber

class MapShapesDemo : AppCompatActivity() {

    private var mapIsVisible = false
    private val options = VltMapOptions()
    private var vltMap: VltMap? = null
    private var markers = mutableListOf<Marker>()
    private var circles = mutableListOf<Circle>()
    private var polygons = mutableListOf<Polygon>()
    private var polylines = mutableListOf<Polyline>()

    private var shapeOptions: TextView? = null
    private val state = intArrayOf(0, 0, 0, 0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_shapes_demo)

        options.target = Coordinate(42.3637f, -71.053604f)
        options.zoom = 10.5

        shapeOptions = findViewById(R.id.shape_options)
        initializeShapes()

        (findViewById<View>(R.id.map_view) as MapView).onCreate(savedInstanceState)

        (findViewById<View>(R.id.map_view) as MapView).attachMapReadyCallback(
            object : OnMapReadyCallback {
                override fun onMapReady(map: VltMap) {
                    Timber.e("onMapReady: %s", map)
                    vltMap = map
                    mapIsVisible = true
                    initializeShapes()
                }

                override fun onMapFailedToLoad(mapError: MapInitializationError) {
                    Timber.e("onMapFailedToLoad error %s", mapError.errorDescription)
                    Toast.makeText(this@MapShapesDemo, "map failed to load : $mapError.errorDescription", Toast.LENGTH_SHORT).show()
                }
            })
        if (!mapIsVisible) {
            loadMap()
        }
    }


    fun shapeListDialog(view: View?) {
        if (mapIsVisible) {
            val shapesDialogBuilder =
                AlertDialog.Builder(this)
            val shapesDialogView =
                this.layoutInflater.inflate(R.layout.shapes_dialog, null)
            shapesDialogBuilder.setView(shapesDialogView)
            val polylineCheckBox = shapesDialogView.findViewById<CheckBox>(R.id.polyline)
            polylineCheckBox.isChecked = state[0] == 1
            val polygonCheckBox = shapesDialogView.findViewById<CheckBox>(R.id.polygon)
            polygonCheckBox.isChecked = state[1] == 1
            val circleCheckBox = shapesDialogView.findViewById<CheckBox>(R.id.circle)
            circleCheckBox.isChecked = state[2] == 1
            val markerCheckBox = shapesDialogView.findViewById<CheckBox>(R.id.marker)
            markerCheckBox.isChecked = state[3] == 1
            val metricsDialog = shapesDialogBuilder.create()
            metricsDialog.setTitle(resources.getString(R.string.map_shapes_dialog_label))
            polylineCheckBox.setOnCheckedChangeListener { compoundButton, b ->
                showPolyline(b)
            }
            polygonCheckBox.setOnCheckedChangeListener { compoundButton, b ->
                showPolygon(b)
            }
            circleCheckBox.setOnCheckedChangeListener { compoundButton, b ->
                showCircles(b)
            }
            markerCheckBox.setOnCheckedChangeListener { compoundButton, b -> showMarkers(b) }
            metricsDialog.setButton(
                DialogInterface.BUTTON_POSITIVE, resources.getString(R.string.dismiss_label)
            ) { dialogInterface, buttonId -> metricsDialog.dismiss() }
            metricsDialog.show()
        }
    }

    private fun loadMap() {
        mapIsVisible = true
        val mv =
            findViewById<MapView>(R.id.map_view)
        mv.initialize(
            resources.getString(R.string.map_key),
            options,
            object : OnMapReadyCallback {
                override fun onMapFailedToLoad(mapError: MapInitializationError) {
                    Timber.e("onMapFailedToLoad error %s", mapError.errorDescription)
                    mapIsVisible = false
                    Toast.makeText(
                        this@MapShapesDemo,
                        "$mapError.errorDescription",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onMapReady(map: VltMap) {
                    vltMap = map
                    Timber.e("onMapReady: %s", map)
                }
            })
    }

    private fun showPolyline(show: Boolean) {
        if (show) {
            polylines.forEach {
                vltMap?.add(it)
            }
            state[0] = 1
            findViewById<View>(R.id.polyline).visibility = View.VISIBLE
        } else {
            polylines.forEach {
                vltMap?.remove(it)
            }
            state[0] = 0
            findViewById<View>(R.id.polyline).visibility = View.GONE
        }
        updateShapeOptionsPrompt()
    }

    private fun showPolygon(show: Boolean) {
        if (show) {
            polygons.forEach {
                vltMap?.add(it)
            }
            state[1] = 1
            findViewById<View>(R.id.polygon).visibility = View.VISIBLE
        } else {
            polygons.forEach {
                vltMap?.remove(it)
            }
            state[1] = 0
            findViewById<View>(R.id.polygon).visibility = View.GONE
        }
        updateShapeOptionsPrompt()
    }

    private fun showCircles(show: Boolean) {
        if (show) {
            circles.forEach {
                vltMap?.add(it)
            }
            state[2] = 1
            findViewById<View>(R.id.circle).visibility = View.VISIBLE
        } else {
            circles.forEach {
                vltMap?.remove(it)
            }
            state[2] = 0
            findViewById<View>(R.id.circle).visibility = View.GONE
        }
        updateShapeOptionsPrompt()
    }

    private fun showMarkers(show: Boolean) {
        if (show) {
            markers.forEach {
                vltMap?.add(it)
            }
            state[3] = 1
            findViewById<View>(R.id.marker).visibility = View.VISIBLE
        } else {
            markers.forEach {
                vltMap?.remove(it)
            }
            state[3] = 0
            findViewById<View>(R.id.marker).visibility = View.GONE
        }
        updateShapeOptionsPrompt()
    }

    private fun initializeShapes() {
        initializeMarkers()
        initializeCircles()
        initializePolygon()
        initializePolyline()
    }

    private fun initializePolyline() {
        val polylinePoints = mutableListOf<Coordinate>()
        polylinePoints.add(Coordinate(42.3637f, -71.053604f))
        polylinePoints.add(Coordinate(42.362061f, -71.05491f))
        polylinePoints.add(Coordinate(42.362554f, -71.055778f))
        polylinePoints.add(Coordinate(42.361543f, -71.057602f))
        polylinePoints.add(Coordinate(42.35887f, -71.056763f))
        polylinePoints.add(Coordinate(42.358851f, -71.05748f))
        val polyline =
            Polyline(
                polylinePoints,
                getColorWithAlpha(Color.parseColor("#0077B4"), 0.8f),
                4f
            )
        polylines.add(polyline)
    }

    private fun initializePolygon() {
        val polygonPoints = mutableListOf<Coordinate>()
        polygonPoints.add(Coordinate(42.363808f, -71.053601f))
        polygonPoints.add(Coordinate(42.363667f, -71.053667f))
        polygonPoints.add(Coordinate(42.363688f, -71.053727f))
        polygonPoints.add(Coordinate(42.363716f, -71.053717f))
        polygonPoints.add(Coordinate(42.363750f, -71.053765f))
        polygonPoints.add(Coordinate(42.363788f, -71.053742f))
        val polygon =
            Polygon(
                polygonPoints,
                Color.parseColor("#ED7000")
            )
        polygon.showCallout = true
        polygons.add(polygon)
    }

    private fun initializeCircles() {
        val circle1 = Circle(
            Coordinate(42.3600f, -71.06137f),
            150f,
            Color.parseColor("#008330")
        )
        circle1.showCallout = true
        circles.add(circle1)
        val circle2 = Circle(
            Coordinate(42.358744f, -71.060403f),
            150f,
            getColorWithAlpha(Color.parseColor("#ED7000"), 0.80f)
        )
        circle2.showCallout = true
        circles.add(circle2)
    }

    private fun initializeMarkers() {
        val marker1 =
            Marker(
                Coordinate(42.3637f, -71.053604f), null
            )
        marker1.showCallout = true;
        markers.add(marker1)
        val marker2 =
            Marker(
                Coordinate(42.358851f, -71.05748f), null
            )
        marker2.showCallout = true;
        markers.add(marker2)
        val marker3 =
            Marker(
                Coordinate(42.376331f, -71.060757f), null
            )
        marker3.showCallout = true;
        markers.add(marker3)
        markers.add(Marker(Coordinate(42.35611f, -71.065944f),
            MarkerImage.createFromResource(this, R.drawable.star_marker, "pink-star")
        ))
        markers.add(Marker(Coordinate(42.374106f, -71.05537f),
            MarkerImage.createFromResource(this, R.drawable.custom_marker, "blue-flame")
        ))
    }

    private fun updateShapeOptionsPrompt() {
        if (state[0] == 0 && state[1] == 0 && state[2] == 0 && state[3] == 0) {
            findViewById<View>(R.id.shape_options).visibility = View.VISIBLE
        } else {
            findViewById<View>(R.id.shape_options).visibility = View.GONE
        }
    }

    private fun getColorWithAlpha(color: Int, ratio: Float): Int {
        val newColor: Int
        val alpha = Math.round(Color.alpha(color) * ratio)
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)
        newColor = Color.argb(alpha, r, g, b)
        return newColor
    }

    override fun onSupportNavigateUp(): Boolean {
        super.onBackPressed()
        finish()
        return true
    }
}