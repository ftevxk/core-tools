@file:Suppress("PrivateApi", "DiscouragedPrivateApi", "MemberVisibilityCanBePrivate")

package com.ftevxk.core.util

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.CountDownTimer
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.text.HtmlCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.ftevxk.core.extension.getDeclaredField
import com.ftevxk.example.R

object ToolUtil {

    /**
     * 获取当前应用的Application
     * 先使用ActivityThread里获取Application的方法，如果没有获取到，
     * 再使用AppGlobals里面的获取Application的方法
     */
    fun getCurApplication(): Application? {
        var application: Application? = null
        try {
            val atClass = Class.forName("android.app.ActivityThread")
            val currentApplicationMethod = atClass.getDeclaredMethod("currentApplication")
            currentApplicationMethod.isAccessible = true
            application = currentApplicationMethod.invoke(null) as Application
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (application != null) return application
        try {
            val atClass = Class.forName("android.app.AppGlobals")
            val currentApplicationMethod = atClass.getDeclaredMethod("getInitialApplication")
            currentApplicationMethod.isAccessible = true
            application = currentApplicationMethod.invoke(null) as Application
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return application
    }

//    /**
//     * 判断网络是否已连接
//     */
//    fun isNetworkConnected(): Boolean {
//        val cm = getCurApplication()?.getSystemService(
//                Context.CONNECTIVITY_SERVICE
//        ) as? ConnectivityManager
//        if (Build.VERSION.SDK_INT < 23) {
//            val mWiFiNetworkInfo = cm?.activeNetworkInfo
//            if (mWiFiNetworkInfo != null) {
//                if (mWiFiNetworkInfo.type == ConnectivityManager.TYPE_WIFI) {
//                    return true
//                } else if (mWiFiNetworkInfo.type == ConnectivityManager.TYPE_MOBILE) {
//                    return true
//                }
//            }
//        } else {
//            val network = cm?.activeNetwork
//            if (network != null) {
//                val nc = cm.getNetworkCapabilities(network)
//                if (nc != null) {
//                    if (nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
//                        return true
//                    } else if (nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
//                        return true
//                    }
//                }
//            }
//        }
//        return false
//    }

    /**
     * 设置Toolbar的标题跑马灯展示
     */
    fun toolbarTitleMarquee(toolbar: Toolbar) {
        toolbar.getDeclaredField<TextView>("mTitleTextView")?.run {
            setSingleLine()
            isSelected = true
            isFocusable = true
            isFocusableInTouchMode = true
            setHorizontallyScrolling(true)
            marqueeRepeatLimit = Integer.MAX_VALUE
            ellipsize = TextUtils.TruncateAt.MARQUEE
        }
    }

    /**
     * EditText改变输入法状态
     * @param showInput 是否显示输入法
     */
    fun inputState(editText: EditText, showInput: Boolean) {
        editText.isFocusable = true
        editText.isFocusableInTouchMode = true
        val manager =
                editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (showInput) {
            editText.requestFocus()
            manager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
        } else {
            editText.clearFocus()
            manager.hideSoftInputFromWindow(
                    editText.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    /**
     * 倒计时
     *
     * @param lifecycle     在页面销毁时自动取消计时器
     * @param totalTime     总倒计时时间，单位默认秒
     * @param result        每秒返回回调
     */
    fun countDown(
            lifecycle: Lifecycle?, totalTime: Long,
            result: (millisUntilFinished: Long) -> Unit
    ): CountDownTimer {
        return countDown(lifecycle, totalTime, 1000, result)
    }

    /**
     * 倒计时
     *
     * @param lifecycle     在页面销毁时自动取消计时器
     * @param totalTime     总倒计时时间，单位默认秒
     * @param period        倒计时周期
     * @param result        每秒返回回调
     */
    fun countDown(
            lifecycle: Lifecycle?, totalTime: Long, period: Long,
            result: (millisUntilFinished: Long) -> Unit
    ): CountDownTimer {
        return object : CountDownTimer(totalTime, period) {
            override fun onTick(millisUntilFinished: Long) {
                result.invoke(millisUntilFinished)
            }

            override fun onFinish() {
                result.invoke(0)
            }
        }.apply {
            start()
            lifecycle?.addObserver(LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_DESTROY) cancel()
            })
        }
    }

    /**
     * 判断应用是否已安装
     */
    fun isInstalledApp(packageName: String): Boolean {
        val packageManager = getCurApplication()?.packageManager
        val packages = packageManager?.getInstalledPackages(0)
        for (i in 0 until (packages?.size ?: 0)) {
            if (packages!![i].packageName == packageName) {
                return true
            }
        }
        return false
    }

    /**
     * 带HTML符号转义字符的转为正常文本
     * https://www.iteye.com/blog/jiangyongyuan-393711
     */
    fun htmlSignToText(htmlText: String): CharSequence {
        return HtmlCompat.fromHtml(
                htmlText.replace("&iexcl;", "?")
                        .replace("&lt;", "<")
                        .replace("&gt;", ">")
                        .replace("&amp;", "&")
                        .replace("&yen;", "￥")
                        .replace("&brvbar;", "|")
                        .replace("&ldquo;", "“")
                        .replace("&rdquo;", "”")
                        .replace("&quot;", "\"")
                        .replace("&copy;", "©")
                        .replace("&nbsp;", " "),
                HtmlCompat.FROM_HTML_MODE_LEGACY
        )
    }

    /**
     * 显示通知
     */
    fun handleNotification(context: Context, content: String,
                           title: String = context.getString(R.string.app_name),
                           intent: Intent? = null,
                           cancelLast: Boolean = false,
                           channelId: String = "default",
                           channelName: String = "默认通知",
                           important: Boolean = false,
                           notifyId: Int = View.generateViewId()): Int {
        val notifyManager = NotificationManagerCompat.from(context)
        val builder = if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(channelId, channelName,
                    if (important) NotificationManager.IMPORTANCE_HIGH else
                        NotificationManager.IMPORTANCE_DEFAULT)
            notifyManager.createNotificationChannel(channel)
            NotificationCompat.Builder(context, channelId)
        } else {
            NotificationCompat.Builder(context, channelId)
        }
        builder.setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true)
        if (intent != null) {
            val pendingIntent = PendingIntent.getActivity(context,
                    System.currentTimeMillis().toInt(),
                    intent, PendingIntent.FLAG_UPDATE_CURRENT)
            builder.setContentIntent(pendingIntent)
        }
        if (cancelLast) notifyManager.cancel(notifyId)
        notifyManager.notify(notifyId, builder.build())
        return notifyId
    }
}