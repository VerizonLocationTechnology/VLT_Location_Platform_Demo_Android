package com.verizon.location.navigation.ui.view

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatImageView
import com.verizon.location.common.model.ManeuverPrompt
import com.verizon.location.common.model.RouteStop
import com.verizon.location.common.util.UiUtil
import com.verizon.location.navigation.ui.R
import com.verizon.location.navigation.ui.util.ConversionUtil
import com.verizon.location.navigation.ui.util.NavatarUtil

class RouteDetailsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    val closeIconView: AppCompatImageView
    private val listView: ListView
    private val destinationView: TextView
    val tripTimeView: TextView
    private var routeManeuvers: List<ManeuverPrompt> = listOf()
    private var tripInfo: String = ""
    var routeTime: Int = 0
    var routeDistance: Double = 0.0
    var listener: RouteDetailsViewListener? = null
    var currentPromptIndex = -1

    init {
        val pad = UiUtil.convertDpToPx(context, 4f).toInt()
        setPadding(4 * pad, 6 * pad, 4 * pad, 6 * pad)
        orientation = VERTICAL
        inflate(context, R.layout.vew_route_details, this)
        closeIconView = findViewById(R.id.close_icon)
        closeIconView.setOnClickListener {
            hide()
        }
        listView = findViewById(R.id.maneuver_list)
        destinationView = findViewById(R.id.destination)
        tripTimeView = findViewById(R.id.trip_time)
    }

    fun show() {
        routeManeuvers.let {
            val stringArray = Array<String>(it.size) {""}
            for (i in it.indices) {
                stringArray[i] = it[i].text
            }
            destinationView.text = tripInfo
            tripTimeView.text = "${ConversionUtil.convertTimeToDisplayString(routeTime)} (${ConversionUtil.convertDistanceToDisplayString(routeDistance)})"
            val adapter = ManeuversListAdapter(context, it)
            listView.adapter = adapter
        }

        visibility = VISIBLE
    }

    fun updatePrompt(prompt: String) {
        tripTimeView.text = prompt
    }

    fun hide() {
        visibility = View.GONE
        listener?.onRouteDetailsViewClosed(routeTime, routeDistance)
    }

    fun isShowing(): Boolean {
        return visibility == VISIBLE
    }

    fun setRouteDetails(maneuvers: List<ManeuverPrompt>, seconds: Int, distance: Double, stops: List<RouteStop>) {
        this.routeManeuvers = maneuvers
        routeTime = seconds
        routeDistance = distance
        tripInfo = "From ${stops[0].name} to ${stops[stops.size-1].name}"
    }

    fun setCurrentManeuver(prompt: ManeuverPrompt) {
        if (isShowing()) {
            currentPromptIndex = this.routeManeuvers.indexOf(prompt)
            if (this.routeManeuvers.size > 1 && currentPromptIndex >= 0) {
                val adapter = ManeuversListAdapter(context, this.routeManeuvers)
                listView.adapter = adapter
                (listView.adapter as ManeuversListAdapter).setCurrentPrompt(this.routeManeuvers.get(currentPromptIndex))
            }
        }
    }

    fun updateRouteManeuvers(maneuvers: List<ManeuverPrompt>) {
        this.routeManeuvers = maneuvers
        if (isShowing()) {
            val adapter = ManeuversListAdapter(context, this.routeManeuvers)
            listView.adapter = adapter
        }
    }

    fun setRouteDetailsViewListener(routeDetailsViewListener: RouteDetailsViewListener) {
        listener = routeDetailsViewListener
    }

    interface RouteDetailsViewListener {
        fun onRouteDetailsViewClosed(secondsToTravel: Int, metersToTravel: Double) {}
    }
}

internal class ManeuversListAdapter(
    private val context: Context,
    items: List<ManeuverPrompt>
) : BaseAdapter() {
    private var prompts
            : List<ManeuverPrompt> = items

    private var activePrompt: ManeuverPrompt? = null

    override fun getCount(): Int {
        return prompts.size
    }

    override fun getItem(position: Int): Any {
        return prompts[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(
        position: Int,
        view: View?,
        parent: ViewGroup?
    ): View {
        var viewHolder: ViewHolder? = null
        val rowView: View
        if (view == null) {
            rowView = LayoutInflater.from(context).inflate(R.layout.view_route_detail_item, parent, false)
            viewHolder = ViewHolder(view = rowView)
            rowView.tag = viewHolder
        } else {
            rowView = view
            viewHolder = rowView.tag as ViewHolder
        }

        val currentItem: ManeuverPrompt = getItem(position) as ManeuverPrompt
        viewHolder.type.setImageResource(NavatarUtil.getNavatar(currentItem.type))
        viewHolder.prompt.text = currentItem.text

        if (position == 0) {
            val tv = TypedValue()
            context.theme.resolveAttribute(R.attr.colorPrimaryVariant, tv, true)
            rowView.setBackgroundColor(tv.data)
        }

        return rowView
    }

    private class ViewHolder(view: View) {
        var type: ImageView = view.findViewById<View>(R.id.maneuver_type) as ImageView
        var prompt: TextView = view.findViewById<View>(R.id.maneuver_text) as TextView
    }

    fun setCurrentPrompt(prompt: ManeuverPrompt) {
        activePrompt = prompt
        val index = prompts.indexOf(prompt)
        if (index > 0) {
            mutableListOf<ManeuverPrompt>().apply {
                val tmpList = prompts.subList(index, prompts.size)
                prompts = tmpList
                println()
                notifyDataSetChanged()
            }
        }
    }
}
