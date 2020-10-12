package com.yz.books.widget.pop

import android.app.Activity
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow

/**
 * @author lilin
 * @time on 2019-12-16 16:08
 */
abstract class BasePopupWindow(val context: Activity) : PopupWindow() {

    init {
        initView()
        initListener()
        initLayoutParams()
        measure()
    }

    abstract fun initView()

    abstract fun initListener()

    private fun initLayoutParams() {
        width = ViewGroup.LayoutParams.WRAP_CONTENT//context.dp2px(118)
        height =
            ViewGroup.LayoutParams.WRAP_CONTENT//context.dp2px(110)//ViewGroup.LayoutParams.WRAP_CONTENT
        isFocusable = true
        isOutsideTouchable = true
        setBackgroundDrawable(ColorDrawable(0x00000000))
        setBackgroundAlpha(1.0f)
    }

    private fun setBackgroundAlpha(bgAlpha: Float) {
        with(context) {
            val lp = window.attributes
            lp.alpha = bgAlpha
            window.addFlags(android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            window.setDimAmount(0.2f)
            window.attributes = lp
        }
    }

    private fun measure() {
        contentView.measure(makeDropDownMeasureSpec(width), makeDropDownMeasureSpec(height))
    }

    private fun makeDropDownMeasureSpec(measureSpec: Int): Int {
        return View.MeasureSpec.makeMeasureSpec(
            View.MeasureSpec.getSize(measureSpec),
            getDropDownMeasureSpecMode(measureSpec)
        )
    }

    private fun getDropDownMeasureSpecMode(measureSpec: Int): Int {
        return when (measureSpec) {
            ViewGroup.LayoutParams.WRAP_CONTENT -> View.MeasureSpec.UNSPECIFIED
            else -> View.MeasureSpec.EXACTLY
        }
    }
}