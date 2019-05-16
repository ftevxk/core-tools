@file:Suppress("unused")

package cn.ftevxk.base.extension

import android.util.Log
import cn.ftevxk.base.BuildConfig

object LogExtension {
    var logIndex = -1
    //全局Log开关，releaseLog可保留release下的Log
    val isDebug = BuildConfig.DEBUG
}

fun Any.vLog(msg: String, releaseLog: Boolean = false, tag: String = getLineTag()) {
    if (LogExtension.isDebug || releaseLog) {
        splitStr(msg).forEach {
            Log.v(tag, it)
        }
    }
}

fun Any.dLog(msg: String, releaseLog: Boolean = false, tag: String = getLineTag()) {
    if (LogExtension.isDebug || releaseLog) {
        splitStr(msg).forEach {
            Log.d(tag, it)
        }
    }
}

fun Any.iLog(msg: String, releaseLog: Boolean = false, tag: String = getLineTag()) {
    if (LogExtension.isDebug || releaseLog) {
        splitStr(msg).forEach {
            Log.i(tag, it)
        }
    }
}

fun Any.wLog(msg: String, releaseLog: Boolean = false, tag: String = getLineTag()) {
    if (LogExtension.isDebug || releaseLog) {
        splitStr(msg).forEach {
            Log.w(tag, it)
        }
    }
}

/**
 * 默认releaseLog为true
 */
fun Any.eLog(msg: String, releaseLog: Boolean = true, tag: String = getLineTag()) {
    if (LogExtension.isDebug || releaseLog) {
        splitStr(msg).forEach {
            Log.e(tag, it)
        }
    }
}

/**
 * Log打印获得调用处行信息
 */
fun Any.getLineTag(): String {
    var isCurrent = false
    val stackTraceElements = Thread.currentThread().stackTrace
    if (LogExtension.logIndex > 0) {
        val stackTrace = stackTraceElements[LogExtension.logIndex]
        return "${stackTrace.methodName}(${stackTrace.fileName}:${stackTrace.lineNumber})"
    } else {
        stackTraceElements.forEachIndexed { index, stackTrace ->
            if (isCurrent && stackTrace.fileName != "LogExtension.kt") {
                LogExtension.logIndex = index
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
private fun Any.splitStr(str: String, maxLength: Int = 4 * 1024): Array<String?> {
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