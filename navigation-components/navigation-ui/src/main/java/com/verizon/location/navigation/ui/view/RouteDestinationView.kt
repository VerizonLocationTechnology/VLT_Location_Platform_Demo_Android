package com.verizon.location.navigation.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.verizon.location.common.util.UiUtil
import com.verizon.location.navigation.ui.R

class RouteDestinationView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    init {
        val pad = UiUtil.convertDpToPx(context, 4f).toInt()
        setPadding(4*pad, 6*pad, 4*pad, 6*pad)
        orientation = VERTICAL
        inflate(context, R.layout.view_route_destination, this)
    }

    fun show(message: String) {
        findViewById<TextView>(R.id.message_text).text = message
        visibility = VISIBLE
    }

    fun hide() {
        visibility = View.INVISIBLE
    }
}