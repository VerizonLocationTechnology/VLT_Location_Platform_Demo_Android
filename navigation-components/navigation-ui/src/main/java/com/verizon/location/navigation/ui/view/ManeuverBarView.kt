package com.verizon.location.navigation.ui.view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.verizon.location.navigation.ui.R
import com.verizon.location.common.model.ManeuverPrompt
import com.verizon.location.common.util.DistanceUtil
import com.verizon.location.common.util.UiUtil
import com.verizon.location.navigation.ui.util.NavatarUtil

class ManeuverBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val currentIcon: ImageView
    private val currentPromptView: TextView
    private val distanceView: TextView

    init {
        val dim4dp = UiUtil.convertDpToPx(context, 4f).toInt()
        setPadding(6*dim4dp, 4*dim4dp, 4*dim4dp, 3*dim4dp)
        inflate(context, R.layout.view_maneuver_bar, this)
        currentIcon = findViewById(R.id.current_icon)
        currentPromptView = findViewById(R.id.current_maneuver)
        distanceView = findViewById(R.id.distance_text)
    }

    fun hide() {
        distanceView.text = ""
        if (visibility == VISIBLE) {
            visibility = INVISIBLE
        }
    }

    fun show() {
        invalidate()
        requestLayout()
        if (visibility != VISIBLE) {
            visibility = VISIBLE
        }
    }

    fun update(prompt: ManeuverPrompt, metersRemaining: Double) {
        currentIcon.setImageResource(NavatarUtil.getNavatar(prompt.type))
        currentPromptView.text = prompt.text.split('.').first()
        distanceView.text = DistanceUtil.formattedDistance(metersRemaining)
        invalidate()
        requestLayout()
    }
}