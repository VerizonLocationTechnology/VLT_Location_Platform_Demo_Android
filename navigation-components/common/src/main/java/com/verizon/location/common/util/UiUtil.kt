package com.verizon.location.common.util

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager

fun Context.dpToPx(dp: Float): Float = dp * resources.displayMetrics.density
fun Context.pxToDp(px: Float): Float = px / resources.displayMetrics.density

fun View.setMargin(
    left: Int? = null,
    top: Int? = null,
    right: Int? = null,
    bottom: Int? = null
) {
    val params = layoutParams as ViewGroup.MarginLayoutParams
    params.setMargins(
        left ?: params.leftMargin,
        top ?: params.topMargin,
        right ?: params.rightMargin,
        bottom ?: params.bottomMargin
    )
    layoutParams = params
}

object UiUtil {

    fun convertDpToPx(context: Context, dp: Float): Float {
        return dp * context.resources.displayMetrics.density
    }

    fun convertPxToDp(context: Context, px: Float): Float {
        return px / context.resources.displayMetrics.density
    }

    fun hideKeyboard(view: View) {
        // HACK: can't always get clear focus to actually work. It's just a request.
        val focusable = view.isFocusable
        val focusableInTouchMode = view.isFocusableInTouchMode
        val parent = view.parent as View
        parent.requestFocus()
        view.clearFocus()
        view.isFocusable = false
        view.isFocusableInTouchMode = false
        (view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(view.windowToken, 0)
        view.isFocusable = focusable
        view.isFocusableInTouchMode = focusableInTouchMode
    }

    fun showKeyboard(view: View) {
        view.requestFocus()
        (view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

}