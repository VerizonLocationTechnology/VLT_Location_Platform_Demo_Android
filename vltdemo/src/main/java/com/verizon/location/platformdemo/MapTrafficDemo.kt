package com.verizon.location.platformdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.CheckBox
import android.widget.Toast
import com.verizon.location.commonmodels.Coordinate
import com.verizon.location.maps.*
import timber.log.Timber

class MapTrafficDemo : AppCompatActivity() {

    private var vltMap: VltMap? = null
    private var vltMapOptions = VltMapOptions()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_traffic)

        findViewById<CheckBox>(R.id.feature_switch)?.let {
            it.setOnCheckedChangeListener { compoundButton, b ->
                vltMap?.apply {
                    if (it.isChecked) {
                        this.setFeatureVisible(MapFeature.TRAFFIC, true)
                        it.text = resources.getString(R.string.traffic_on_label)
                    } else {
                        this.setFeatureVisible(MapFeature.TRAFFIC, false)
                        it.text = resources.getString(R.string.traffic_off_label)
                    }
                }
            }
        }

        val mv = findViewById<MapView>(R.id.map_view)
        mv.onCreate(savedInstanceState)

        vltMapOptions.target = Coordinate(42.3637f, -71.053604f)
        vltMapOptions.zoom = 12.0

        mv.initialize(
            resources.getString(R.string.map_key),
            vltMapOptions,
            object : OnMapReadyCallback {
                override fun onMapReady(map: VltMap) {
                    vltMap = map
                }

                override fun onMapFailedToLoad(mapError: MapInitializationError) {
                    Timber.e("map failed to load %s", mapError.errorDescription)
                    Toast.makeText(this@MapTrafficDemo, "map failed to load : $mapError.errorDescription", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onSupportNavigateUp(): Boolean {
        super.onBackPressed()
        finish()
        return true
    }

}