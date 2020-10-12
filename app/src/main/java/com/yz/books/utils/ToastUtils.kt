package com.yz.books.utils

import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Message
import android.view.Gravity
import android.widget.Toast
import java.lang.reflect.Field

/**
 * @author lilin
 * @time on 2020-01-16 09:25
 */
class ToastUtils {
    class SafelyHandlerWrapper(private val impl: Handler) : Handler() {

        override fun dispatchMessage(msg: Message) {
            try {
                super.dispatchMessage(msg)
            } catch (e: Exception) {
            }

        }

        override fun handleMessage(msg: Message) {
            impl.handleMessage(msg)//需要委托给原Handler执行
        }
    }

    companion object {
        private var mToast: Toast? = null

        private var sField_TN: Field? = null
        private var sField_TN_Handler: Field? = null

        init {
            try {
                sField_TN = Toast::class.java.getDeclaredField("mTN")
                sField_TN!!.isAccessible = true
                sField_TN_Handler = sField_TN!!.type.getDeclaredField("mHandler")
                sField_TN_Handler!!.isAccessible = true
            } catch (e: Exception) {
            }

        }

        fun showToast(context: Context, content: String?) {
            if (content == null || "Job was" in content) {// 协程取消异常，不提示
                return
            }

            //val view = LayoutInflater.from(context).inflate(R.layout.toast_view,null)
            //val tvTip = view.findViewById<TextView>(R.id.tv_toast_tip)
            //val ivTip = view.findViewById<ImageView>(R.id.iv_toast_succeed)
            //tvTip.text = content
            /*ivTip.visibility = if (content.contains("成功")) {
                View.VISIBLE
            } else {
                View.GONE
            }*/
            if (mToast == null) {
                mToast = Toast.makeText(context.applicationContext, "", Toast.LENGTH_LONG)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N &&
                Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                hook(mToast)
            }

            mToast?.apply {
                //this.duration = duration
                //setGravity(Gravity.CENTER,0,-50)
                setText(content)
                show()
            }
        }

        private fun hook(toast: Toast?) {
            try {
                val tn = sField_TN!!.get(toast)
                val preHandler = sField_TN_Handler!!.get(tn) as Handler
                sField_TN_Handler!!.set(tn, SafelyHandlerWrapper(preHandler))
            } catch (e: Exception) {
            }

        }
    }
}