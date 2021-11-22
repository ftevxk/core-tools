@file:Suppress("UNUSED_VARIABLE", "unused")

package com.ftevxk.core.extension

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.ftevxk.core.fragment.ProxyFragment
import java.io.Serializable

/*****************************************************
 * 简化权限请求
 *****************************************************/

fun FragmentActivity.requestPermissions(vararg permissions: String,
                                        permissionResult: (grantPermissions: List<String>,
                                                           deniedPermissions: List<String>) -> Unit) {
    ProxyFragment.newPermissionsInstance(supportFragmentManager,
            attach = { fragment, code -> fragment.requestPermissions(permissions, code) },
            result = permissionResult)
}

fun Fragment.requestPermissions(vararg permissions: String,
                                permissionResult: (grantPermissions: List<String>,
                                                   deniedPermissions: List<String>) -> Unit) {
    ProxyFragment.newPermissionsInstance(childFragmentManager,
            attach = { fragment, code -> fragment.requestPermissions(permissions, code) },
            result = permissionResult)
}

/*****************************************************
 * 简化startActivityForResult
 *****************************************************/

fun FragmentActivity.finishResult(vararg params: Pair<String, Any?>) {
    val intent = Intent(this, javaClass).apply {
        if (params.isNotEmpty()) fillIntentArguments(this, params)
    }
    setResult(Activity.RESULT_OK, intent)
    finish()
}

fun FragmentActivity.startActivityForResult(intent: Intent, result: (Intent?) -> Unit) {
    ProxyFragment.newResultInstance(supportFragmentManager,
            attach = { _, code -> startActivityForResult(intent, code) },
            result = { result.invoke(it) }
    )
}

inline fun <reified T : Activity> FragmentActivity.startActivityForResult(
        vararg params: Pair<String, Any?>, noinline result: (Intent?) -> Unit) {
    val intent = Intent(this, T::class.java).apply {
        if (params.isNotEmpty()) fillIntentArguments(this, params)
    }
    startActivityForResult(intent, result)
}

fun Fragment.startActivityForResult(intent: Intent, result: (Intent?) -> Unit) {
    ProxyFragment.newResultInstance(childFragmentManager,
            attach = { _, code -> startActivityForResult(intent, code) },
            result = { result.invoke(it) }
    )
}

inline fun <reified T : Activity> Fragment.startActivityForResult(
        vararg params: Pair<String, Any?>, noinline result: (Intent?) -> Unit) {
    val intent = Intent(activity, T::class.java).apply {
        if (params.isNotEmpty()) fillIntentArguments(this, params)
    }
    startActivityForResult(intent, result)
}


fun fillIntentArguments(intent: Intent, params: Array<out Pair<String, Any?>>) {
    params.forEach {
        when (val value = it.second) {
            null -> intent.putExtra(it.first, null as Serializable?)
            is Int -> intent.putExtra(it.first, value)
            is Long -> intent.putExtra(it.first, value)
            is CharSequence -> intent.putExtra(it.first, value)
            is String -> intent.putExtra(it.first, value)
            is Float -> intent.putExtra(it.first, value)
            is Double -> intent.putExtra(it.first, value)
            is Char -> intent.putExtra(it.first, value)
            is Short -> intent.putExtra(it.first, value)
            is Boolean -> intent.putExtra(it.first, value)
            is Serializable -> intent.putExtra(it.first, value)
            is Bundle -> intent.putExtra(it.first, value)
            is Parcelable -> intent.putExtra(it.first, value)
            is Array<*> -> when {
                value.isArrayOf<CharSequence>() -> intent.putExtra(it.first, value)
                value.isArrayOf<String>() -> intent.putExtra(it.first, value)
                value.isArrayOf<Parcelable>() -> intent.putExtra(it.first, value)
                else -> throw Exception("Intent extra ${it.first} has wrong type ${value.javaClass.name}")
            }
            is IntArray -> intent.putExtra(it.first, value)
            is LongArray -> intent.putExtra(it.first, value)
            is FloatArray -> intent.putExtra(it.first, value)
            is DoubleArray -> intent.putExtra(it.first, value)
            is CharArray -> intent.putExtra(it.first, value)
            is ShortArray -> intent.putExtra(it.first, value)
            is BooleanArray -> intent.putExtra(it.first, value)
            else -> throw Exception("Intent extra ${it.first} has wrong type ${value.javaClass.name}")
        }
        return@forEach
    }
}