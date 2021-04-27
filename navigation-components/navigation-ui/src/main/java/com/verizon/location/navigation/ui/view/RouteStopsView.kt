package com.verizon.location.navigation.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import com.verizon.location.navigation.ui.R
import com.verizon.location.common.model.RouteStop

class RouteStopsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val stopViews = mutableListOf<RouteStopItemView>()
    private var listener: StopClickListener? = null

    init {
        orientation = VERTICAL
        addEmptyStopView()
        addEmptyStopView()
    }

    fun setStops(stops: List<RouteStop>) {
        removeAllViews()
        stops.forEachIndexed { index, stop ->
            addStopView(index, stop, index == stops.size - 1)
        }
    }

    fun setStopClickListener(listener: StopClickListener) {
        this.listener = listener
    }

    private fun addEmptyStopView() {
        val stopView = RouteStopItemView(context).apply {
            this.layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
                this.bottomMargin = 8
                this.topMargin = 8
            }
        }
        stopViews.add(stopView)
        addView(stopView)
    }

    private fun addStopView(position: Int, stop: RouteStop, isFinalStop: Boolean) {
        val stopView = RouteStopItemView(context).apply {
            this.layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
                this.bottomMargin = 8
                this.topMargin = 8
            }
            if (isFinalStop) {
                this.setIcon(R.drawable.ic_route_destination)
            } else {
                this.setIcon(R.drawable.ic_route_stop)
            }
            this.setOnClickListener { listener?.onStopClicked(position) }
            this.nameView.text = stop.name
        }
        stopViews.add(stopView)
        addView(stopView)
    }

    interface StopClickListener {
        fun onStopClicked(position: Int)
    }
}