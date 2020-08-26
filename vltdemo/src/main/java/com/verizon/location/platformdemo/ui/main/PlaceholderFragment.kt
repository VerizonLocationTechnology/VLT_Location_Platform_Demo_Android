package com.verizon.location.platformdemo.ui.main

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.verizon.location.commonmodels.Coordinate
import com.verizon.location.maps.*
import timber.log.Timber
import com.verizon.location.platformdemo.R


/**
 * A placeholder fragment containing a simple view.
 */
class PlaceholderFragment : Fragment(), Camera.CameraListener {

    private lateinit var pageViewModel: PageViewModel
    private var mapMode: MapMode? = null
    private var mapOptions: VltMapOptions? = null
    private var vltMap: VltMap? = null

    var viewRoot: View? = null
    var viewGroupContainer: ViewGroup? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel::class.java).apply {
            setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 1)
            setMapMode(arguments?.getSerializable(ARG_MAP_MODE) as MapMode?)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_map_mode_demo, container, false)
        val textView: TextView = root.findViewById(R.id.section_label)

        pageViewModel.text.observe(viewLifecycleOwner, Observer<String> {
            textView.text = it
        })
        textView.setTextColor(Color.BLACK)

        val mapView = root.findViewById<View>(R.id.map_view) as MapView
        mapView.onCreate(savedInstanceState)
        mapView.attachMapReadyCallback(object : OnMapReadyCallback {
            override fun onMapFailedToLoad(mapError: MapInitializationError) {

            }

            override fun onMapReady(map: VltMap) {

            }
        })



        viewRoot = root
        viewGroupContainer = container
        return root
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"
        private const val ARG_MAP_MODE = "map_mode"
        private const val ARG_MAP_OPTIONS = "map_options"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(sectionNumber: Int): PlaceholderFragment {
            return PlaceholderFragment().apply {
                arguments = Bundle().apply {
                    when (sectionNumber) {
                        1 -> putSerializable(ARG_MAP_MODE, MapMode.VERIZON_DAY)
                        2 -> putSerializable(ARG_MAP_MODE, MapMode.VERIZON_NIGHT)
                        3 -> putSerializable(ARG_MAP_MODE, MapMode.VERIZON_SAT)
                    }
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadMap()
    }

    private fun loadMap() {
        pageViewModel.getOptions()?.let {
            mapOptions = it.value
        }
        pageViewModel.getMode()?.let {
            mapMode = it.value
        }
        mapMode?.let {
            mapOptions?.mode = it
        }
        viewRoot?.apply {
            val mv: MapView = this.findViewById(R.id.map_view)
            mv.initialize(
                resources.getString(R.string.map_key),
                mapOptions ?: VltMapOptions().apply {
                    this.zoom = 12.0
                    this.target = Coordinate(42.3637f, -71.053604f)
                    this.mode = mapMode ?: MapMode.VERIZON_DAY
                },
                object : OnMapReadyCallback {
                    override fun onMapFailedToLoad(mapError: MapInitializationError) {
                        Timber.e("onMapFailedToLoad: error %s", mapError)
                        Toast.makeText(activity, "" + mapError.errorDescription, Toast.LENGTH_SHORT).show()
                    }

                    override fun onMapReady(map: VltMap) {
                        Timber.e("onMapReady: %s", map)
                        map.camera.addCameraListener(this@PlaceholderFragment)
                        vltMap = map
                    }
                })
        }
    }

    override fun onCameraUpdated(target: Coordinate, zoom: Double, bearing: Double, tilt: Double) {
        val mapOptions = VltMapOptions().apply {
            this.target = target
            this.zoom = zoom
            this.bearing = bearing
            this.tilt = tilt
        }
        this@PlaceholderFragment.pageViewModel.setOptions(mapOptions)
    }
}