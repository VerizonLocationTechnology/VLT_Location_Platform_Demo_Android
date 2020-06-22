package com.verizon.location.platformdemo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.verizon.location.commonmodels.Coordinate
import com.verizon.location.maps.*

class UserLocationDemo : AppCompatActivity() {

    private val options = VltMapOptions()
    private val REQUEST_CODE = 1487

    private var mapIsVisible = false
    private var vltMap: VltMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_location_demo)
        val mapView =
            findViewById<MapView>(R.id.map_view)

        options.zoom = 6.0
        options.target = Coordinate(42.3637f, -71.053604f)

        mapView.onCreate(savedInstanceState)
        mapView.attachMapReadyCallback(object : OnMapReadyCallback {
            override fun onMapReady(map: VltMap) {
                vltMap = map
                mapIsVisible = true
            }

            override fun onMapFailedToLoad(mapError: MapInitializationError) {
                Log.e("onCreate", "map failed to load $mapError.errorDescription")
                Toast.makeText(this@UserLocationDemo, "map failed to load : $mapError.errorDescription", Toast.LENGTH_SHORT).show()
            }
        })

        if (!mapIsVisible) {
            loadMap()
        }

        val fab =
            findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener { view: View? ->
            if (mapIsVisible) {
                showUserLocation()
            }
        }
    }

    private fun showUserLocation() {
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            showCurrentLocation()
        } else {
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this@UserLocationDemo,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_CODE
        )
    }

    private fun showCurrentLocation() {
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            vltMap?.setMyLocationEnabled(true)
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
                    Log.e("loadMap", "MAP ERROR : $mapError")
                    mapIsVisible = false
                    Toast.makeText(
                        this@UserLocationDemo,
                        "$mapError.errorDescription",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onMapReady(map: VltMap) {
                    vltMap = map
                    showUserLocation()
                }
            })
    }

    override fun onSupportNavigateUp(): Boolean {
        super.onBackPressed()
        finish()
        return true
    }

}