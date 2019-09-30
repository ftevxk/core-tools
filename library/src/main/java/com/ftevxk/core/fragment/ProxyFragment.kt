@file:Suppress("ValidFragment", "unused")

package com.ftevxk.core.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

/**
 * 代理返回onActivityResult、onRequestPermissionsResult
 */
@PublishedApi
internal class ProxyFragment @SuppressLint("ValidFragment")
private constructor(private val attach: (ProxyFragment, Int) -> Unit,
                    private val activityResult: (ProxyFragment, Intent?) -> Unit,
                    private val permissionResult: (ProxyFragment, List<String>, List<String>) -> Unit) : Fragment() {

    private val mRequestCode = View.generateViewId()

    companion object {
        /**
         * 创建一个onActivityResult的ProxyFragment实例
         */
        fun newResultInstance(fragmentManager: FragmentManager,
                              attach: (ProxyFragment, requestCode: Int) -> Unit,
                              result: (Intent?) -> Unit): ProxyFragment {
            val fragment = ProxyFragment(
                    attach = attach,
                    activityResult = { fragment, it ->
                        result.invoke(it)
                        fragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss()
                    },
                    permissionResult = { _, _, _ -> })
            fragmentManager.beginTransaction().add(0, fragment).commitAllowingStateLoss()
            return fragment
        }

        /**
         * 创建一个onRequestPermissionsResult的ProxyFragment实例
         */
        fun newPermissionsInstance(fragmentManager: FragmentManager,
                                   attach: (ProxyFragment, requestCode: Int) -> Unit,
                                   result: (grantPermissions: List<String>,
                                            deniedPermissions: List<String>) -> Unit): ProxyFragment {
            val fragment = ProxyFragment(
                    attach = attach,
                    activityResult = { _, _ -> },
                    permissionResult = { fragment, grantPermissions, deniedPermissions ->
                        result.invoke(grantPermissions, deniedPermissions)
                        fragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss()
                    })
            fragmentManager.beginTransaction().add(0, fragment).commitAllowingStateLoss()
            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        attach.invoke(this, mRequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == mRequestCode) {
            activityResult.invoke(this, data)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == mRequestCode && permissions.isNotEmpty() && grantResults.isNotEmpty()) {
            val grantPermissions = mutableListOf<String>()
            val deniedPermissions = mutableListOf<String>()
            grantResults.forEachIndexed { index, result ->
                if (result == PackageManager.PERMISSION_GRANTED) {
                    grantPermissions.add(permissions[index])
                } else {
                    deniedPermissions.add(permissions[index])
                }
            }
            permissionResult.invoke(this, grantPermissions, deniedPermissions)
        }
    }
}