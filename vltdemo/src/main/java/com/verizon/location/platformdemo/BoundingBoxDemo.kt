package com.verizon.location.platformdemo

import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import com.verizon.location.commonmodels.BoundingBox
import com.verizon.location.commonmodels.Coordinate
import com.verizon.location.maps.*
import timber.log.Timber

class BoundingBoxDemo : AppCompatActivity(),
    PopupMenu.OnMenuItemClickListener {

    private lateinit var mapView: MapView
    private var vltMap: VltMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bounding_box_demo)

        val button = findViewById<Button>(R.id.btn)
        button.setOnClickListener { view ->
            view?.let {
                val popup = PopupMenu(
                    this@BoundingBoxDemo,
                    it,
                    Gravity.END,
                    0,
                    R.style.PopupMenuPosition
                )
                popup.setOnMenuItemClickListener(this@BoundingBoxDemo)
                popup.inflate(R.menu.popup_menu)
                popup.show()
            }
        }

//        val options = VltMapOptions()
//         options.mode = MapMode.VERIZON_NIGHT

        mapView = findViewById(R.id.map_view)
        mapView.onCreate(savedInstanceState)
        mapView.initialize(
            resources.getString(R.string.map_key),
            VltMapOptions(),
            object : OnMapReadyCallback {
                override fun onMapReady(map: VltMap) {
                    vltMap = map
                }

                override fun onMapFailedToLoad(mapError: MapInitializationError) {
                    Timber.e("map failed to load ${mapError.errorDescription}")
                }
            }
        )
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        item?.itemId.let {
            return when (it) {
                R.id.co -> {
                    moveToColoradoBounds()
                    true
                }
                R.id.tx -> {
                    moveToTexasBounds()
                    true
                }
                R.id.ny -> {
                    moveToNewYorkBounds()
                    true
                }
                R.id.ut -> {
                    moveToUtahBounds()
                    true
                }
                else -> false
            }
        }
    }

    private fun moveToUtahBounds() {
        val bbUtah = BoundingBox(
            Coordinate(42.40689f, -109.01411f),
            Coordinate(37.1935f, -114.141815f)
        )
        moveToBoundingBox(bbUtah)
    }

    private fun moveToColoradoBounds() {
        val bbColorado = BoundingBox(
            Coordinate(36.99403f, -109.336205f),
            Coordinate(41.4281f, -101.6619f)
        )
        moveToBoundingBox(bbColorado)
    }

    private fun moveToTexasBounds() {
        val bbTexas = BoundingBox(
            Coordinate(36.5584f, -95.07161f),
            Coordinate(28.5479f, -104.982185f)
        )
        moveToBoundingBox(bbTexas)
    }

    private fun moveToNewYorkBounds() {
        val bbNewYork = BoundingBox(
            Coordinate(40.2307f, -74.5358f),
            Coordinate(41.2496f, -72.60234f)
        )
        moveToBoundingBox(bbNewYork)
    }

    private fun moveToBoundingBox(boundingBox: BoundingBox) {
        vltMap?.camera?.update(boundingBox)
    }

    override fun onSupportNavigateUp(): Boolean {
        super.onBackPressed()
        finish()
        return true
    }
}