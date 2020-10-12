package com.yz.books.widget.dialog

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity

/**
 * @author lilin
 * @time on 2020/4/14 下午9:21
 */

class LoadingDialog(context: Context) : ProgressDialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.setGravity(Gravity.CENTER)
    }

}