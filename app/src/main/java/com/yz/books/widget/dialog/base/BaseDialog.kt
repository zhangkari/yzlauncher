package com.yz.books.widget.dialog.base

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.ViewGroup
import com.yz.books.R
import com.yz.books.widget.focuslayout.FocusIndicator

/**
 * @author lilin
 * @time on 2019-12-17 09:30
 */
abstract class BaseDialog(context: Context) :
    Dialog(context, R.style.BaseDialogStyle), IDialogView {

    private lateinit var mFocusIndicator: FocusIndicator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (getLayoutId() != 0) {
            setContentView(getLayoutId())
        }
        initView()
        initListener()

        mFocusIndicator = FocusIndicator()
        mFocusIndicator.bind(this, showFocusIndicator())
        window?.decorView?.viewTreeObserver?.removeOnGlobalFocusChangeListener(mFocusIndicator.layout)
    }

    override fun onStart() {
        super.onStart()
        window?.decorView?.viewTreeObserver?.addOnGlobalFocusChangeListener(mFocusIndicator.layout)
    }

    override fun onStop() {
        window?.decorView?.viewTreeObserver?.removeOnGlobalFocusChangeListener(mFocusIndicator.layout)
        super.onStop()
    }

    protected open fun showFocusIndicator(): Boolean {
        return true
    }

    override fun initView() {
        if (isBottomDialog()) {
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            window?.setGravity(Gravity.BOTTOM)
            window?.setWindowAnimations(R.style.BaseDialogAnim)
        }

        setCancelable(false)
        setCanceledOnTouchOutside(false)
    }

    protected fun showCenter() {
        window?.setGravity(Gravity.CENTER)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            dismiss()
        }
        return false
    }
}