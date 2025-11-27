package com.example.petrov122_prs.domain.utils

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.petrov122_prs.R
import com.google.android.material.card.MaterialCardView

/**
 * Extension functions for showing tooltips on views
 */

/**
 * Show a tooltip above or below a view
 */
fun View.showTooltip(
    message: String,
    duration: Long = 3000L,
    onDismiss: (() -> Unit)? = null
) {
    val tooltipView = createTooltipView(message)
    val popupWindow = PopupWindow(
        tooltipView,
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT,
        true
    )

    // Set background to transparent
    popupWindow.isOutsideTouchable = true
    popupWindow.isTouchable = true

    // Calculate position
    val location = IntArray(2)
    this.getLocationOnScreen(location)
    
    // Measure tooltip view
    tooltipView.measure(
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    )
    
    val tooltipWidth = tooltipView.measuredWidth
    val tooltipHeight = tooltipView.measuredHeight
    
    // Position above the view
    val xOffset = (this.width - tooltipWidth) / 2
    val yOffset = -(tooltipHeight + 16)

    // Show popup
    popupWindow.showAsDropDown(this, xOffset, yOffset)

    // Auto dismiss after duration
    this.postDelayed({
        if (popupWindow.isShowing) {
            popupWindow.dismiss()
            onDismiss?.invoke()
        }
    }, duration)

    // Dismiss on touch
    tooltipView.setOnClickListener {
        popupWindow.dismiss()
        onDismiss?.invoke()
    }
}

/**
 * Create a styled tooltip view
 */
private fun View.createTooltipView(message: String): View {
    val context = this.context
    
    // Create card container
    val card = MaterialCardView(context).apply {
        radius = 8.dpToPx(context)
        cardElevation = 4.dpToPx(context)
        setCardBackgroundColor(ContextCompat.getColor(context, R.color.tooltip_background))
        setPadding(12.dpToPx(context).toInt(), 8.dpToPx(context).toInt(), 
                   12.dpToPx(context).toInt(), 8.dpToPx(context).toInt())
    }
    
    // Create text view
    val textView = TextView(context).apply {
        text = message
        textSize = 14f
        setTextColor(ContextCompat.getColor(context, R.color.tooltip_text))
        maxWidth = 250.dpToPx(context).toInt()
    }
    
    card.addView(textView)
    return card
}

/**
 * Convert dp to pixels
 */
private fun Int.dpToPx(context: Context): Float {
    return this * context.resources.displayMetrics.density
}

/**
 * Show tooltip with custom positioning
 */
fun View.showTooltipAtPosition(
    message: String,
    xPos: Int,
    yPos: Int,
    duration: Long = 3000L,
    onDismiss: (() -> Unit)? = null
) {
    val tooltipView = createTooltipView(message)
    val popupWindow = PopupWindow(
        tooltipView,
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT,
        true
    )

    popupWindow.isOutsideTouchable = true
    popupWindow.isTouchable = true
    popupWindow.showAtLocation(this, Gravity.NO_GRAVITY, xPos, yPos)

    this.postDelayed({
        if (popupWindow.isShowing) {
            popupWindow.dismiss()
            onDismiss?.invoke()
        }
    }, duration)

    tooltipView.setOnClickListener {
        popupWindow.dismiss()
        onDismiss?.invoke()
    }
}