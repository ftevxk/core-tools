package cn.ftevxk.base.extension

import android.util.Log

fun Any.vLog(msg: String, tag: String = javaClass.simpleName) {
    val stack = Throwable().stackTrace[2]
    val logLine = "LogLine = ${stack.methodName}(${stack.fileName}:${stack.lineNumber})\n"
    val logMsg = "LogMsg = $msg"
    Log.v(tag, "$logLine$logMsg")
}

fun Any.dLog(msg: String, tag: String = javaClass.simpleName) {
    val stack = Throwable().stackTrace[2]
    val logLine = "LogLine = ${stack.methodName}(${stack.fileName}:${stack.lineNumber})\n"
    val logMsg = "LogMsg = $msg"
    Log.d(tag, "$logLine$logMsg")
}

fun Any.iLog(msg: String, tag: String = javaClass.simpleName) {
    val stack = Throwable().stackTrace[2]
    val logLine = "LogLine = ${stack.methodName}(${stack.fileName}:${stack.lineNumber})\n"
    val logMsg = "LogMsg = $msg"
    Log.i(tag, "$logLine$logMsg")
}

fun Any.wLog(msg: String, tag: String = javaClass.simpleName) {
    val stack = Throwable().stackTrace[2]
    val logLine = "LogLine = ${stack.methodName}(${stack.fileName}:${stack.lineNumber})\n"
    val logMsg = "LogMsg = $msg"
    Log.w(tag, "$logLine$logMsg")
}

fun Any.eLog(msg: String, tag: String = javaClass.simpleName) {
    val stack = Throwable().stackTrace[2]
    val logLine = "LogLine = ${stack.methodName}(${stack.fileName}:${stack.lineNumber})\n"
    val logMsg = "LogMsg = $msg"
    Log.e(tag, "$logLine$logMsg")
}