package com.ftevxk.core.util

import android.os.SystemClock
import android.view.View
import androidx.databinding.BindingAdapter


abstract class NoDoubleClickListener : View.OnClickListener {
    private var lastClickTime: Long = 0

    companion object {
        const val MIN_CLICK_DELAY_TIME = 1000

        @BindingAdapter("onNotDoubleClick")
        fun View.setNoDoubleClickListener(listener: (View) -> Unit) {
            this.setOnClickListener(object : NoDoubleClickListener() {
                override fun onNoDoubleClick(view: View) {
                    listener.invoke(view)
                }
            })
        }
    }

    override fun onClick(v: View) {
        val currentTime = SystemClock.elapsedRealtime()
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime
            onNoDoubleClick(v)
        }
    }

    protected abstract fun onNoDoubleClick(view: View)
}