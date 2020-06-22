package com.verizon.location.platformdemo

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.verizon.location.commonmodels.Coordinate
import com.verizon.location.maps.*
import com.verizon.location.maps.Camera.CameraListener
import timber.log.Timber

class MapCameraDemo : AppCompatActivity() {

    private var mapIsVisible = false
    private var vltMap: VltMap? = null

    private var zoomField: TextView? = null
    private var bearingField:TextView? = null
    private var tiltField:TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_camera_demo)

        bearingField = findViewById<TextView>(R.id.bearing_value)
        zoomField = findViewById(R.id.zoom_value)
        tiltField = findViewById<TextView>(R.id.tilt_value)

        val vltMapOptions = VltMapOptions()
        vltMapOptions.target = Coordinate(42.3637f, -71.053604f)

        val mv = findViewById<MapView>(R.id.map_view)
        mv.onCreate(savedInstanceState)

        mv.initialize(
            resources.getString(R.string.map_key),
            vltMapOptions,
            object : OnMapReadyCallback {
                override fun onMapReady(map: VltMap) {
                    mapIsVisible = true
                    Timber.e("map is ready!")
                    vltMap = map
                    vltMap?.camera?.let {
                        updateFieldValues(it.zoom, it.bearing, it.tilt)
                        it.addCameraListener(object: CameraListener {
                            override fun onCameraUpdated(
                                target: Coordinate,
                                zoom: Double,
                                bearing: Double,
                                tilt: Double
                            ) { updateFieldValues(zoom, bearing, tilt) }
                        })
                    }
                }

                override fun onMapFailedToLoad(mapError: MapInitializationError) {
                    Timber.e("map failed to load %s", mapError.errorDescription)
                    Toast.makeText(this@MapCameraDemo, "map failed to load : $mapError.errorDescription", Toast.LENGTH_SHORT).show()
                }
            })
    }

    fun adjustCamera(view: View?) {
        if (mapIsVisible) {
            showMetricsDialog()
        }
    }

    private fun showMetricsDialog() {
        val metricsDialogBuilder = AlertDialog.Builder(this)
        val metricsDialogView =
            this.layoutInflater.inflate(R.layout.metrics_dialog, null)
        metricsDialogBuilder.setView(metricsDialogView)

        val updateButton =
            metricsDialogView.findViewById<Button>(R.id.update_button)

        val bearingText = metricsDialogView.findViewById<EditText>(R.id.bearing)
        val zoomText = metricsDialogView.findViewById<EditText>(R.id.zoom)
        val tiltText = metricsDialogView.findViewById<EditText>(R.id.tilt)
        vltMap?.camera?.let {
            bearingText.setText( getDoubleAsString(it.bearing) )
            zoomText.setText( getDoubleAsString(it.zoom) )
            tiltText.setText( getDoubleAsString(it.tilt) )
        }

        val metricsDialog = metricsDialogBuilder.create()

        metricsDialog.setTitle(resources.getString(R.string.map_camera_dialog_label));
        updateButton.setOnClickListener { v: View? ->
            val zoom = java.lang.Double.valueOf(zoomText.text.toString())
            val bearing = java.lang.Double.valueOf(bearingText.text.toString())
            val tilt = java.lang.Double.valueOf(tiltText.text.toString())
            try {
                vltMap?.camera?.let {
                    val currentPosition = it.target
                    it.update(currentPosition, zoom, bearing, tilt, false)
                }
                metricsDialog.dismiss()
            } catch (e: IllegalArgumentException) {
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
        }
        metricsDialog.setCancelable(false)
        metricsDialog.show()
    }

    private fun updateFieldValues(
        zoom: Double,
        bearing: Double,
        tilt: Double
    ) {
        zoomField?.text =
            resources.getString(R.string.camera_metric_zoom_label, getDoubleAsString(zoom))
        zoomField?.visibility = View.VISIBLE

        bearingField?.text =
            resources.getString(R.string.camera_metric_bearing_label, getDoubleAsString(bearing))
        bearingField?.visibility = View.VISIBLE

        tiltField?.text =
            resources.getString(R.string.camera_metric_tilt_label, getDoubleAsString(tilt))
        tiltField?.visibility = View.VISIBLE
    }

    private fun getDoubleAsString(value: Double): String? {
        return String.format("%.2f", value)
    }

    override fun onSupportNavigateUp(): Boolean {
        super.onBackPressed()
        finish()
        return true
    }

}
