package com.ftevxk.core.extension

import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*

fun tryCatchLaunchByScope(
        block: suspend CoroutineScope.() -> Unit,
        catch: ((t: Throwable) -> Unit)? = null,
        finally: (() -> Unit)? = null,
        jobResult: ((job: Job) -> Unit)? = null,
        lifecycleScope: LifecycleCoroutineScope? = null,
        logTag: String = "LoggerDebug"
) {
    val job = (lifecycleScope ?: CoroutineScope(SupervisorJob()))
            .launch {
                withContext(Dispatchers.Main) {
                    try {
                        block.invoke(this)
                    } catch (t: Throwable) {
                        catchError(t, catch, logTag)
                    } finally {
                        finally?.invoke()
                    }
                }
                CoroutineExceptionHandler { _, throwable ->
                    catchError(throwable, catch)
                }
            }
    jobResult?.invoke(job)
}

private fun catchError(t: Throwable, catch: ((t: Throwable) -> Unit)?,
                       logTag: String = "LoggerDebug") {
    tryCatchLog {
        eLog(t.message ?: "", tag = logTag)
        catch?.invoke(t)
    }
}

/************************************常规异常捕获*******************************************/

/**
 * 带Log的异常捕获
 */
fun tryCatchLog(
        run: () -> Unit,
        catch: ((t: Throwable) -> Unit)? = null,
        finally: (() -> Unit)? = null,
        logTag: String = "LoggerDebug"
) {
    try {
        run.invoke()
    } catch (t: Throwable) {
        catchError(t, catch, logTag)
    } finally {
        finally?.invoke()
    }
}

fun tryCatchLog(run: () -> Unit) {
    tryCatchLog(run, null, null)
}

/************************************协程异常捕获*******************************************/

/**
 * 协程带Log的异常捕获
 */
fun tryCatchLaunch(
        block: suspend CoroutineScope.() -> Unit,
        catch: ((t: Throwable) -> Unit)? = null,
        finally: (() -> Unit)? = null,
        result: ((Job) -> Unit)? = null,
) {
    tryCatchLaunchByScope(block, catch, finally, result)
}

fun tryCatchLaunch(block: suspend CoroutineScope.() -> Unit) {
    return tryCatchLaunch(block, null, null, null)
}

/**
 * 感知Activity/Fragment生命周期，协程带Log的异常捕获
 */
fun LifecycleOwner.lifeTryCatchLaunch(
        block: suspend CoroutineScope.() -> Unit,
        catch: ((t: Throwable) -> Unit)? = null,
        finally: (() -> Unit)? = null,
        result: ((Job) -> Unit)? = null,
) {
    tryCatchLaunchByScope(block, catch, finally, result, lifecycleScope)
}

fun LifecycleOwner.lifeTryCatchLaunch(block: suspend CoroutineScope.() -> Unit) {
    return lifeTryCatchLaunch(block, null, null, null)
}