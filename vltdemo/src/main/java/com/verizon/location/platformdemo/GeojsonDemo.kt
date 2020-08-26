package com.verizon.location.platformdemo

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.verizon.location.commonmodels.Coordinate
import com.verizon.location.maps.*
import com.verizon.location.maps.model.layer.GeoJsonLayer
import com.verizon.location.maps.model.layer.GeoJsonStyle
import org.json.JSONObject
import timber.log.Timber
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class GeojsonDemo : AppCompatActivity() {

    private var mapIsVisible = false
    private val options = VltMapOptions()
    private var vltMap: VltMap? = null

    private val state = intArrayOf(0, 0, 0, 0, 0, 0)

    private var polygonLayer: GeoJsonLayer? = null
    private var multiPolygonLayer: GeoJsonLayer? = null
    private var pointLayer: GeoJsonLayer? = null
    private var lineStringLayer: GeoJsonLayer? = null
    private var multiLineStringLayer: GeoJsonLayer? = null
    private var multiPointLayer: GeoJsonLayer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_geojson_demo)

        options.target = Coordinate(42.3637f, -71.060604f)
        options.zoom = 10.0

        (findViewById<View>(R.id.map_view) as MapView).onCreate(savedInstanceState)

        (findViewById<View>(R.id.map_view) as MapView).attachMapReadyCallback(
            object : OnMapReadyCallback {
                override fun onMapReady(map: VltMap) {
                    Timber.e("onMapReady: %s", map)
                    vltMap = map
                    mapIsVisible = true
                    initializeImages()
                }

                override fun onMapFailedToLoad(mapError: MapInitializationError) {
                    Timber.e("onMapFailedToLoad error %s", mapError.errorDescription)
                    Toast.makeText(this@GeojsonDemo, "map failed to load : $mapError.errorDescription", Toast.LENGTH_SHORT).show()
                }
            })
        if (!mapIsVisible) {
            loadMap()
        }
    }


    fun geojsonOptionsDialog(view: android.view.View) {
        if (mapIsVisible) {
            val shapesDialogBuilder =
                AlertDialog.Builder(this)
            val shapesDialogView =
                layoutInflater.inflate(R.layout.geojson_dialog, null)
            shapesDialogBuilder.setView(shapesDialogView)

            val multiPolygonCheckBox = shapesDialogView.findViewById<CheckBox>(R.id.multi_polygon_cb)
            multiPolygonCheckBox.isChecked = state[0] == 1
            multiPolygonCheckBox.setOnCheckedChangeListener { compoundButton, b ->
                showMultiPolygon(b)
            }

            val polygonCheckBox = shapesDialogView.findViewById<CheckBox>(R.id.polygon_cb)
            polygonCheckBox.isChecked = state[1] == 1
            polygonCheckBox.setOnCheckedChangeListener { compoundButton, b ->
                showPolygon(b)
            }

            val pointCheckBox = shapesDialogView.findViewById<CheckBox>(R.id.point_cb)
            pointCheckBox.isChecked = state[2] == 1
            pointCheckBox.setOnCheckedChangeListener { compoundButton, b ->
                showPoints(b)
            }

            val linestringCheckBox = shapesDialogView.findViewById<CheckBox>(R.id.linestring_cb)
            linestringCheckBox.isChecked = state[3] == 1
            linestringCheckBox.setOnCheckedChangeListener { compoundButton, b ->
                showLineString(b)
            }

            val multiPointCheckBox = shapesDialogView.findViewById<CheckBox>(R.id.multi_point_cb)
            multiPointCheckBox.isChecked = state[4] == 1
            multiPointCheckBox.setOnCheckedChangeListener { compoundButton, b ->
                showMultiPoints(b)
            }

            val multiLinestringCheckBox = shapesDialogView.findViewById<CheckBox>(R.id.multi_linestring_cb)
            multiLinestringCheckBox.isChecked = state[5] == 1
            multiLinestringCheckBox.setOnCheckedChangeListener { compoundButton, b ->
                showMultiLineString(b)
            }

            val metricsDialog = shapesDialogBuilder.create()
            metricsDialog.setTitle(resources.getString(R.string.geojson_dialog_label))
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
                        this@GeojsonDemo,
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

    private fun showMultiPolygon(show: Boolean) {
        if (show) {
            loadFeature(R.raw.multi_polygon)
            state[0] = 1
            findViewById<View>(R.id.multi_polygon).visibility = View.VISIBLE
        } else {
            vltMap?.layers?.let {
                multiPolygonLayer?.let {
                    vltMap?.removeLayer(it.id)
                    multiPolygonLayer = null
                }
            }
            state[0] = 0
            findViewById<View>(R.id.multi_polygon).visibility = View.GONE
        }
        updateShapeOptionsPrompt()
    }

    private fun showPolygon(show: Boolean) {
        if (show) {
            loadFeature(R.raw.polygon)
            state[1] = 1
            findViewById<View>(R.id.polygon).visibility = View.VISIBLE
        } else {
            vltMap?.layers?.let {
                polygonLayer?.let {
                    vltMap?.removeLayer(it.id)
                    polygonLayer = null
                }
            }
            state[1] = 0
            findViewById<View>(R.id.polygon).visibility = View.GONE
        }
        updateShapeOptionsPrompt()
    }

    private fun showPoints(show: Boolean) {
        if (show) {
            loadFeature(R.raw.point)
            state[2] = 1
            findViewById<View>(R.id.point).visibility = View.VISIBLE
        } else {
            vltMap?.layers?.let {
                pointLayer?.let {
                    vltMap?.removeLayer(it.id)
                }
            }
            pointLayer = null
            state[2] = 0
            findViewById<View>(R.id.point).visibility = View.GONE
        }
        updateShapeOptionsPrompt()
    }

    private fun showLineString(show: Boolean) {
        if (show) {
            loadFeature(R.raw.linestring)
            state[3] = 1
            findViewById<View>(R.id.linestring).visibility = View.VISIBLE
        } else {
            vltMap?.layers?.let {
                lineStringLayer?.let {
                    vltMap?.removeLayer(it.id)
                    lineStringLayer = null
                }
            }
            state[3] = 0
            findViewById<View>(R.id.linestring).visibility = View.GONE
        }
        updateShapeOptionsPrompt()
    }

    private fun showMultiPoints(show: Boolean) {
        if (show) {
            loadFeature(R.raw.multi_point)
            state[4] = 1
            findViewById<View>(R.id.multi_point).visibility = View.VISIBLE
        } else {
            vltMap?.layers?.let {
                multiPointLayer?.let {
                    vltMap?.removeLayer(it.id)
                    multiPointLayer = null
                }
            }
            state[4] = 0
            findViewById<View>(R.id.multi_point).visibility = View.GONE
        }
        updateShapeOptionsPrompt()
    }

    private fun showMultiLineString(show: Boolean) {
        if (show) {
            loadFeature(R.raw.multi_linestring)
            state[5] = 1
            findViewById<View>(R.id.multi_linestring).visibility = View.VISIBLE
        } else {
            vltMap?.layers?.let {
                multiLineStringLayer?.let {
                    vltMap?.removeLayer(it.id)
                    multiLineStringLayer = null
                }
            }
            state[5] = 0
            findViewById<View>(R.id.multi_linestring).visibility = View.GONE
        }
        updateShapeOptionsPrompt()
    }

    private fun loadFeature(fileResource: Int) {
        val inputStream = resources.openRawResource(fileResource)
        inputStream.let {
            try {
                val inputStreamReader = InputStreamReader(inputStream)
                val reader = BufferedReader(inputStreamReader)
                reader.use {
                    val fileString = it.readText()
                    val data = JSONObject(fileString)
                    parseFeature(data)
                }
            } catch (ioe: IOException) {
                ioe.printStackTrace()
                Log.e("", "")
            }
        }
    }

    private fun parseFeature(featureCollection: JSONObject) {
        val features = featureCollection.getJSONArray("features")
        val featuredType = features.getJSONObject(0)
        featuredType.getJSONObject("geometry").apply {
            val type = this.get("type")
            when (type) {
                "MultiPolygon" -> {
                    addMultiPolygonFeature(featureCollection)
                }
                "Polygon" -> {
                    addPolygonFeature(featureCollection)
                }
                "Point" -> {
                    addPointFeature(featureCollection)
                }
                "LineString" -> {
                    addLineFeature(featureCollection)
                }
                "MultiLineString" -> {
                    addMultiLineFeature(featureCollection)
                }
                "MultiPoint" -> {
                    addMultiPointFeature(featureCollection)
                }
                else -> {
                    Timber.i("unsupported type : $type");
                }
            }
        }

    }

    private fun initializeImages() {
        vltMap?.addImage(this@GeojsonDemo, "default", R.drawable.default_marker_icon)
        vltMap?.addImage(this@GeojsonDemo, "blueflame", R.drawable.custom_marker)
        vltMap?.addImage(this@GeojsonDemo, "pinkstar", R.drawable.star_marker)
    }

    private fun updateShapeOptionsPrompt() {
        if (state[0] == 0 && state[1] == 0 && state[2] == 0 && state[3] == 0 && state[4] == 0 && state[5] == 0) {
            findViewById<View>(R.id.geojson_options).visibility = View.VISIBLE
        } else {
            findViewById<View>(R.id.geojson_options).visibility = View.GONE
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

    private fun addLineFeature(lineFeatureCollection: JSONObject) {
        vltMap?.layers?.let { _ ->
            lineStringLayer = GeoJsonLayer("LineString")
            lineStringLayer?.let {
                vltMap?.addLayer(it)
                it.setGeoJson(lineFeatureCollection)
                it.defaultStyle = GeoJsonStyle().also { style ->
                    style.lineStringColor = Color.parseColor("#7851A9")
                    style.lineStringWidth = 4f
                }
            }
        }
    }

    private fun addPointFeature(pointFeatureCollection: JSONObject) {
        vltMap?.addImage(this@GeojsonDemo, "default", R.drawable.default_marker_icon)
        vltMap?.layers?.let { _ ->
            pointLayer = GeoJsonLayer("Point")
            pointLayer?.let {
                vltMap?.addLayer(it)
                it.setGeoJson(pointFeatureCollection)
                it.defaultStyle = GeoJsonStyle().also { style ->
                    style.pointIconImage = "default"
                }
            }
        }

    }

    private fun addMultiLineFeature(multiLineFeatureCollection: JSONObject) {
        vltMap?.layers?.let { _ ->
            multiLineStringLayer = GeoJsonLayer("MultiLineString")
            multiLineStringLayer?.let {
                vltMap?.addLayer(it)
                it.setGeoJson(multiLineFeatureCollection)
                it.defaultStyle = GeoJsonStyle().also { style ->
                    style.lineStringWidth = 4f
                    style.lineStringColor = Color.parseColor("#0028C7")
                }
            }
        }
    }

    private fun addMultiPolygonFeature(multiPolygonFeatureCollection: JSONObject) {
        vltMap?.layers?.let { _ ->
            multiPolygonLayer = GeoJsonLayer("MultiPolygon")
            multiPolygonLayer?.let {
                vltMap?.addLayer(it)
                it.setGeoJson(multiPolygonFeatureCollection)
                it.defaultStyle = GeoJsonStyle().also { style ->
                    style.polygonFillColor = getColorWithAlpha(Color.parseColor("#ED7000"), 0.8f)
                }
            }
        }
    }

    private fun addMultiPointFeature(multiPointFeatureCollection: JSONObject) {
        vltMap?.addImage(this@GeojsonDemo, "pinkstar", R.drawable.star_marker)
        vltMap?.layers?.let { _ ->
            multiPointLayer = GeoJsonLayer("MultiPoint")
            multiPointLayer?.let {
                vltMap?.addLayer(it)
                it.setGeoJson(multiPointFeatureCollection)
                it.defaultStyle = GeoJsonStyle().also { style ->
                    style.pointIconImage = "pinkstar"
                }
            }
        }
    }

    private fun addPolygonFeature(polygonFeatureCollection: JSONObject) {
        vltMap?.layers?.let { _ ->
            polygonLayer = GeoJsonLayer("Polygon")
            polygonLayer?.let {
                vltMap?.addLayer(it)
                it.setGeoJson(polygonFeatureCollection)
                it.defaultStyle = GeoJsonStyle().also { style ->
                    style.polygonFillColor =
                        Color.parseColor("#0077B4")
                }
            }
        }
    }

}