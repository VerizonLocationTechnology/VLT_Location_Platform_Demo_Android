package com.verizon.location.platformdemo

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.verizon.location.commonmodels.Coordinate
import com.verizon.location.maps.*
import com.verizon.location.maps.model.layer.GeoJsonLayer
import com.verizon.location.maps.model.layer.GeoJsonStyle
import com.verizon.location.maps.model.layer.Layer
import com.verizon.location.platformdemo.util.JsonFileUtil
import java.util.Collections

class LayersDemoActivity : AppCompatActivity(), OnMapReadyCallback, LayersUpdateListener {

    private lateinit var mapView: MapView
    private lateinit var editButton: Button
    private var map: VltMap? = null

    private var vltMapOptions = VltMapOptions().apply {
        this.target = Coordinate(42.3637f, -71.060604f)
        this.zoom = 10.0
    }

    private val layerA = GeoJsonLayer("A")
    private val layerB = GeoJsonLayer("B")
    private val layerC = GeoJsonLayer("C")

    private val styleA = GeoJsonStyle().apply {
        this.lineStringWidth = 2f
        this.lineStringColor = Color.parseColor("#7e0170")
        this.polygonFillColor = Color.parseColor("#ff00f7")
        this.polygonFillOpacity = 0.75f
    }

    private val styleB = GeoJsonStyle().apply {
        this.lineStringWidth = 2f
        this.lineStringColor = Color.parseColor("#8a7300")
        this.polygonFillColor = Color.parseColor("#ffdd00")
        this.polygonFillOpacity = 0.75f
    }

    private val styleC = GeoJsonStyle().apply {
        this.lineStringWidth = 2f
        this.lineStringColor = Color.parseColor("#3e8901")
        this.polygonFillColor = Color.parseColor("#51ff00")
        this.polygonFillOpacity = 0.75f
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_layers)
        editButton = findViewById(R.id.edit_layers_button)

        mapView = findViewById(R.id.map_view)
        mapView.onCreate(savedInstanceState)
        mapView.initialize(resources.getString(R.string.map_key), vltMapOptions, this)
    }

    override fun onMapReady(map: VltMap) {
        this.map = map

        initGeoJsonData()

        addLayer("A")
        addLayer("B")
        addLayer("C")

        editButton.setOnClickListener {
            val editLayersFrag = ReorderLayersDialogFragment.newInstance(map.layers)
            editLayersFrag.show(supportFragmentManager, "edit_layers_frag")
        }
    }

    override fun onMapFailedToLoad(error: MapInitializationError) {
        println(error.errorDescription)
    }

    override fun onLayersUpdated(newLayerOrder: List<String>) {
        map?.let { m ->
            newLayerOrder.forEach { id -> m.removeLayer(id) }
            newLayerOrder.forEach { id ->
                addLayer(id)
            }
        }
    }

    private fun addLayer(id: String) {
        map?.let {
            if (id == "A") {
                it.addLayer(layerA)
                layerA.defaultStyle = styleA
            } else if (id == "B") {
                it.addLayer(layerB)
                layerB.defaultStyle = styleB
            } else if (id == "C") {
                it.addLayer(layerC)
                layerC.defaultStyle = styleC
            }
        }
    }

    private fun initGeoJsonData() {
        layerA.setGeoJson(JsonFileUtil.loadJSONFromFile(mapView.context, R.raw.layer_a))
        layerA.defaultStyle = styleA
        layerB.setGeoJson(JsonFileUtil.loadJSONFromFile(mapView.context, R.raw.layer_b))
        layerB.defaultStyle = styleB
        layerC.setGeoJson(JsonFileUtil.loadJSONFromFile(mapView.context, R.raw.layer_c))
        layerC.defaultStyle = styleC
    }

    override fun onSupportNavigateUp(): Boolean {
        super.onBackPressed()
        finish()
        return true
    }
}

interface LayersUpdateListener {
    fun onLayersUpdated(layers: List<String>)
}

interface OnStartDragListener {
    fun onStartDrag(viewHolder: ViewHolder)
}

interface ItemTouchHelperAdapter {
    fun onItemMove(fromPosition: Int, toPosition: Int): Boolean
}

class LayersAdapter(val dragStartListener: OnStartDragListener) : Adapter<ViewHolder>(),
    ItemTouchHelperAdapter {

    var layerIds = listOf<String>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return LayerViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_dragable_layer, parent, false)
        ).apply {
            this.itemView.setOnTouchListener { view, event ->
                if (event.action == MotionEvent.ACTION_DOWN ||
                    event.action == MotionEvent.ACTION_UP) {
                    dragStartListener.onStartDrag(this)
                }
                false
            }
        }
    }

    override fun getItemCount(): Int {
        return layerIds.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        (holder as LayerViewHolder).apply {
            this.nameView.text = "Layer ${layerIds[position]}"
        }
    }

    class LayerViewHolder(itemView: View) : ViewHolder(itemView) {
        val nameView = itemView.findViewById<TextView>(R.id.name)
    }

    class SimpleItemTouchHelperCallback(val adapter: ItemTouchHelperAdapter) : ItemTouchHelper.Callback() {

        override fun isLongPressDragEnabled() = true

        override fun onSwiped(viewHolder: ViewHolder, direction: Int) {}

        override fun isItemViewSwipeEnabled() = false

        override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: ViewHolder): Int {
            return makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0)
        }

        override fun onMove(recyclerView: RecyclerView, viewHolder: ViewHolder, target: ViewHolder): Boolean {
            adapter.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
            return true
        }
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(layerIds, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(layerIds, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
        return true
    }
}

class ReorderLayersDialogFragment : DialogFragment(), OnStartDragListener {

    private val layersAdapter = LayersAdapter(this)
    private val callback = LayersAdapter.SimpleItemTouchHelperCallback(layersAdapter)
    private val itemTouchHelper = ItemTouchHelper(callback)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map_layers_reorder, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<Toolbar>(R.id.toolbar).setOnMenuItemClickListener { item ->
            if (item?.itemId == R.id.action_close) {
                dismiss()
                true
            } else {
                false
            }
        }
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler)
        recyclerView.adapter = layersAdapter
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        recyclerView.addItemDecoration(DividerItemDecoration(view.context, DividerItemDecoration.VERTICAL))
        itemTouchHelper.attachToRecyclerView(recyclerView)
        layersAdapter.layerIds = arguments?.getStringArrayList(ARG_LAYER_IDS) ?: emptyList()
    }

    override fun onDismiss(dialog: DialogInterface) {
        val layersActivity = requireActivity() as LayersUpdateListener
        layersActivity.onLayersUpdated(layersAdapter.layerIds)
        super.onDismiss(dialog)
    }

    companion object {
        private const val ARG_LAYER_IDS = "layer_ids"

        @JvmStatic
        fun newInstance(layers: List<Layer>): ReorderLayersDialogFragment {
            val frag = ReorderLayersDialogFragment()
            val ids = arrayListOf<String>()
            layers.forEach { layer -> ids.add(layer.id) }
            frag.arguments = Bundle().apply {
                this.putStringArrayList(ARG_LAYER_IDS, ids)
            }
            return frag
        }
    }

    override fun onStartDrag(viewHolder: ViewHolder) {
        itemTouchHelper.startDrag(viewHolder)
    }
}