package com.verizon.location.navdemo

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView

class SearchBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    private val iconView: AppCompatImageView
    private val textView: TextView
    private val hintView: TextView

    init {
        inflate(context, R.layout.view_search_bar, this)
        iconView = findViewById(R.id.search_bar_icon)
        textView = findViewById(R.id.search_bar_text)
        hintView = findViewById(R.id.search_bar_hint)
        radius = 16f
    }

    var text: String = ""
        set(value) {
            field = value
            if (value.isBlank()) {
                showHint()
            } else {
                showSearchText()
            }
        }

    private fun showSearchText() {
        textView.visibility = VISIBLE
        hintView.visibility = INVISIBLE
    }

    private fun showHint() {
        textView.visibility = INVISIBLE
        hintView.visibility = VISIBLE
    }
}