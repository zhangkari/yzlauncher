package com.yz.books.base.activity

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.yz.books.base.interf.IView
import com.yz.books.common.Constant
import com.yz.books.ext.dismissDialog
import com.yz.books.widget.dialog.LoadingDialog
import com.yz.books.widget.focuslayout.FocusIndicator
import me.jessyan.autosize.internal.CustomAdapt

/**
 * @author lilin
 * @time on 2019-12-16 21:18
 */
abstract class BaseActivity : AppCompatActivity(), IView, CustomAdapt {
//region var/val

    lateinit var focusIndicator: FocusIndicator

    /**
     * 加载提示框
     */
    private val mLoadingDialog by lazy {
        LoadingDialog(this)
    }

    private val mBaseHandler by lazy(LazyThreadSafetyMode.NONE) {
        Handler()
    }

//endregion

//region implement methods

    override fun isBaseOnWidth() = true

    override fun getSizeInDp(): Float {
        return if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            360f
        } else {
            640f
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        dismissLoading()
        mBaseHandler.removeCallbacksAndMessages(null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = if (Constant.SCREEN_ORIENTATION_LANDSCAPE) {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE//横屏
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        if (getLayoutId() != 0) {
            setContentView(getLayoutId())
        }
        initVM()
        initView()
        initData()
        initListener()

        //在窗口完成以后进行加载，这里面的run方法是在onResume之后运行的
        window.decorView.post {
            mBaseHandler.post {
                afterWindowViewMeasured()
            }
        }

        focusIndicator = FocusIndicator()
        focusIndicator.bind(this, showFocusIndicator())
    }

    protected open fun showFocusIndicator(): Boolean {
        return true;
    }

    override fun showLoading(tips: Any?) {
        with(mLoadingDialog) {
            if (!isShowing) {
                setMessage("正在加载...")
                show()
            }
        }
    }

    override fun dismissLoading() {
        mLoadingDialog.dismissDialog()
    }

//endregion

//region public methods

//endregion

//region private methods

//endregion
}