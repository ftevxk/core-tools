@file:Suppress("unused")

package com.ftevxk.core.extension

import android.util.Log

object LogExtension {
    //全局Log开关，releaseLog可保留release下的Log
    var isDebug = true

    //记录线程中调用行堆栈位置
    private var stackTraceIndex = -1

    /**
     * 获得Log调用行可高亮信息
     * @param stackTraceElements 传入自定义堆栈信息
     * @return 初始触发位置行信息
     */
    @JvmStatic
    fun getLineTag(stackTraceElements: Array<StackTraceElement>? = null): String {
        if (stackTraceElements == null) {
            var isCurrent = false
            val elements = Thread.currentThread().stackTrace
            if (stackTraceIndex > 0) {
                val stackTrace = elements[stackTraceIndex]
                return "${stackTrace.methodName}(${stackTrace.fileName}:${stackTrace.lineNumber})"
            } else {
                elements.forEachIndexed { index, stackTrace ->
                    if (isCurrent && !stackTrace.fileName.startsWith(javaClass.simpleName)) {
                        stackTraceIndex = index
                        return "${stackTrace.methodName}(${stackTrace.fileName}:${stackTrace.lineNumber})"
                    } else {
                        if (stackTrace.fileName.startsWith(javaClass.simpleName)) isCurrent = true
                    }
                }
            }
            return javaClass.simpleName
        } else {
            var lineInfo = Pair("", javaClass.simpleName)
            stackTraceElements.forEach { stackTrace ->
                if (stackTrace.fileName.startsWith(javaClass.simpleName)) {
                    return lineInfo.second
                } else {
                    if (lineInfo.first != stackTrace.fileName) {
                        lineInfo = Pair(stackTrace.fileName,
                            "${stackTrace.methodName}(${stackTrace.fileName}:${stackTrace.lineNumber})")
                    }
                }
            }
            return lineInfo.second
        }
    }
}

fun Any.vLog(msg: Any?, releaseLog: Boolean = false,
             tag: String = LogExtension.getLineTag()) {
    if (LogExtension.isDebug || releaseLog) {
        getLogMsgStr(msg) { Log.v(tag, it) }
    }
}

fun Any.dLog(msg: Any?, releaseLog: Boolean = false,
             tag: String = LogExtension.getLineTag()) {
    if (LogExtension.isDebug || releaseLog) {
        getLogMsgStr(msg) { Log.d(tag, it) }
    }
}

fun Any.iLog(msg: Any?, releaseLog: Boolean = false,
             tag: String = LogExtension.getLineTag()) {
    if (LogExtension.isDebug || releaseLog) {
        getLogMsgStr(msg) { Log.i(tag, it) }
    }
}

fun Any.wLog(msg: Any?, releaseLog: Boolean = false,
             tag: String = LogExtension.getLineTag()) {
    if (LogExtension.isDebug || releaseLog) {
        getLogMsgStr(msg) { Log.w(tag, it) }
    }
}

/**
 * 默认releaseLog为true
 */
fun Any.eLog(msg: Any?, releaseLog: Boolean = true,
             tag: String = LogExtension.getLineTag()) {
    if (LogExtension.isDebug || releaseLog) {
        getLogMsgStr(msg) { Log.e(tag, it) }
    }
}

/**
 * 封装try-catch，当捕获异常时打印异常类型及错误位置
 */
fun Any.tryCatchLog(run: () -> Unit,
                    catch: ((Throwable) -> Unit)? = null,
                    finally: (() -> Unit)? = null,
                    releaseLog: Boolean = true) {
    try {
        run.invoke()
    } catch (t: Throwable) {
        eLog(Log.getStackTraceString(t), releaseLog = releaseLog,
            tag = LogExtension.getLineTag(t.stackTrace))
        catch?.invoke(t)
    } finally {
        finally?.invoke()
    }
}

/**
 * 当Log的msg为null或空字符串时额外处理返回
 */
private fun Any.getLogMsgStr(msg: Any?, result: (String) -> Unit) {
    if (msg == null) {
        result.invoke("LogExtensionMsg: this msg is null!!!")
    } else {
        val msgStr = msg.toString()
        if (msgStr.isEmpty()) {
            result.invoke("LogExtensionMsg: this msg is empty string!!!")
        } else {
            msgStr.splitStr().forEach { result.invoke(it) }
        }
    }
}