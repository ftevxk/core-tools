@file:Suppress("unused")

package cn.ftevxk.base.extension

import android.util.Log

fun Any.vLog(msg: String, tag: String = getLineTag()) {
    splitStr(msg).forEach {
        Log.v(tag, it)
    }
}

fun Any.dLog(msg: String, tag: String = getLineTag()) {
    splitStr(msg).forEach {
        Log.d(tag, it)
    }
}

fun Any.iLog(msg: String, tag: String = getLineTag()) {
    splitStr(msg).forEach {
        Log.i(tag, it)
    }
}

fun Any.wLog(msg: String, tag: String = getLineTag()) {
    splitStr(msg).forEach {
        Log.w(tag, it)
    }
}

fun Any.eLog(msg: String, tag: String = getLineTag()) {
    splitStr(msg).forEach {
        Log.e(tag, it)
    }
}

/**
 * Log打印获得调用处行信息
 */
fun Any.getLineTag(): String {
    var isCurrent = false
    val stackTrace = Thread.currentThread().stackTrace
    stackTrace.forEach {
        if (isCurrent && it.fileName != "LogExtension.kt") {
            return "${it.methodName}(${it.fileName}:${it.lineNumber})"
        } else {
            if (it.fileName == "LogExtension.kt") isCurrent = true
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