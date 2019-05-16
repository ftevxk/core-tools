@file:Suppress("unused")

package cn.ftevxk.base.extension

import android.util.Log
import cn.ftevxk.base.BuildConfig

object LogExtension {
    //全局Log开关，releaseLog可保留release下的Log
    val isDebug = BuildConfig.DEBUG

    //记录线程中调用行堆栈位置
    private var stackTraceIndex = -1

    /**
     * 获得Log调用行可高亮信息
     */
    fun getLineTag(): String {
        var isCurrent = false
        val stackTraceElements = Thread.currentThread().stackTrace
        if (stackTraceIndex > 0) {
            val stackTrace = stackTraceElements[stackTraceIndex]
            return "${stackTrace.methodName}(${stackTrace.fileName}:${stackTrace.lineNumber})"
        } else {
            stackTraceElements.forEachIndexed { index, stackTrace ->
                if (isCurrent && stackTrace.fileName != "${javaClass.simpleName}.kt") {
                    stackTraceIndex = index
                    return "${stackTrace.methodName}(${stackTrace.fileName}:${stackTrace.lineNumber})"
                } else {
                    if (stackTrace.fileName == "${javaClass.simpleName}.kt") isCurrent = true
                }
            }
        }
        return javaClass.simpleName
    }
}

fun Any.vLog(msg: String, releaseLog: Boolean = false,
             tag: String = LogExtension.getLineTag()) {
    if (LogExtension.isDebug || releaseLog) {
        msg.splitStr().forEach {
            Log.v(tag, it)
        }
    }
}

fun Any.dLog(msg: String, releaseLog: Boolean = false,
             tag: String = LogExtension.getLineTag()) {
    if (LogExtension.isDebug || releaseLog) {
        msg.splitStr().forEach {
            Log.d(tag, it)
        }
    }
}

fun Any.iLog(msg: String, releaseLog: Boolean = false,
             tag: String = LogExtension.getLineTag()) {
    if (LogExtension.isDebug || releaseLog) {
        msg.splitStr().forEach {
            Log.i(tag, it)
        }
    }
}

fun Any.wLog(msg: String, releaseLog: Boolean = false,
             tag: String = LogExtension.getLineTag()) {
    if (LogExtension.isDebug || releaseLog) {
        msg.splitStr().forEach {
            Log.w(tag, it)
        }
    }
}

/**
 * 默认releaseLog为true
 */
fun Any.eLog(msg: String, releaseLog: Boolean = true,
             tag: String = LogExtension.getLineTag()) {
    if (LogExtension.isDebug || releaseLog) {
        msg.splitStr().forEach {
            Log.e(tag, it)
        }
    }
}