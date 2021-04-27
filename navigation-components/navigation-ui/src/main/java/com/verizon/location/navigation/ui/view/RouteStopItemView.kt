package com.verizon.location.navigation.ui.view

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.verizon.location.navigation.ui.R

class RouteStopItemView(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    val iconView: ImageView
    var nameView: TextView

    init {
        inflate(context, R.layout.view_route_stop_item, this)
        nameView = findViewById(R.id.name)
        iconView = findViewById(R.id.icon)
        radius = 16f

        val tv = TypedValue()
        context.theme.resolveAttribute(R.attr.colorSurface, tv, true)
        setBackgroundColor(tv.data)
    }

    fun setIcon(resId: Int) {
        iconView.setImageResource(resId)
    }

    var text: String
        get() = nameView.text.toString()
        set(value) {
            nameView.text = value
        }
}