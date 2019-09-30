@file:Suppress("UNUSED_VARIABLE", "unused")

package com.ftevxk.core.extension

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.ftevxk.core.fragment.ProxyFragment
import org.jetbrains.anko.internals.AnkoInternals

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
    val intent = AnkoInternals.createIntent(this, javaClass, params)
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
    val intent = AnkoInternals.createIntent(this, T::class.java, params)
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
    val intent = AnkoInternals.createIntent(activity!!, T::class.java, params)
    startActivityForResult(intent, result)
}