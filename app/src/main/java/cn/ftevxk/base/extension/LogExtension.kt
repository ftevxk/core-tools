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
                if (isCurrent && stackTrace.fileName != "LogExtension.kt") {
                    stackTraceIndex = index
                    return "${stackTrace.methodName}(${stackTrace.fileName}:${stackTrace.lineNumber})"
                } else {
                    if (stackTrace.fileName == "LogExtension.kt") isCurrent = true
                }
            }
        }
        return javaClass.simpleName
    }

    /**
     * 数据分割成不超过 maxLength 的数据
     */
    fun splitStr(str: String, maxLength: Int = 4 * 1024): Array<String?> {
        var start = 0
        val strings = arrayOfNulls<String>(str.length / maxLength + 1)
        for (i in 0 until strings.size) {
            if (start + maxLength < str.length) {
                strings[i] = str.substring(start, start + maxLength)
                start += maxLength
            } else {
                strings[i] = str.substring(start, str.length)
                start = str.length
            }
        }
        return strings
    }
}

fun Any.vLog(msg: String, releaseLog: Boolean = false,
             tag: String = LogExtension.getLineTag()) {
    if (LogExtension.isDebug || releaseLog) {
        LogExtension.splitStr(msg).forEach {
            Log.v(tag, it)
        }
    }
}

fun Any.dLog(msg: String, releaseLog: Boolean = false,
             tag: String = LogExtension.getLineTag()) {
    if (LogExtension.isDebug || releaseLog) {
        LogExtension.splitStr(msg).forEach {
            Log.d(tag, it)
        }
    }
}

fun Any.iLog(msg: String, releaseLog: Boolean = false,
             tag: String = LogExtension.getLineTag()) {
    if (LogExtension.isDebug || releaseLog) {
        LogExtension.splitStr(msg).forEach {
            Log.i(tag, it)
        }
    }
}

fun Any.wLog(msg: String, releaseLog: Boolean = false,
             tag: String = LogExtension.getLineTag()) {
    if (LogExtension.isDebug || releaseLog) {
        LogExtension.splitStr(msg).forEach {
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
        LogExtension.splitStr(msg).forEach {
            Log.e(tag, it)
        }
    }
}