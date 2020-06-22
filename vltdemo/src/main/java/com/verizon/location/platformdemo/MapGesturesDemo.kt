package com.verizon.location.platformdemo

import android.graphics.Point
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.verizon.location.commonmodels.Coordinate
import com.verizon.location.maps.MapGestures
import com.verizon.location.maps.MapInitializationError
import com.verizon.location.maps.MapView
import com.verizon.location.maps.OnMapReadyCallback
import com.verizon.location.maps.VltMap
import com.verizon.location.maps.VltMapOptions
import timber.log.Timber

class MapGesturesDemo : AppCompatActivity() {

    private var vltMapOptions = VltMapOptions()
    private lateinit var mapRotatingToast: Toast

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_gestures)
        mapRotatingToast = Toast.makeText(this, "Map rotating...", Toast.LENGTH_SHORT)

        val mapView = findViewById<MapView>(R.id.map_view)
        mapView.onCreate(savedInstanceState)

        mapView.initialize(
            resources.getString(R.string.map_key),
            vltMapOptions,
            object : OnMapReadyCallback {
                override fun onMapReady(map: VltMap) {

                    map.gestures.addOnMapClickListener(object: MapGestures.OnMapClickListener {
                        override fun onMapClick(position: Coordinate, point: Point) {
                            showSnackbar("click at $point / $position")
                        }
                    })

                    map.gestures.addOnMapLongClickListener(object: MapGestures.OnMapLongClickListener {
                            override fun onMapLongClick(position: Coordinate) {
                                showSnackbar("long click at $position")
                            }
                        })

                    map.gestures.addOnMapRotateListener(object: MapGestures.OnMapRotateListener {
                        override fun onMapRotateBegin() {
                            showSnackbar("Map rotation starting.")
                        }

                        override fun onMapRotate(bearing: Double) {
                            mapRotatingToast.show()
                        }

                        override fun onMapRotateEnd(bearing: Double) {
                            showSnackbar("Map rotation complete.")
                            mapRotatingToast.cancel()
                        }
                    })

                    map.gestures.addOnMapFlingListener(object: MapGestures.OnMapFlingListener {
                        override fun onMapFling() {
                            showSnackbar("Map fling")
                        }
                    })
                }

                override fun onMapFailedToLoad(mapError: MapInitializationError) {
                    Timber.e("map failed to load %s", mapError.errorDescription)
                    Toast.makeText(
                        this@MapGesturesDemo,
                        "map failed to load : $mapError.errorDescription",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        super.onBackPressed()
        finish()
        return true
    }

    private fun showSnackbar(text: String) {
        Snackbar.make(findViewById(android.R.id.content), text, Snackbar.LENGTH_LONG).also {
            it.setAction(resources.getString(R.string.dismiss_label)) { view: View? -> it.dismiss() }
            it.show()
        }
    }
}