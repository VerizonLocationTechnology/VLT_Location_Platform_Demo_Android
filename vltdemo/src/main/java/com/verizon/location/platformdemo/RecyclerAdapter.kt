package com.verizon.location.platformdemo

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.widget.TextView
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout

import androidx.recyclerview.widget.RecyclerView


class RecyclerAdapter(val context: Context) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    private val titles = arrayOf(
        context.resources.getString(R.string.welcome_label),
        context.resources.getString(R.string.title_activity_map_camera_demo),
        context.resources.getString(R.string.title_activity_map_mode_demo),
        context.resources.getString(R.string.title_activity_user_location_demo),
        context.resources.getString(R.string.title_activity_map_shapes_demo),
        context.resources.getString(R.string.title_activity_map_traffic),
        context.resources.getString(R.string.title_activity_map_gestures),
        context.resources.getString(R.string.title_activity_geojson_demo)
    )
    private val summaries = arrayOf(
        context.resources.getString(R.string.welcome_header_label),
        context.resources.getString(R.string.camera_header_label),
        context.resources.getString(R.string.mode_header_label),
        context.resources.getString(R.string.user_location_header_label),
        context.resources.getString(R.string.shapes_header_label),
        context.resources.getString(R.string.traffic_header_label),
        context.resources.getString(R.string.gestures_header_label),
        context.resources.getString(R.string.geojson_header_label)
    )

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemTitle: TextView = itemView.findViewById(R.id.card_title)
        var itemSummary: TextView = itemView.findViewById(R.id.card_subtitle)
        var rootView: LinearLayout = itemView.findViewById(R.id.root_view)

        init {

            itemView.setOnClickListener { v: View  ->
                var position: Int = adapterPosition

                when (position) {
                    1 -> { context.startActivity(Intent(context, MapCameraDemo::class.java)) }
                    2 -> { context.startActivity(Intent(context, MapModeDemo::class.java)) }
                    3 -> { context.startActivity(Intent(context, UserLocationDemo::class.java)) }
                    4 -> { context.startActivity(Intent(context, MapShapesDemo::class.java)) }
                    5 -> { context.startActivity(Intent(context, MapTrafficDemo::class.java)) }
                    6 -> { context.startActivity(Intent(context, MapGesturesDemo::class.java)) }
                    7 -> { context.startActivity(Intent(context, GeojsonDemo::class.java)) }
                }
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.card_view_item, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        viewHolder.itemTitle.text = titles[i]
        viewHolder.itemSummary.text = summaries[i]

        if (i == 0) {
            viewHolder.itemTitle.setTextColor(Color.WHITE)
            viewHolder.itemSummary.setTextColor(Color.WHITE)
            viewHolder.rootView.setBackgroundColor(Color.BLACK)
        } else {
            viewHolder.itemTitle.setTextColor(Color.BLACK)
            viewHolder.itemSummary.setTextColor(Color.BLACK)
            viewHolder.rootView.setBackgroundColor(Color.WHITE)
        }
    }

    override fun getItemCount(): Int {
        return titles.size
    }
}