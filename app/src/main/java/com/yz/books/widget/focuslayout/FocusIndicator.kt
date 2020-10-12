package com.yz.books.widget.focuslayout

import android.app.Activity
import android.app.Dialog
import android.widget.RelativeLayout

class FocusIndicator() {
    lateinit var layout: FocusLayout
    fun bind(activity: Activity, showIndicator: Boolean) {
        if (!showIndicator) {
            return
        }
        layout = FocusLayout(activity)
        activity.addContentView(
            layout,
            RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
            )
        )
        activity.window.decorView.viewTreeObserver.addOnGlobalFocusChangeListener(layout)
    }

    fun bind(dialog: Dialog, showIndicator: Boolean) {
        if (!showIndicator) {
            return
        }
        layout = FocusLayout(dialog.context)
        dialog.addContentView(
            layout,
            RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
            )
        )
        dialog.window!!.decorView.viewTreeObserver.addOnGlobalFocusChangeListener(layout)
    }

    fun getIndicatorLayout(): FocusLayout {
        return layout
    }
}