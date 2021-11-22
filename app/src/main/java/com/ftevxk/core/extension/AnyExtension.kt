package com.ftevxk.core.extension

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.databinding.ObservableBoolean

/**
 * 反射设置私有字段
 */
fun Any.setDeclaredField(name: String, value: Any) {
    this.javaClass.getDeclaredField(name).runCatching {
        isAccessible = true
        set(this@setDeclaredField, value)
    }
}

/**
 * 反射获取私有字段
 */
inline fun <reified T> Any.getDeclaredField(name: String): T? {
    return this.javaClass.getDeclaredField(name).runCatching {
        isAccessible = true
        get(this@getDeclaredField) as T
    }.getOrElse { null }
}

/**
 * 执行延时任务
 * @return 调用 ObservableBoolean.set(true) 可取消当前延迟任务
 */
fun runDelayed(delayMillis: Long, run: () -> Unit): ObservableBoolean {
    var handler: Handler? = Handler(Looper.getMainLooper())
    val runnable = Runnable {
        run.invoke()
        handler = null
    }
    handler?.postDelayed(runnable, delayMillis)
    return object : ObservableBoolean(false) {
        override fun set(value: Boolean) {
            super.set(value)
            if (value) {
                handler?.removeCallbacks(runnable)
                handler = null
            }
        }
    }
}