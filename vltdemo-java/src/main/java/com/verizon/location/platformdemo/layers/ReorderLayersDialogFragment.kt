package com.verizon.location.platformdemo.layers

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.verizon.location.maps.model.layer.Layer
import com.verizon.location.platformdemo.R
import java.util.*

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
        Log.d("EditLayersDialog", "#onDismiss")
        val layersActivity = requireActivity() as LayersUpdateListener
        layersActivity.onLayersUpdated(layersAdapter.layerIds)
        super.onDismiss(dialog)
    }

    companion object {
        private const val ARG_LAYER_IDS = "layer_ids"

        @JvmStatic
        fun newInstance(layers: List<Layer>): ReorderLayersDialogFragment {
            val frag =
                ReorderLayersDialogFragment()
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

    interface LayersUpdateListener {
        fun onLayersUpdated(layers: List<String>)
    }

    interface ItemTouchHelperAdapter {
        fun onItemMove(fromPosition: Int, toPosition: Int): Boolean
    }

    class LayersAdapter(val dragStartListener: OnStartDragListener) : RecyclerView.Adapter<ViewHolder>(),
        ItemTouchHelperAdapter {

        var layerIds = listOf<String>()
            set(value) {
                field = value
                Log.d("LayersAdapter", "layer count:${value.size}")
                notifyDataSetChanged()
            }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return LayerViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_dragable_layer,
                    parent,
                    false
                )
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
}