package com.yz.books.utils

import android.os.Handler
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent


class CommonHandler(
    lifecycleOwner: LifecycleOwner,
    callback: Callback) : Handler(callback), LifecycleObserver {

    private var mLifecycleOwner: LifecycleOwner? = lifecycleOwner

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy() {
        removeCallbacksAndMessages(null)
        mLifecycleOwner?.lifecycle?.removeObserver(this)
        mLifecycleOwner = null
    }
}